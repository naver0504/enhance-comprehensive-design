package com.example.command.scheduler;

import com.example.command.adapter.repository.apart.ApartmentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
@EnableScheduling
public class UpdateTransactionScheduler {

    private final BeanFactory beanFactory;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final ApartmentTransactionRepository apartmentTransactionRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

    // 매달 1일 5시에
    @Scheduled(cron = "0 0 5 * * *")
    public void createNewTransaction() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        Job allGuOpenApiJob = beanFactory.getBean("allGuOpenApiJob", Job.class);

        Long lastId = apartmentTransactionRepository.findLastId();
        LocalDate contractDate = LocalDate.now().minusDays(1);

        JobParameters allGuOpenApiJobParameters = new JobParametersBuilder(jobExplorer)
                .addLong("lastId", lastId)
                .addJobParameter("contractDate", contractDate, LocalDate.class)
                .toJobParameters();

        jobLauncher.run(allGuOpenApiJob, allGuOpenApiJobParameters);
    }


    @PostMapping("/update-predict-cost")
    public void updatePredictCost() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job predictCostJob = beanFactory.getBean("predictCostJob", Job.class);

        JobParameters predictCostJobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(predictCostJob)
                .toJobParameters();

        jobLauncher.run(predictCostJob, predictCostJobParameters);

        Job updateTransactionEventJob = beanFactory.getBean("update_transaction_event_job", Job.class);
        JobParameters updateTrnasctionEventJobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(updateTransactionEventJob)
                .toJobParameters();
        jobLauncher.run(updateTransactionEventJob, updateTrnasctionEventJobParameters);
    }
}
