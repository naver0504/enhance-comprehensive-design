package com.example.command.batch.open_api.all;

import com.example.command.domain.dong.Gu;
import com.example.command.api_client.ApiClient;
import com.example.command.batch.open_api.dto.ApartmentDetailResponse;
import com.example.command.batch.open_api.dto.ApartmentDetailResponseWithGu;
import com.example.command.api_client.open_api.OpenApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
public class OpenApiAllGuBatchReader implements ItemStreamReader<ApartmentDetailResponseWithGu> {

    private static final String NEXT_PAGE_NO = "nextPageNo";
    private static final String NEXT_GU_ORDINAL = "nextGuOrdinal";
    private static final int LAST_GU_ORDER = Gu.NONE.ordinal();

    private int guOrdinal = 0;
    private int pageNo = 1;

    @Value("#{jobParameters[contractDate]}")
    private LocalDate contractDate;

    private final ApiClient<OpenApiRequest, ApartmentDetailResponse> openApiClient;
    private final int NUM_OF_ROWS;

    @Override
    public ApartmentDetailResponseWithGu read() {

        if(guOrdinal == LAST_GU_ORDER) {
            return null;
        }

        Gu gu = Gu.fromOrdinal(guOrdinal);

        ApartmentDetailResponse response = openApiClient.callApi(new OpenApiRequest(gu.getRegionalCode(), pageNo, contractDate));
        if(response.isLimitExceeded()){
            throw new RuntimeException("Limit Exceeded");
        }

        if(isEndOfData(response)) {
            guOrdinal++;
            pageNo = 1;
        } else {
            pageNo++;
        }

        return new ApartmentDetailResponseWithGu(response, gu);
    }

    public boolean isEndOfData(ApartmentDetailResponse response) {
        return response.isEndOfGu() || response.getTotalCount() <= NUM_OF_ROWS;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if(executionContext.containsKey(NEXT_PAGE_NO)) {
            pageNo = executionContext.getInt(NEXT_PAGE_NO);
        }

        if(executionContext.containsKey(NEXT_GU_ORDINAL)) {
            guOrdinal = executionContext.getInt(NEXT_GU_ORDINAL);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(NEXT_PAGE_NO, pageNo);
        executionContext.putInt(NEXT_GU_ORDINAL, guOrdinal);
    }
}
