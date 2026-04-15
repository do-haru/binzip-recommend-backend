package com.doharu.binzip_recommend.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BackData {

    private double latitude;    // 해당 지역의 중심 위도 좌표
    private double longitude;   // 해당 지역의 중심 경도 좌표
    private int facilityCount;  // 주변 편의시설 개수

    private Double crowd;    // 유동인구 수준

    private List<Integer> targetAges;

    private double age20;
    private double age30;
    private double age40;
    private double age50;
    private double age60;
    private double ageEtc;

    private int price;

}
