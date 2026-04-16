package com.doharu.binzip_recommend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    private String regionName;
    private String regionDetail;

    private double area;
    private String houseType;
    private int grade;

    private String manager;
    private String phone;

    private LocalDate updateDate;

    private double latitude;    // 해당 지역의 중심 위도 좌표
    private double longitude;   // 해당 지역의 중심 경도 좌표
    private int facilityCount;  // 주변 편의시설 개수

    private Double crowd;    // 유동인구 수준

    @ElementCollection
    private List<Integer> targetAges;

    private double age20;
    private double age30;
    private double age40;
    private double age50;
    private double age60;
    private double ageEtc; // 연령대별 방문 비율 정보
    private int price;          // 해당 빈집의 가격

    private Double score;

    @Override
    public String toString() {
        return "RecommendHouse{" +
                "region='" + regionName + '\'' +
                ", age20=" + age20 +
                ", age30=" + age30 +
                ", age40=" + age40 +
                ", age50=" + age50 +
                ", age60=" + age60 +
                ", ageEtc=" + ageEtc +
                ", crowd=" + crowd +
                ", area=" + area +
                ", facility=" + facilityCount +
                ", grade=" + grade +
                ", price=" + price +
                '}';
    }
}
