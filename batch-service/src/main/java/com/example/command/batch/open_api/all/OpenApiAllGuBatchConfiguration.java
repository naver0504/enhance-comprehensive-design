package com.example.command.batch.open_api.all;

import com.example.command.adapter.repository.apart.QuerydslApartmentTransaction;
import com.example.command.adapter.repository.dong.DongRepository;
import com.example.command.adapter.repository.interest.InterestRepository;
import com.example.command.api_client.kakao.KaKaoApiClientWithJibun;
import com.example.command.api_client.open_api.OpenApiClient;
import com.example.command.api_client.predict.dto.ApartmentBatchQuery;
import com.example.command.batch.kakao_map.jibun.KaKaoMapBatchConfigurationWithJibun;
import com.example.command.batch.open_api.OpenApiBaseBatchConfiguration;
import com.example.command.batch.open_api.dto.ApartmentDetailResponseWithGu;
import com.example.command.batch.predict_cost.PredictCostBatchConfiguration;
import com.example.command.batch.publish_event.create.CreateNewTransactionBatchConfiguration;
import com.example.command.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.command.batch.query_dsl.expression.Expression;
import com.example.command.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.predict_cost.PredictCost;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;

import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.dongEntity;
import static com.example.command.domain.interest.QInterest.interest;
import static com.example.command.domain.predict_cost.QPredictCost.*;

@Configuration
@Import({OpenApiBaseBatchConfiguration.class, KaKaoMapBatchConfigurationWithJibun.class, PredictCostBatchConfiguration.class, CreateNewTransactionBatchConfiguration.class})
public class OpenApiAllGuBatchConfiguration {

    private static final String JOB_NAME = "allGuOpenApiJob";
    private static final String STEP_NAME = JOB_NAME + "Step";
    private static final int CHUNK_SIZE = 1;
    private static final int NUM_OF_ROWS = 1000;

    private static final int SECOND_CHUNK_SIZE = 1000;

    private final OpenApiClient openApiClient;
    private final PlatformTransactionManager platformTransactionManager;
    private final KaKaoApiClientWithJibun kaKaoApiClientWithJibun;
    private final InterestRepository interestRepository;
    private final DongRepository dongRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final ItemProcessor<ApartmentBatchQuery, PredictCost> predictCostProcessor;
    private final JdbcBatchItemWriter<PredictCost> predictCostJdbcBatchItemWriter;
    private final Step createNewTransactionEventStep;
    private final QuerydslApartmentTransaction querydslApartmentTransaction;
    private final JdbcTemplate jdbcTemplate;

    public OpenApiAllGuBatchConfiguration(OpenApiClient openApiClient,
                                          PlatformTransactionManager platformTransactionManager,
                                          KaKaoApiClientWithJibun kaKaoApiClientWithJibun,
                                          InterestRepository interestRepository,
                                          DongRepository dongRepository,
                                          EntityManagerFactory entityManagerFactory,
                                          ItemProcessor<ApartmentBatchQuery, PredictCost> predictCostProcessor,
                                          JdbcBatchItemWriter<PredictCost> predictCostJdbcBatchItemWriter,
                                          @Qualifier(value = CreateNewTransactionBatchConfiguration.STEP_NAME) Step createNewTransactionEventStep,
                                          QuerydslApartmentTransaction querydslApartmentTransaction, JdbcTemplate jdbcTemplate) {
        this.openApiClient = openApiClient;
        this.platformTransactionManager = platformTransactionManager;
        this.kaKaoApiClientWithJibun = kaKaoApiClientWithJibun;
        this.interestRepository = interestRepository;
        this.dongRepository = dongRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.predictCostProcessor = predictCostProcessor;
        this.predictCostJdbcBatchItemWriter = predictCostJdbcBatchItemWriter;
        this.createNewTransactionEventStep = createNewTransactionEventStep;
        this.querydslApartmentTransaction = querydslApartmentTransaction;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean(name = JOB_NAME)
    public Job openApiAllGuJob(JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(openApiAllGuStep(jobRepository, platformTransactionManager))
                .next(openApiAllGuPredictCostStep(jobRepository))
                .next(createNewTransactionEventStep)
                .build();
    }

    @Bean(name = STEP_NAME)
    public Step openApiAllGuStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ApartmentDetailResponseWithGu, ApartmentDetailResponseWithGu>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(openApiAllGuBatchReader())
                .writer(openApiAllJdbcWriter())
                .build();
    }

    @Bean(name = STEP_NAME + "_PredictCost")
    public Step openApiAllGuPredictCostStep(JobRepository jobRepository) {
        return new StepBuilder(STEP_NAME + "_PredictCost", jobRepository)
                .<ApartmentBatchQuery, PredictCost>chunk(SECOND_CHUNK_SIZE, platformTransactionManager)
                .reader(apartmentTransactionQuerydslNoOffsetIdPagingItemReader(null))
                .processor(predictCostProcessor)
                .writer(predictCostJdbcBatchItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ExistTransactionChecker existTransactionChecker() {
        return new ExistTransactionChecker(querydslApartmentTransaction);
    }

    @Bean
    @StepScope
    public OpenApiAllGuDataHolder openApiAllGuDataHolder() {
        return new OpenApiAllGuDataHolder(dongRepository);
    }

    @Bean
    @StepScope
    public InterestDataHolder interestDataHolder() {
        return new InterestDataHolder(interestRepository);
    }
    @Bean
    @StepScope
    public OpenApiAllGuBatchReader openApiAllGuBatchReader() {
        return new OpenApiAllGuBatchReader(openApiClient, NUM_OF_ROWS);
    }

    @Bean
    @StepScope
    public OpenApiAllJdbcWriter openApiAllJdbcWriter() {
        return new OpenApiAllJdbcWriter(interestDataHolder(), openApiAllGuDataHolder(), existTransactionChecker(), jdbcTemplate);
    }

    @Bean(name = STEP_NAME + "_QuerydslReader")
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<ApartmentBatchQuery, Long> apartmentTransactionQuerydslNoOffsetIdPagingItemReader(@Value("#{jobParameters[lastId]}") Long lastId) {

        QuerydslNoOffsetNumberOptions<ApartmentBatchQuery, Long> options = new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);

        return new QuerydslNoOffsetIdPagingItemReader<>(entityManagerFactory, SECOND_CHUNK_SIZE, options, query -> query
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
                .where(apartmentTransaction.id.gt(lastId))
        );
    }
}
