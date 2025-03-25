package com.example.command.batch.kakao_map.road_name;

import com.example.command.batch.CacheRepository;
import com.example.command.api_client.kakao.KaKaoApiClientWithRoadName;
import com.example.command.batch.kakao_map.KaKaoMapBaseConfiguration;
import com.example.command.api_client.kakao.KaKaoRestApiProperties;
import com.example.command.api_client.ApiClient;
import com.example.command.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.command.batch.kakao_map.dto.LocationRecord;
import com.example.command.domain.apartment.ApartmentTransaction;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Future;

/***
 *
 * @deprecated
 * Because the road name address is not used in the current system.
 * Use {@link com.example.command.batch.kakao_map.jibun.KaKaoMapBatchConfigurationWithJibun} instead.
 */
@Configuration
@EnableConfigurationProperties(KaKaoRestApiProperties.class)
@Slf4j
@Import(KaKaoMapBaseConfiguration.class)
@Deprecated
public class KaKaoMapBatchConfigurationWithRoadName {

    private static final String JOB_NAME = "kakaoMapJobWithRoadName";
    private static final String STEP_NAME = JOB_NAME + "_step";
    private static final int CHUNK_SIZE = 1000;

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final KaKaoRestApiProperties kaKaoRestApiProperties;

    private final ItemWriter<Future<ApartmentGeoRecord>> kaKaoMapWriter;
    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> cacheRepository;
    private final TaskExecutor taskExecutor;


    public KaKaoMapBatchConfigurationWithRoadName(EntityManagerFactory emf,
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
    public KaKaoApiClientWithRoadName kaKaoApiClient() {
        return new KaKaoApiClientWithRoadName(kaKaoRestApiProperties, restTemplate, cacheRepository);
    }

    @Bean(name = STEP_NAME + " Reader")
    @StepScope
    public JpaPagingItemReader<ApartmentTransaction> jpaApartmentTransactionReader(@Value("#{jobParameters[regionalCode]}") String regionalCode) {
        JpaPagingItemReader<ApartmentTransaction> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT a FROM ApartmentTransaction a join DongEntity d on a.dongEntity.id = d.id where d.guCode = :regionalCode order by a.id asc");
        reader.setParameterValues(Map.of("regionalCode", regionalCode));
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @Bean(name = STEP_NAME + " Processor")
    @StepScope
    public ItemProcessor<ApartmentTransaction, ApartmentGeoRecord> kaKaoMapProcessor(ApiClient<ApartmentTransaction, ApartmentGeoRecord> kaKaoApiClient) {
        return kaKaoApiClient::callApi;
    }

    @Bean(name = STEP_NAME + " AsyncProcessor")
    @StepScope
    public AsyncItemProcessor<ApartmentTransaction, ApartmentGeoRecord> asyncKaKaoMapProcessor() {
        AsyncItemProcessor<ApartmentTransaction, ApartmentGeoRecord> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(kaKaoMapProcessor(kaKaoApiClient()));
        asyncItemProcessor.setTaskExecutor(taskExecutor);
        return asyncItemProcessor;
    }

    @Bean(name = STEP_NAME)
    public Step kaKaoMapStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ApartmentTransaction, Future<ApartmentGeoRecord>>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(jpaApartmentTransactionReader(null))
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
