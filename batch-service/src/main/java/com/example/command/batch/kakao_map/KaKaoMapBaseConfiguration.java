package com.example.command.batch.kakao_map;

import com.example.command.batch.CacheRepository;
import com.example.command.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.command.batch.kakao_map.dto.LocationRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Future;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KaKaoMapBaseConfiguration {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    @StepScope
    public CacheRepository<String, LocationRecord> cacheRepository() {
        return new LocationRecordCacheRepository();
    }

    //queue 가 꽉 차야지 maxPoolSize 만큼 thread 를 생성한다.
    //하지만 기본 값이 Integer.MAX_VALUE 이기 때문에 queue 가 꽉 차지 않는 한 maxPoolSize 만큼 thread 를 생성하지 않는다.
    //적절한 queueCapacity 를 설정해야 하지만 단순한 작업이기 때문에
    //corePoolSize만 사용하도록 설정한다.
    @Bean(name = "kakaoMapTaskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        // size 6일시 메모리 사용량 95퍼 까지 상승
        threadPoolTaskExecutor.setCorePoolSize(4);

        //corePoolSize 만큼 thread 를 미리 생성한다.
        //threadPoolTaskExecutor.setPrestartAllCoreThreads(true);

        //corePoolSize 만큼 thread 를 미리 생성하지 않는걸로 수정
        //싱글톤 빈으로 등록되어 있기 때문에 미리 생성하지 않는다.
        threadPoolTaskExecutor.setPrestartAllCoreThreads(false);
        //true 설정시 어플리케이션 종료 요청시 queue에 남아 있는 모든 작업들이 완료될 때까지 기다린 후 종료된다.
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setThreadNamePrefix("kakaoMapTaskExecutor-");
        return threadPoolTaskExecutor;
    }


    @Bean
    @StepScope
    public ItemWriter<Future<ApartmentGeoRecord>> kaKaoMapWriter() {


        return futures -> {

            List<ApartmentGeoRecord> apartmentGeoRecords = futures.getItems().stream().map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    log.error("Error while getting future", e);
                    throw new IllegalStateException(e);
                }
            }).filter(ApartmentGeoRecord::isNotEmpty).toList();

            log.info("start idx : {}", apartmentGeoRecords.get(0).id());

            jdbcTemplate.batchUpdate("""
                    update apartment_transaction set geography = ST_GeomFromText(?) where id = ?
                    """.trim(), apartmentGeoRecords, apartmentGeoRecords.size(), (ps, record) -> {
                ps.setString(1, record.toPoint());
                ps.setLong(2, record.id());
            });
        };
    }
}
