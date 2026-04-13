package com.doharu.binzip_recommend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 원본 빈집(House)과 연결하기 위한 식별자

    // 원본 House 연결
    @ManyToOne(fetch = FetchType.LAZY)
    private House house;

    private double latitude;    // 해당 지역의 중심 위도 좌표
    private double longitude;   // 해당 지역의 중심 경도 좌표
    private int facilityCount;  // 주변 편의시설 개수

    private Double crowd;    // 유동인구 수준
    private Double age10;
    private Double age20;
    private Double age30;
    private Double age40;
    private Double ageEtc; // 연령대별 방문 비율 정보

    private int price;          // 해당 빈집의 가격

//    private Double score;
}
