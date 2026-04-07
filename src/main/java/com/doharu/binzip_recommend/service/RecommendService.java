package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.dto.RecommendHouse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendService {

    private final HouseRepository houseRepository;

    public RecommendHouse convert(House house) {
        return RecommendHouse.builder()
                .id(house.getId())
                .area(house.getArea())
                .grade(house.getGrade())
                .houseType(house.getHouseType())
                .latitude(0)
                .longitude(0)
                .facilityCount(0)
                .crowd("LOW")
                .targetAgeRatio(null)
                .price(0)
                .build();
    }

    public List<RecommendHouse> getRecommendHouses(String regionName) {
        return houseRepository.findByRegionName(regionName)
                .stream()
                .map(this::convert)
                .toList();
    }
}
