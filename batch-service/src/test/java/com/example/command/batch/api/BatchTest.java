package com.example.command.batch.api;

import com.example.command.batch.BatchTag;
import com.example.command.config.JdbcTemplateConfig;
import com.example.command.batch.open_api.simple.OpenApiBatchConfiguration;
import com.example.command.api_client.open_api.OpenApiClient;
import com.example.command.domain.dong.Gu;
import com.example.command.adapter.repository.apart.ApartmentTransactionRepository;
import com.example.command.adapter.repository.dong.DongRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.YearMonth;

@SpringBootTest
@ComponentScan(basePackageClasses = {OpenApiBatchConfiguration.class, DongRepository.class})
@Import({OpenApiClient.class, TestBatchConfig.class, JdbcTemplateConfig.class})
@SpringBatchTest
@BatchTag
@ActiveProfiles("test")
public class BatchTest  {


    @Autowired
    private ApartmentTransactionRepository apartmentTransactionRepository;
    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BeanFactory beanFactory;

    @BeforeEach
    void tearDown() {
//        apartmentTransactionRepository.deleteAll();
    }


    @Test
    void testJob() throws Exception {

        Job job = beanFactory.getBean("simpleOpenApiJob", Job.class);

        // 2min 6sec
        // 54.577 sec

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        // 강서 진행중 // 마포, 송파, 서초는 이미 함
        jobParametersBuilder.addString("regionalCode", Gu.중랑구.getRegionalCode());
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Check if the job execution is successful
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void testExcelJob() throws Exception {
        Job job = beanFactory.getBean("create_transaction_event_job", Job.class);


        jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job)
                .toJobParameters());

        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void allGuTest() throws Exception {
        Job job = beanFactory.getBean("allGuOpenApiJob", Job.class);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        YearMonth from = YearMonth.from(LocalDate.now().minusMonths(1));
        String format = from.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        jobParametersBuilder.addString("contractDate", format);
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    }

    @Test
    void predictJobTest() throws Exception {
        Job job = beanFactory.getBean("predictCostJob", Job.class);

        jobLauncherTestUtils.setJob(job);
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job)
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    }
}
