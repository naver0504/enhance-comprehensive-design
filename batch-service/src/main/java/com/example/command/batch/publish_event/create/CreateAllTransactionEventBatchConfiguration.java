package com.example.command.batch.publish_event.create;

import com.example.command.batch.publish_event.CustomKafkaItemWriter;
import com.example.command.batch.publish_event.EventItemRecord;
import com.example.command.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.command.batch.query_dsl.expression.Expression;
import com.example.command.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
import com.example.command.kafka.config.KafkaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Projections;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;


import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.dongEntity;
import static com.example.command.domain.predict_cost.QPredictCost.predictCost;

@Configuration
@RequiredArgsConstructor
public class CreateAllTransactionEventBatchConfiguration {

    private static final int CHUNK_SIZE = 1000;
    private static final String JOB_NAME = "create_transaction_event_job";
    private static final String STEP_NAME = JOB_NAME + "_step";

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final KafkaTemplate<Long, EventItemRecord> kafkaTemplate;


    @Bean(name = JOB_NAME)
    public Job createAllTransactionEventJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(createAllTransactionEventStep())
                .build();
    }

    @Bean(name = STEP_NAME)
    public Step createAllTransactionEventStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<CreateTransactionRecord, CreateTransactionRecord>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(apartmentTransactionQuerydslNoOffsetIdPagingItemReader())
                .writer(createTransactionEventItemWriter())
                .build();

    }


    @Bean(name = STEP_NAME + "_QuerydslReader")
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<CreateTransactionRecord, Long> apartmentTransactionQuerydslNoOffsetIdPagingItemReader() {

        QuerydslNoOffsetNumberOptions<CreateTransactionRecord, Long> options = new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);

        return new QuerydslNoOffsetIdPagingItemReader<>(emf, CHUNK_SIZE, options, query -> query
                .select(Projections.constructor(
                        CreateTransactionRecord.class,
                        apartmentTransaction.id,
                        apartmentTransaction,
                        dongEntity,
                        predictCost
                ))
                .from(apartmentTransaction)
                .leftJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .leftJoin(predictCost).on(apartmentTransaction.id.eq(predictCost.apartmentTransaction.id))
        );
    }

    @Bean(name = STEP_NAME + "_Writer")
    @StepScope
    public CustomKafkaItemWriter<Long, EventItemRecord> createTransactionEventItemWriter() {
        CustomKafkaItemWriter<Long, EventItemRecord> kafkaItemWriter = new CustomKafkaItemWriter<>();
        kafkaItemWriter.setKafkaTemplate(kafkaTemplate);
        kafkaItemWriter.setItemKeyMapper(EventItemRecord::getPartitionKey);
        kafkaItemWriter.setTopic(KafkaProperties.CREATE_TRANSACTION_TOPIC);
        kafkaItemWriter.setTimeout(1500);
        return kafkaItemWriter;
    }
}
