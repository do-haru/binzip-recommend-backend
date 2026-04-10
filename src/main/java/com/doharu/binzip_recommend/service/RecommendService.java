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
public class RecommendService {

    private final HouseRepository houseRepository;
    private final RecommendHouseRepository recommendHouseRepository;
    private final TmapApiClient tmapApiClient;


    // RecommendHouse 조회
    public List<RecommendHouse> getRecommendHouses() {
        return recommendHouseRepository.findAll();
    }

    // House -> RecommendHouse 변환
    private RecommendHouse toRecommendHouse(House house) {

        return RecommendHouse.builder()
                .house(house)

                // 지금은 전부 기본값 (나중에 채움)
                .latitude(0.0)
                .longitude(0.0)
                .facilityCount(0)

                .crowd(0.0)
                .targetAgeRatio(0.0)

                .price(0)
//                .score(null)

                .build();
    }

    public void generateRecommendHouse(String regionName) {
        // 1. 기존 데이터 제거 (중복 방지)
        recommendHouseRepository.deleteAll();

        // 2. 원본 조회
        List<House> houses = houseRepository.findByRegionName(regionName);

        // 3. 변환
        List<RecommendHouse> list = houses.stream()
                .map(this::toRecommendHouse)
                .toList();

        // 4. 저장
        recommendHouseRepository.saveAll(list);
        /*
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
//                            .score(0.0)      // 임시
                            .build();
                })
                .toList();

        // 저장
        recommendHouseRepository.saveAll(list);
*/
    }

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public List<RecommendHouse> getAllRecommendHouses() {
        return recommendHouseRepository.findAll();
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
//                            .score(0.0)    // 임시
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
//                        .score(r.getScore())

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
