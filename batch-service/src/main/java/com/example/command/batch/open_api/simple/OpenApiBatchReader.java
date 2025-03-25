package com.example.command.batch.open_api.simple;

import com.example.command.api_client.ApiClient;
import com.example.command.api_client.open_api.OpenApiRequest;
import com.example.command.batch.open_api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
public class OpenApiBatchReader implements ItemStreamReader<ApartmentDetailResponse> {

    private static final String NEXT_PAGE_NO = "nextPageNo";
    private static final String NEXT_CONTRACT_DATE = "nextContractDate";

    private int pageNo = 1;
    private LocalDate contractDate = LocalDate.now().minusMonths(1);

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;

    private final ApiClient<OpenApiRequest, ApartmentDetailResponse> openApiClient;
    private final int NUM_OF_ROWS;

    @Override
    public ApartmentDetailResponse read()  {

        ApartmentDetailResponse response = openApiClient.callApi(new OpenApiRequest(regionalCode, pageNo, contractDate));
        if(response.isLimitExceeded()){
            log.error("Limit Exceeded");
            throw new RuntimeException("Limit Exceeded");
        }

        log.info("pageNo : {}, contractDate : {}, totalCount : {}", pageNo, contractDate, response.getTotalCount());

        if(response.isEndOfData()) return null;

        if(isEndOfPage(response.getTotalCount())) {
                contractDate = getPreMonthContractDate();
                pageNo = 1;
        }
        else {
            pageNo++;
        }
            return response;
    }

    private LocalDate getPreMonthContractDate() {
        return contractDate.minusMonths(1);
    }

    private boolean isEndOfPage(int totalCount) {
        return pageNo * NUM_OF_ROWS >= totalCount;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        if (executionContext.containsKey(NEXT_PAGE_NO)) {
            pageNo = executionContext.getInt(NEXT_PAGE_NO);
        }

        if (executionContext.containsKey(NEXT_CONTRACT_DATE)) {
            contractDate = LocalDate.parse(executionContext.getString(NEXT_CONTRACT_DATE));
        }
    }


    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(NEXT_PAGE_NO, pageNo);
        executionContext.putString(NEXT_CONTRACT_DATE, contractDate.toString());
    }

}
