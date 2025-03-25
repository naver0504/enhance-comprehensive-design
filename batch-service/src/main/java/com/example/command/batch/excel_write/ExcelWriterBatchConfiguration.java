package com.example.command.batch.excel_write;

import com.example.command.batch.excel_write.dto.ExcelOutputRecord;
import com.example.command.batch.excel_write.dto.ExcelQueryRecord;
import com.example.command.batch.query_dsl.QuerydslNoOffsetIdPagingItemReader;
import com.example.command.batch.query_dsl.expression.Expression;
import com.example.command.batch.query_dsl.options.QuerydslNoOffsetNumberOptions;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.example.command.domain.apartment.QApartmentTransaction.apartmentTransaction;
import static com.example.command.domain.dong.QDongEntity.dongEntity;
import static com.example.command.domain.interest.QInterest.interest;


@Configuration
@RequiredArgsConstructor
public class ExcelWriterBatchConfiguration {

    private static final String JOB_NAME = "excelWriterJob";
    private static final String STEP_NAME = JOB_NAME + "_step";
    private static final int chunkSize = 1000;
    private static final String DELIMITER = "|";

    private final EntityManagerFactory emf;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    @StepScope
    public QuerydslNoOffsetIdPagingItemReader<ExcelQueryRecord, Long> apartmentTransactionReader() {

        QuerydslNoOffsetNumberOptions<ExcelQueryRecord, Long> options =
                new QuerydslNoOffsetNumberOptions<>(apartmentTransaction.id, Expression.ASC);
        return new QuerydslNoOffsetIdPagingItemReader<>(emf, chunkSize, options, query -> query
                .select(Projections.constructor(ExcelQueryRecord.class,
                        apartmentTransaction.id,
                        apartmentTransaction,
                        dongEntity,
                        interest
                        ))
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.eq(dongEntity))
                .innerJoin(interest).on(apartmentTransaction.interest.eq(interest))
                );
    }

    @Bean
    @StepScope
    public ItemProcessor<ExcelQueryRecord, ExcelOutputRecord> excelProcessor() {
        return ExcelQueryRecord::toExcelOutputRecord;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ExcelOutputRecord> apartmentTransactionExcelWriter() {
        Field[] fields = ExcelOutputRecord.class.getDeclaredFields();
        List<String> fieldList = Arrays.stream(fields).map(Field::getName).toList();
        String[] fieldNames = fieldList.toArray(new String[0]);
        FlatFileItemWriter<ExcelOutputRecord> flatFileItemWriter = new FlatFileItemWriterBuilder<ExcelOutputRecord>()
                .name("apartmentTransactionTsvWriter")
                .resource(new FileSystemResource("output/apartment_transaction.txt"))
                .delimited()
                .delimiter(DELIMITER)
                .names(fieldNames)
                .shouldDeleteIfExists(true)
                .build();

        flatFileItemWriter.setHeaderCallback(writer -> writer.write(String.join(DELIMITER, fieldNames)));
        return flatFileItemWriter;
    }

    @Bean
    public Job excelWriterJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(excelWriterStep())
                .build();
    }

    @Bean
    public Step excelWriterStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<ExcelQueryRecord, ExcelOutputRecord>chunk(chunkSize, platformTransactionManager)
                .reader(apartmentTransactionReader())
                .processor(excelProcessor())
                .writer(apartmentTransactionExcelWriter())
                .build();
    }
}
