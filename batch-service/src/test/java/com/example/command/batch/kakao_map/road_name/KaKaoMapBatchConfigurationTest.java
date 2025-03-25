package com.example.command.batch.kakao_map.road_name;

import com.example.command.domain.dong.Gu;
import com.example.command.batch.BatchTag;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
@BatchTag
class KaKaoMapBatchConfigurationTest   {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Test
    public void readerTest() throws Exception {


        Job job = beanFactory.getBean("kakaoMapJobWithRoadName", Job.class);
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("regionalCode", Gu.송파구.getRegionalCode());
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        jobLauncherTestUtils.setJob(job);
        jobLauncherTestUtils.launchJob(jobParameters);
    }


}