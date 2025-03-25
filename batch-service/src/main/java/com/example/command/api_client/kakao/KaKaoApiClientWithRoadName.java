package com.example.command.api_client.kakao;

import com.example.command.batch.CacheRepository;
import com.example.command.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.command.batch.kakao_map.dto.LocationRecord;
import com.example.command.domain.apartment.ApartmentTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;


/***
 *
 * @deprecated
 * This class is deprecated and will be removed in the future.
 * Use {@link KaKaoApiClientWithJibun} instead.
 * Because the road name address is not used in the current system.
 */
@Slf4j
@Deprecated
public class KaKaoApiClientWithRoadName extends KaKaoApiClient<ApartmentTransaction, ApartmentGeoRecord> {

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;
    private final RestTemplate restTemplate;
    private final CacheRepository<String, LocationRecord> roadNameCacheRepository;

    public KaKaoApiClientWithRoadName(KaKaoRestApiProperties kaKaoRestApiProperties,
                                      RestTemplate restTemplate,
                                      CacheRepository<String, LocationRecord> roadNameCacheRepository) {
        super(kaKaoRestApiProperties);
        this.restTemplate = restTemplate;
        this.roadNameCacheRepository = roadNameCacheRepository;
    }

    @Override
    public ApartmentGeoRecord callApi(ApartmentTransaction apartmentTransaction) {

//        Optional<String> roadNameAddress = apartmentTransaction.getRoadNameAddress();
//        LocationRecord roadNameLocationRecord = roadNameAddress.isPresent() ?
//                roadNameCacheRepository.computeIfAbsent(
//                        roadNameAddress.get(),
//                        roadNm -> {
//                            Documents documents = restTemplate
//                                    .exchange(
//                                            createUrl(apartmentTransaction),
//                                            HttpMethod.GET,
//                                            createHttpEntity(),
//                                            Documents.class)
//                                    .getBody();
//                            return documents.toLocationRecord();
//                        }) : LocationRecord.EMPTY;
//
//        return roadNameLocationRecord.toApartmentGeoRecord(apartmentTransaction.getId());
        return null;
    }

    @Override
    protected String getLocation(ApartmentTransaction apartmentTransaction) {
//        return apartmentTransaction.getRoadNameWithGu(Gu.getGuFromRegionalCode(regionalCode));
        return null;
    }
}
