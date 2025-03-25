package com.example.command.batch.kakao_map.jibun;

import com.example.command.domain.dong.Gu;
import com.example.command.batch.CacheRepository;
import com.example.command.api_client.kakao.KaKaoApiClientWithJibun;
import com.example.command.batch.kakao_map.KaKaoMapBaseConfiguration;
import com.example.command.api_client.ApiClient;
import com.example.command.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.command.batch.kakao_map.dto.LocationRecord;
import com.example.command.api_client.kakao.KaKaoRestApiProperties;
import com.example.command.batch.kakao_map.dto.TransactionWithGu;
import com.example.command.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.command.batch.query_dsl.expression.Expression;
import com.example.command.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.dongEntity;


@Configuration
@EnableConfigurationProperties(KaKaoRestApiProperties.class)
@Import(KaKaoMapBaseConfiguration.class)
public class KaKaoMapBatchConfigurationWithJibun {

    private static final int CHUNK_SIZE = 1000;
    private static final String JOB_NAME = "kakaoMapJobWithJibun";
    private static final String STEP_NAME = JOB_NAME + "_step";

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final KaKaoRestApiProperties kaKaoRestApiProperties;

    private final ItemWriter<Future<ApartmentGeoRecord>> kaKaoMapWriter;
    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> cacheRepository;
    private final TaskExecutor taskExecutor;


    public KaKaoMapBatchConfigurationWithJibun(EntityManagerFactory emf,
                                               JobRepository jobRepository,
                                               PlatformTransactionManager platformTransactionManager,
                                               KaKaoRestApiProperties kaKaoRestApiProperties,
                                               ItemWriter<Future<ApartmentGeoRecord>> kaKaoMapWriter,
                                               RestTemplate restTemplate,
                                               CacheRepository<String, LocationRecord> cacheRepository,
                                               @Qualifier("kakaoMapTaskExecutor") TaskExecutor taskExecutor) {
        this.emf = emf;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.kaKaoRestApiProperties = kaKaoRestApiProperties;
        this.kaKaoMapWriter = kaKaoMapWriter;
        this.restTemplate = restTemplate;
        this.cacheRepository = cacheRepository;
        this.taskExecutor = taskExecutor;
    }

    @Bean(name = STEP_NAME + " ApiClient")
    @StepScope
    public KaKaoApiClientWithJibun kaKaoApiClient() {
        return new KaKaoApiClientWithJibun(kaKaoRestApiProperties, restTemplate, cacheRepository);
    }

    @Bean(name = STEP_NAME + " Reader")
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<TransactionWithGu, Long> querydslPagingItemReader(@Value("#{jobParameters[regionalCode]}") String regionalCode) {


        Gu gu = Gu.getGuFromRegionalCode(regionalCode);
        QuerydslNoOffsetNumberOptions<TransactionWithGu, Long> options =
                new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);

        return new QuerydslNoOffsetIdPagingItemReader<>(emf, CHUNK_SIZE, options, query -> query
                .select(Projections.constructor(
                        TransactionWithGu.class,
                        apartmentTransaction.id,
                        dongEntity.gu,
                        apartmentTransaction.dongEntity.dongName,
                        apartmentTransaction.jibun)
                )
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.eq(dongEntity))
                .where(dongEntity.gu.eq(gu), apartmentTransaction.geography.isNull())
        );
    }

    @Bean(name = STEP_NAME + " Processor")
    @StepScope
    public ItemProcessor<TransactionWithGu, ApartmentGeoRecord> kaKaoMapProcessor(ApiClient<TransactionWithGu, ApartmentGeoRecord> kaKaoApiClient) {
        return kaKaoApiClient::callApi;
    }

    @Bean(name = STEP_NAME + " AsyncProcessor")
    @StepScope
    public AsyncItemProcessor<TransactionWithGu, ApartmentGeoRecord> asyncKaKaoMapProcessor() {
        AsyncItemProcessor<TransactionWithGu, ApartmentGeoRecord> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(kaKaoMapProcessor(kaKaoApiClient()));
        asyncItemProcessor.setTaskExecutor(taskExecutor);
        return asyncItemProcessor;
    }


    @Bean(name = STEP_NAME)
    public Step kaKaoMapStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<TransactionWithGu, Future<ApartmentGeoRecord>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(querydslPagingItemReader(null))
                .processor(asyncKaKaoMapProcessor())
                .writer(kaKaoMapWriter)
                .build();
    }

    @Bean(name = JOB_NAME)
    public Job kaKaoMapJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(kaKaoMapStep())
                .build();
    }

}
