package com.example.command.batch.open_api.simple;


import com.example.command.batch.open_api.dto.ApartmentDetail;
import com.example.command.batch.open_api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OpenApiJdbcWriter implements ItemWriter<ApartmentDetailResponse> {

    private final OpenApiDongDataHolder openApiDongDataHolder;
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SQL =
            "INSERT INTO apartment_transaction (" +
                    "apartment_name, " +
                    "build_year, " +
                    "deal_amount, " +
                    "deal_year, " +
                    "deal_month, " +
                    "deal_day, " +
                    "area_for_exclusive_use, " +
                    "jibun, " +
                    "floor, " +
                    "deal_date, " +
                    "dealing_gbn," +
                    "dong_entity_id" +
                    ") " +
                    "VALUES" +
                    "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";




    @Override
    public void write(Chunk<? extends ApartmentDetailResponse> chunk)  {
        ApartmentDetailResponse apartmentDetailResponse = chunk.getItems().get(0);

        List<ApartmentDetail> items = apartmentDetailResponse.body().items()
                .stream()
                .filter(apartmentDetail -> openApiDongDataHolder.getEntityId(apartmentDetail.dongName().trim()) != null)
                .toList();

        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                try {
                    ApartmentDetail apartmentDetail = items.get(i);
                    ps.setString(1, apartmentDetail.apartmentName() == null ? null : apartmentDetail.apartmentName());
                    ps.setInt(2, apartmentDetail.buildYear());
                    ps.setInt(3, apartmentDetail.dealAmount());
                    ps.setInt(4, apartmentDetail.dealYear());
                    ps.setInt(5, apartmentDetail.dealMonth());
                    ps.setInt(6, apartmentDetail.dealDay());
                    ps.setDouble(7, apartmentDetail.areaForExclusiveUse());
                    ps.setString(8, apartmentDetail.jibun());
                    ps.setInt(9, apartmentDetail.floor());
                    ps.setDate(10, Date.valueOf(apartmentDetail.getDealDate()));
                    ps.setString(11, apartmentDetail.dealingGbn() == null ? null : apartmentDetail.dealingGbn().name());
                    ps.setLong(12, openApiDongDataHolder.getEntityId(apartmentDetail.dongName()));
                } catch (Exception e) {
                    log.error("apartmentDetail : {}", items.get(i));
                    throw e;
                }
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }
}
