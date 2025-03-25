
package com.example.command.api_client.predict.dto;

import com.example.command.domain.dong.Gu;

public interface ApartmentQuery {
    Gu getGu();
    String getDongName();
    double getAreaForExclusiveUse();
    int getFloor();
    int getBuildYear();
}
