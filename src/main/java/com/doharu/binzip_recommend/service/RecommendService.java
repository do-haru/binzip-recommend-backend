package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.dto.RecommendHouse;
import org.springframework.stereotype.Service;

@Service
public class RecommendService {
    public RecommendHouse convert(House house) {
        return RecommendHouse.builder()
                .id(house.getId())
                .area(house.getArea())
                .grade(house.getGrade())
                .houseType(house.getHouseType())
                .latitude(0)
                .longitude(0)
                .facilityCount(0)
                .crowd(0)
                .targetAgeRatio(null)
                .price(0)
                .build();
    }
}
