package com.doharu.binzip_recommend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendHouseResponse {

    // House (원본 데이터)
    private Long houseId;

    private String regionName;
    private String regionDetail;

    private Double area;
    private String houseType;
    private Integer grade;

    private String manager;
    private String phone;

    // RecommendHouse (가공 데이터)
    private Integer price;
    private Double score;
    private double latitude;
    private double longitude;

    // 추천 설명
    private String reason;
}
