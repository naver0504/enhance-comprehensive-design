package com.example.command.batch.predict_cost;

import com.example.command.adapter.repository.predict_cost.QuerydslPredictCostRepository;
import com.example.command.api_client.ApiClient;
import com.example.command.api_client.predict.PredictAiProperties;
import com.example.command.api_client.predict.PredictApiClientForBatch;
import com.example.command.api_client.predict.dto.ApartmentBatchQuery;
import com.example.command.batch.publish_event.update.UpdateTransactionBatchConfiguration;
import com.example.command.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.command.batch.query_dsl.expression.Expression;
import com.example.command.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
import com.example.command.domain.predict_cost.PredictCost;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.dongEntity;
import static com.example.command.domain.interest.QInterest.interest;


@Configuration
@EnableConfigurationProperties(PredictAiProperties.class)
@Import(UpdateTransactionBatchConfiguration.class)
public class PredictCostBatchConfiguration {

    private static final int CHUNK_SIZE = 1000;
    private static final int SECOND_CHUNK_SIZE = 100;
    private static final String JOB_NAME = "predictCostJob";
    private static final String STEP_NAME = JOB_NAME + "_step";

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final PredictAiProperties predictAiProperties;
    private final RestTemplate restTemplate;
    private final QuerydslPredictCostRepository querydslPredictCostRepository;
    private final Step produceUpdateTransactionStep;

    public PredictCostBatchConfiguration(EntityManagerFactory emf,
                                         JobRepository jobRepository,
                                         PlatformTransactionManager platformTransactionManager,
                                         DataSource dataSource, PredictAiProperties predictAiProperties,
                                         RestTemplate restTemplate,
                                         QuerydslPredictCostRepository querydslPredictCostRepository,
                                         @Qualifier(UpdateTransactionBatchConfiguration.STEP_NAME) Step produceUpdateTransactionStep) {
        this.emf = emf;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.dataSource = dataSource;
        this.predictAiProperties = predictAiProperties;
        this.restTemplate = restTemplate;
        this.querydslPredictCostRepository = querydslPredictCostRepository;
        this.produceUpdateTransactionStep = produceUpdateTransactionStep;
    }

    @Bean
    public Job predictCostJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(updatePredictCostToNotRecentStep())
                .next(insertPredictCostStep())
                .next(produceUpdateTransactionStep)
                .end()
                .build();
    }

    @Bean
    public Step updatePredictCostToNotRecentStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(jpaPagingPredictCostReader())
                .writer(predictCostJpaItemWriter())
                .build();
    }

    @Bean(name = STEP_NAME + "_jpaPagingItemReader")
    @StepScope
    public JpaPagingItemReader<Long> jpaPagingPredictCostReader() {

        JpaPagingItemReader<Long> jpaPagingItemReader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        jpaPagingItemReader.setEntityManagerFactory(emf);
        jpaPagingItemReader.setPageSize(CHUNK_SIZE);
        jpaPagingItemReader.setQueryString("SELECT p.id FROM predict_cost p WHERE p.predictStatus = 'RECENT' order by p.id");
        return jpaPagingItemReader;
    }

    @Bean
    @StepScope
    public ItemWriter<Long> predictCostJpaItemWriter() {
        return (items) ->
            querydslPredictCostRepository.updateStatusToNotRecent(items.getItems().stream().map(Long::valueOf).toList());
    }

    @Bean
    public Step insertPredictCostStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ApartmentBatchQuery, PredictCost>chunk(SECOND_CHUNK_SIZE, platformTransactionManager)
                .reader(apartmentTransactionQuerydslNoOffsetIdPagingItemReader())
                .processor(predictCostQueryItemProcessor(predictApiClient()))
                .writer(predictCostJdbcBatchItemWriter())
                .build();
    }

    @Bean(name = STEP_NAME + "_QuerydslReader")
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<ApartmentBatchQuery, Long> apartmentTransactionQuerydslNoOffsetIdPagingItemReader() {

        QuerydslNoOffsetNumberOptions<ApartmentBatchQuery, Long> options = new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);

        return new QuerydslNoOffsetIdPagingItemReader<>(emf, SECOND_CHUNK_SIZE, options, query -> query
                .select(Projections.constructor(
                        ApartmentBatchQuery.class,
                        apartmentTransaction.id,
                        interest.interestRate,
                        dongEntity.gu,
                        dongEntity.dongName,
                        apartmentTransaction.dealDate,
                        apartmentTransaction.dealAmount,
                        apartmentTransaction.areaForExclusiveUse,
                        apartmentTransaction.floor,
                        apartmentTransaction.buildYear
                ))
                .from(apartmentTransaction)
                .innerJoin(interest).on(apartmentTransaction.interest.id.eq(interest.id))
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
        );
    }

    @Bean(name = STEP_NAME + "_ApiClient")
    @StepScope
    public ApiClient<ApartmentBatchQuery, PredictCost> predictApiClient() {
        return new PredictApiClientForBatch(predictAiProperties, restTemplate);
    }

    @Bean(name = STEP_NAME + "_Processor")
    @StepScope
    public ItemProcessor<ApartmentBatchQuery, PredictCost> predictCostQueryItemProcessor(ApiClient<ApartmentBatchQuery, PredictCost> apiClient) {
        return apiClient::callApi;
    }

    @Bean(name = STEP_NAME + "_JdbcBatchItemWriter")
    @StepScope
    public JdbcBatchItemWriter<PredictCost> predictCostJdbcBatchItemWriter() {
        JdbcBatchItemWriter<PredictCost> predictCostJdbcBatchItemWriter = new JdbcBatchItemWriter<PredictCost>();
        predictCostJdbcBatchItemWriter.setDataSource(dataSource);
        predictCostJdbcBatchItemWriter.setSql("INSERT INTO predict_cost (predicted_cost, is_reliable, predict_status, apartment_transaction_id) VALUES (?, ?, ?, ?)");
        predictCostJdbcBatchItemWriter.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getPredictedCost());
            ps.setBoolean(2, item.isReliable());
            ps.setString(3, item.getPredictStatus().name());
            ps.setLong(4, item.getApartmentTransaction().getId());
        });
        return predictCostJdbcBatchItemWriter;
    }
}
