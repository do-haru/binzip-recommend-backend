package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.RecommendHouseResponse;
import com.doharu.binzip_recommend.external.TmapApiClient;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecommendHouseService {

    private final HouseRepository houseRepository;
    private final RecommendHouseRepository recommendHouseRepository;
    private final TmapApiClient tmapApiClient;

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public List<RecommendHouse> getAllRecommendHouses() {
        return recommendHouseRepository.findAll();
    }

    public void generateRecommendHouse() {
        List<House> houses = houseRepository.findAll();

        Map<String, double[]> cache = new HashMap<>();

        // 변환
        List<RecommendHouse> list = houses.stream()
                .map(house -> {
                    String address;

                    if (house.getRegionDetail() == null || house.getRegionDetail().isBlank()) {
                        address = house.getRegionName();
                    } else {
                        address = house.getRegionName() + " " + house.getRegionDetail();
                    }
                    address = address.trim();

                    double[] latlon;
                    if (cache.containsKey(address)) {
                        latlon = cache.get(address);
                    } else {
                        Map result = tmapApiClient.getCoordinates(address);
                        latlon = tmapApiClient.extractLatLon(result);

                        cache.put(address, latlon);
                    }

                    return  RecommendHouse.builder()
                            .house(house)
                            .latitude(latlon[0])
                            .longitude(latlon[1])
                            .price(1000)     // 임시
                            .score(0.0)      // 임시
                            .build();
                })
                .toList();

        // 저장
        recommendHouseRepository.saveAll(list);
    }

    public List<RecommendHouse> createRecommendByRegion(String regionName) {

        List<House> houses = houseRepository.findByRegionName(regionName);

        Map<String, double[]> cache = new HashMap<>();

        return houses.stream()
                .map(house -> {

                    // 1️⃣ 주소 생성
                    String address;

                    if (house.getRegionDetail() == null || house.getRegionDetail().isBlank()) {
                        address = house.getRegionName();
                    } else {
                        address = house.getRegionName() + " " + house.getRegionDetail();
                    }

                    address = address.trim();

                    // 2️⃣ 좌표 캐시
                    double[] latlon;

                    if (cache.containsKey(address)) {
                        latlon = cache.get(address);
                    } else {
                        Map result = tmapApiClient.getCoordinates(address);

                        try {
                            Thread.sleep(200); // 호출 제한
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        latlon = tmapApiClient.extractLatLon(result);

                        if (latlon == null) {
                            latlon = new double[]{0.0, 0.0};
                        }

                        cache.put(address, latlon);
                    }

                    // 3️⃣ RecommendHouse 생성
                    return RecommendHouse.builder()
                            .house(house)
                            .latitude(latlon[0])
                            .longitude(latlon[1])
                            .price(1000)   // 임시
                            .score(0.0)    // 임시
                            .build();
                })
                .toList();
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
