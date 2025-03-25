package com.example.query.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchApartNameRequest {

    private String gu;
    private String dong;

    public boolean isNotValid() {
        return isGuNotValid() | isDongNotValid();
    }

    private boolean isGuNotValid() {
        return !StringUtils.hasText(gu);
    }

    private boolean isDongNotValid() {
        return !StringUtils.hasText(dong);
    }
}
