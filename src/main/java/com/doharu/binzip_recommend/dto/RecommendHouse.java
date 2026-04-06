package com.doharu.binzip_recommend.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendHouse {

    private Long id;            // 원본 빈집(House)과 연결하기 위한 식별자

    private double area;        // 건축물 면적
    private int grade;          // 빈집 상태등급
    private String houseType;   // 주택 유형

    private double latitude;    // 해당 지역의 중심 위도 좌표
    private double longitude;   // 해당 지역의 중심 경도 좌표
    private int facilityCount;  // 주변 편의시설 개수

    private String crowd;          // 유동인구 수준
    private Map<String, Double> targetAgeRatio; // 연령대별 방문 비율 정보

    private int price;          // 해당 빈집의 가격
}
