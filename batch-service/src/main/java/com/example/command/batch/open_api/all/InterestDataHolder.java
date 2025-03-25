package com.example.command.batch.open_api.all;

import com.example.command.adapter.repository.interest.InterestRepository;
import com.example.command.batch.open_api.DataHolder;
import com.example.command.domain.interest.Interest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class InterestDataHolder implements DataHolder<LocalDate> {

    private List<Interest> interests;
    private final InterestRepository interestRepository;

    @PostConstruct
    @Override
    public void init() {
        interests = interestRepository.findAll();
    }

    @Override
    public Integer getEntityId(LocalDate dealDate) {
        for (Interest interest : interests) {
            if(isBetween(dealDate, interest.getStartDate(), interest.getEndDate())) {
                return interest.getId();
            }
        }
        return null;
    }

    public boolean isBetween(LocalDate dealDate, LocalDate startDate, LocalDate endDate) {
        if(startDate.isEqual(dealDate) || endDate.isEqual(dealDate)) {
            return true;
        }
        return dealDate.isAfter(startDate) && dealDate.isBefore(endDate);
    }
}
