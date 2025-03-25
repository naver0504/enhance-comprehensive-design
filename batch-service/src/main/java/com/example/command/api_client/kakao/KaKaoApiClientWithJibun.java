package com.example.command.api_client.kakao;

import com.example.command.batch.CacheRepository;
import com.example.command.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.command.batch.kakao_map.dto.Documents;
import com.example.command.batch.kakao_map.dto.LocationRecord;
import com.example.command.batch.kakao_map.dto.TransactionWithGu;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class KaKaoApiClientWithJibun extends KaKaoApiClient<TransactionWithGu, ApartmentGeoRecord> {

    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> jibunCacheRepository;

    public KaKaoApiClientWithJibun(KaKaoRestApiProperties kaKaoRestApiProperties,
                                   RestTemplate restTemplate,
                                   CacheRepository<String, LocationRecord> jibunCacheRepository) {
        super(kaKaoRestApiProperties);
        this.restTemplate = restTemplate;
        this.jibunCacheRepository = jibunCacheRepository;
    }

    @Override
    public ApartmentGeoRecord callApi(TransactionWithGu transactionWithGu) {

        Optional<String> jibunAddress = transactionWithGu.getJibunAddress();
        LocationRecord jibunLocationRecord = jibunAddress.isPresent() ?
                jibunCacheRepository.computeIfAbsent(
                        jibunAddress.get(),
                        jibun -> {
                            Documents documents = restTemplate
                                    .exchange(
                                            createUrl(transactionWithGu),
                                            HttpMethod.GET,
                                            createHttpEntity(),
                                            Documents.class)
                                    .getBody();
                            return documents.toLocationRecord();
                        }) : LocationRecord.EMPTY;
        return jibunLocationRecord.toApartmentGeoRecord(transactionWithGu.id());    }

    @Override
    protected String getLocation(TransactionWithGu transactionWithGu) {
        return transactionWithGu.getResolvedJibunAddress();
    }
}
