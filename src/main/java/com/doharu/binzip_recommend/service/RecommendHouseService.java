package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendHouseService {

    private final HouseRepository houseRepository;
    private final RecommendHouseRepository recommendHouseRepository;

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public List<RecommendHouse> getAllRecommendHouses() {
        return recommendHouseRepository.findAll();
    }

    public void generateRecommendHouse() {
        List<House> houses = houseRepository.findAll();

        // 변환
        List<RecommendHouse> list = houses.stream()
                .map(house -> RecommendHouse.builder()
                        .house(house)
                        .latitude(35.0)
                        .longitude(128.0)
                        .facilityCount(5)
                        .crowd(100.0)
                        .targetAgeRatio(0.3)
                        .price(1000)
                        .score(0.0)
                        .build()
                )
                .toList();

        // 저장
        recommendHouseRepository.saveAll(list);
    }
}
