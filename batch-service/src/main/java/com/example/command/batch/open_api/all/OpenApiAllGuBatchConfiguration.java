package com.example.command.batch.open_api.all;

import com.example.command.adapter.repository.dong.DongRepository;
import com.example.command.adapter.repository.interest.InterestRepository;
import com.example.command.api_client.kakao.KaKaoApiClientWithJibun;
import com.example.command.api_client.open_api.OpenApiClient;
import com.example.command.api_client.predict.dto.ApartmentBatchQuery;
import com.example.command.batch.kakao_map.jibun.KaKaoMapBatchConfigurationWithJibun;
import com.example.command.batch.open_api.OpenApiBaseBatchConfiguration;
import com.example.command.batch.open_api.dto.ApartmentDetailResponseWithGu;
import com.example.command.batch.predict_cost.PredictCostBatchConfiguration;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.dongEntity;
import static com.example.command.domain.interest.QInterest.interest;
import static com.example.command.domain.predict_cost.QPredictCost.*;

@Configuration
@RequiredArgsConstructor
@Import({OpenApiBaseBatchConfiguration.class, KaKaoMapBatchConfigurationWithJibun.class, PredictCostBatchConfiguration.class})
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

    @Bean(name = JOB_NAME)
    public Job openApiAllGuJob(JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(openApiAllGuStep(jobRepository, platformTransactionManager))
                .next(openApiAllGuPredictCostStep(jobRepository))
                .build();
    }

    @Bean(name = STEP_NAME)
    public Step openApiAllGuStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ApartmentDetailResponseWithGu, List<ApartmentTransaction>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(openApiAllGuBatchReader())
                .processor(apartmentTransactionProcessor())
                .writer(jpaItemListWriter())
                .build();
    }

    @Bean(name = STEP_NAME + "_PredictCost")
    public Step openApiAllGuPredictCostStep(JobRepository jobRepository) {
        return new StepBuilder(STEP_NAME + "_PredictCost", jobRepository)
                .<ApartmentBatchQuery, PredictCost>chunk(SECOND_CHUNK_SIZE, platformTransactionManager)
                .reader(apartmentTransactionQuerydslNoOffsetIdPagingItemReader())
                .processor(predictCostProcessor)
                .writer(predictCostJdbcBatchItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ApartmentTransactionProcessor apartmentTransactionProcessor() {
        return new ApartmentTransactionProcessor(openApiAllGuDataHolder(), kaKaoApiClientWithJibun, interestDataHolder());
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
    public JpaItemListWriter<ApartmentTransaction> jpaItemListWriter() {
        JpaItemWriter<ApartmentTransaction> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

        return new JpaItemListWriter<>(jpaItemWriter);
    }

    @Bean(name = STEP_NAME + "_QuerydslReader")
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<ApartmentBatchQuery, Long> apartmentTransactionQuerydslNoOffsetIdPagingItemReader() {

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
                .leftJoin(predictCost).on(apartmentTransaction.id.eq(predictCost.apartmentTransaction.id))
                .where(predictCost.apartmentTransaction.id.isNull())
        );
    }
}
