package com.example.command.batch.open_api.simple;

import com.example.command.api_client.open_api.OpenApiClient;
import com.example.command.batch.open_api.OpenApiBaseBatchConfiguration;
import com.example.command.api_client.open_api.OpenApiProperties;
import com.example.command.batch.open_api.dto.ApartmentDetailResponse;
import com.example.command.adapter.repository.dong.QuerydslDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenApiProperties.class)
@Import(OpenApiBaseBatchConfiguration.class)
public class OpenApiBatchConfiguration {

    private static final String JOB_NAME = "simpleOpenApiJob";
    private static final String STEP_NAME = JOB_NAME + "Step";
    private static final int CHUNK_SIZE = 1;
    private static final int NUM_OF_ROWS = OpenApiBaseBatchConfiguration.NUM_OF_ROWS;

    private final PlatformTransactionManager platformTransactionManager;
    private final QuerydslDongRepository querydslDongRepository;
    private final JdbcTemplate jdbcTemplate;
    private final OpenApiClient openApiClient;

    @Bean(name = JOB_NAME)
    public Job simpleOpenApiJob(JobRepository jobRepository) {
        return new JobBuilder("simpleOpenApiJob", jobRepository)
                .start(simpleOpenApiStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean(name = STEP_NAME)
    public Step simpleOpenApiStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("simpleOpenApiStep", jobRepository)
                .<ApartmentDetailResponse, ApartmentDetailResponse>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(simpleOpenApiReader())
                .writer(openApiJdbcWriter())
                .build();
    }

    @Bean
    @JobScope
    public OpenApiDongDataHolder openApiDongDataHolder() {
        return new OpenApiDongDataHolder(querydslDongRepository);
    }

    @Bean
    @StepScope
    public OpenApiBatchReader simpleOpenApiReader() {
        return new OpenApiBatchReader(openApiClient, NUM_OF_ROWS);
    }

    @Bean
    @StepScope
    public OpenApiJdbcWriter openApiJdbcWriter() {
        return new OpenApiJdbcWriter(openApiDongDataHolder(), jdbcTemplate);
    }
}
