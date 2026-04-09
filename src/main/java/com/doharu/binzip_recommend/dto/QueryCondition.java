package com.doharu.binzip_recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCondition {

    private String purpose; // CAFE, RESTAURANT, STUDIO, VACATION, ETC
    private List<String> targetAges; // 10, 20, 30, 40, 50, 60, ETC

    private String areaLevel; // SMALL, MEDIUM, LARGE, ETC
    private String crowdLevel; // LOW, MEDIUM, HIGH, ETC
    private String priceLevel; // LOW, MEDIUM, HIGH, ETC
    private String facilityLevel; // LOW, MEDIUM, HIGH, ETC
    private String conditionLevel; // BAD, NORMAL, GOOD, ETC

    @Override
    public String toString() {
        return "QueryCondition{" +
                "purpose='" + purpose + '\'' +
                ", targetAges=" + targetAges +
                ", areaLevel='" + areaLevel + '\'' +
                ", crowdLevel='" + crowdLevel + '\'' +
                ", priceLevel='" + priceLevel + '\'' +
                ", facilityLevel='" + facilityLevel + '\'' +
                ", conditionLevel='" + conditionLevel + '\'' +
                '}';
    }
}
