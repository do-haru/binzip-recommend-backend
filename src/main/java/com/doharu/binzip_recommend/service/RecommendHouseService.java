package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.RecommendHouseResponse;
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

    public List<RecommendHouseResponse> getRecommendResponse() {

        return recommendHouseRepository.findAll()
                .stream()
                .map(r -> RecommendHouseResponse.builder()

                        // House (원본)
                        .houseId(r.getHouse().getId())
                        .regionName(r.getHouse().getRegionName())
                        .regionDetail(r.getHouse().getRegionDetail())
                        .area(r.getHouse().getArea())
                        .houseType(r.getHouse().getHouseType())
                        .grade(r.getHouse().getGrade())
                        .manager(r.getHouse().getManager())
                        .phone(r.getHouse().getPhone())

                        // RecommendHouse (가공)
                        .price(r.getPrice())
                        .score(r.getScore())

                        // 추천 이유 (임시)
                        .reason(generateReason(r))

                        .build())
                .toList();
    }

    private String generateReason(RecommendHouse r) {

        // 지금은 임시 로직
        return "유동인구와 입지가 좋아 추천됩니다.";
    }
}
