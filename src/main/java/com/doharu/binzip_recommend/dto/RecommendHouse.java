package com.doharu.binzip_recommend.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendHouse {

    private Long id;

    private double area;
    private int grade;
    private String houseType;

    private double latitude;
    private double longitude;
    private int facilityCount;

    private int crowd;
    private Map<String, Double> targetAgeRatio;

    private int price;
}
