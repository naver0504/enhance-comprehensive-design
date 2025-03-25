package com.example.command.batch.open_api.all;

import com.example.command.api_client.kakao.KaKaoApiClientWithJibun;
import com.example.command.batch.kakao_map.dto.ApartmentGeoRecord;
import com.example.command.batch.kakao_map.dto.TransactionWithGu;
import com.example.command.batch.open_api.dto.ApartmentDetail;
import com.example.command.batch.open_api.dto.ApartmentDetailResponseWithGu;
import com.example.command.batch.open_api.dto.GuDong;
import com.example.command.domain.apartment.ApartmentTransaction;
import com.example.command.domain.dong.DongEntity;
import com.example.command.domain.interest.Interest;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ApartmentTransactionProcessor implements ItemProcessor<ApartmentDetailResponseWithGu, List<ApartmentTransaction>> {

    private final OpenApiAllGuDataHolder openApiAllGuDataHolder;
    private final KaKaoApiClientWithJibun kaKaoApiClientWithJibun;
    private final InterestDataHolder interestDataHolder;

    @Override
    public List<ApartmentTransaction> process(ApartmentDetailResponseWithGu item) throws Exception {
        List<ApartmentTransaction> apartmentTransactions = new ArrayList<>(item.toApartmentDetails().size());
        for(ApartmentDetail apartmentDetail : item.toApartmentDetails()) {

            ApartmentGeoRecord apartmentGeoRecord = kaKaoApiClientWithJibun.callApi(new TransactionWithGu(-1L, item.gu(), apartmentDetail.dongName(), apartmentDetail.jibun()));
            Point point = createPoint(apartmentGeoRecord);
            Interest interest = Interest.builder().id(interestDataHolder.getEntityId(apartmentDetail.getDealDate())).build();
            DongEntity dongEntity = DongEntity.builder().id(openApiAllGuDataHolder.getEntityId(item.gu(), apartmentDetail.dongName())).build();

            ApartmentTransaction apartmentTransaction = ApartmentTransaction.builder()
                    .apartmentName(apartmentDetail.apartmentName())
                    .buildYear(apartmentDetail.buildYear())
                    .dealAmount(apartmentDetail.getDealAmount())
                    .dealYear(apartmentDetail.dealYear())
                    .dealMonth(apartmentDetail.dealMonth())
                    .dealDay(apartmentDetail.dealDay())
                    .areaForExclusiveUse(apartmentDetail.areaForExclusiveUse())
                    .floor(apartmentDetail.floor())
                    .dealDate(apartmentDetail.getDealDate())
                    .dealingGbn(apartmentDetail.dealingGbn())
                    .geography(point)
                    .interest(interest)
                    .dongEntity(dongEntity)
                    .build();

            apartmentTransactions.add(apartmentTransaction);
        }
        return apartmentTransactions;
    }

    public Point createPoint(ApartmentGeoRecord apartmentGeoRecord) {
        if(!apartmentGeoRecord.isNotEmpty()) return null;
        WKTReader wktReader = new WKTReader();
        try {
            return (Point) wktReader.read(apartmentGeoRecord.toPoint());
        } catch (Exception e) {
            return null;
        }
    }
}
