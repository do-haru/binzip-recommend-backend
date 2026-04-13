package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.Level;
import com.doharu.binzip_recommend.domain.Purpose;
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
    private RecommendHouse toRecommendHouse(House house, double lat, double lon) {

        int facilityCount = (int)(Math.random() * 20);
        double crowd = Math.round(Math.random() * 100) / 100.0;
        int price = (int)(Math.random() * 1000);

        // ageRatio 임시 값
        double a = Math.random();
        double b = Math.random();
        double c = Math.random();
        double d = Math.random();
        double e = Math.random();

        double sum = a + b + c + d + e;

        double age10 = Math.round((a / sum) * 100) / 100.0;
        double age20 = Math.round((b / sum) * 100) / 100.0;
        double age30 = Math.round((c / sum) * 100) / 100.0;
        double age40 = Math.round((d / sum) * 100) / 100.0;
        double ageEtc = Math.round((e / sum) * 100) / 100.0;

       return RecommendHouse.builder()
                .house(house)

                // 지금은 전부 임시값 (나중에 채움)
                .latitude(lat)
                .longitude(lon)
                .facilityCount(facilityCount)

                .crowd(crowd)
               .age10(age10)
               .age20(age20)
               .age30(age30)
               .age40(age40)
               .ageEtc(ageEtc)

                .price(price)
//                .score(null)

                .build();
    }

    public void generateRecommendHouse(String regionName) {
        // 1. 기존 데이터 제거 (중복 방지)
        recommendHouseRepository.deleteAll();

        // 2. House 조회
        List<House> houses = houseRepository.findByRegionName(regionName);

        // 3. 캐시 성성
        Map<String, double[]> coordCache = new HashMap<>();

        // 3. 변환
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

                    // 🔥 캐시 사용
                    if (coordCache.containsKey(address)) {
                        latlon = coordCache.get(address);
                    } else {
                        Map result = tmapApiClient.getCoordinates(address);
                        latlon = tmapApiClient.extractLatLon(result);

                        coordCache.put(address, latlon);
                    }

                    double lat = 0.0;
                    double lon = 0.0;

                    if (latlon != null) {
                        lat = latlon[0];
                        lon = latlon[1];
                    }

                    RecommendHouse r = toRecommendHouse(house, lat, lon);

                    double score = calculateFinalScore(
                            r,
                            Purpose.CAFE,        // 일단 고정 테스트
                            Level.HIGH,          // 임시
                            Level.LOW,
                            Level.LOW,
                            Level.HIGH,
                            Level.MID
                    );
                    r.setScore(score);

                    return r;
                })
                .toList();

        // 4. 저장
        recommendHouseRepository.saveAll(list);
    }

    public Weight getWeight(Purpose purpose) {

        return switch (purpose) {

            case CAFE -> new Weight(0.35, 0.30, 0.15, 0.10, 0.10, "20s");

            case RESTAURANT -> new Weight(0.40, 0.30, 0.15, 0.05, 0.10, "30s");

            case STUDIO -> new Weight(0.10, 0.10, 0.20, 0.30, 0.30, "30s");

            case OFFICE -> new Weight(0.20, 0.20, 0.25, 0.20, 0.15, "30s");

            case VACATION -> new Weight(0.05, 0.10, 0.25, 0.35, 0.25, "40s");

            default -> new Weight(0.20, 0.20, 0.20, 0.20, 0.20, "20s");
        };
    }

    public double getScoreByLevel(double value, Level level) {

        return switch (level) {

            case HIGH -> value; // 클수록 좋음

            case LOW -> 1 - value; // 작을수록 좋음

            case MID -> 1 - Math.abs(value - 0.5); // 중간값이 최고
        };
    }

    private double calculateScore(RecommendHouse r, Weight w) {

        double facilityScore = r.getFacilityCount() / 20.0; // 임시 기준
        double crowdScore = r.getCrowd(); // 이미 0~1
        double priceScore = 1 - (r.getPrice() / 1000.0); // 낮을수록 좋음
        double conditionScore = r.getHouse().getGrade() / 5.0; // 1~5라고 가정
        double areaScore = r.getHouse().getArea() / 100.0; // 임시 기준

        // 🔥 age 점수
        double ageScore = 0.0;

        switch (w.targetAge) {
            case "10s" -> ageScore = r.getAge10();
            case "20s" -> ageScore = r.getAge20();
            case "30s" -> ageScore = r.getAge30();
            case "40s" -> ageScore = r.getAge40();
        }

        return
                facilityScore * w.facility +
                        crowdScore * w.crowd +
                        priceScore * w.price +
                        conditionScore * w.condition +
                        areaScore * w.area +
                        ageScore * 0.2; // 🔥 age는 일단 0.2 고정
    }

    private double calculateFinalScore(RecommendHouse r, Purpose purpose,
                                       Level crowdLevel,
                                       Level priceLevel,
                                       Level areaLevel,
                                       Level facilityLevel,
                                       Level conditionLevel) {

        // 1. 가중치 가져오기
        Weight w = getWeight(purpose);

        // 2. 각 값 정규화 (0~1 맞추기)
        double crowd = r.getCrowd(); // 이미 0~1
        double price = r.getPrice() / 1000.0;
        double area = r.getHouse().getArea() / 100.0;
        double facility = r.getFacilityCount() / 20.0;
        double condition = r.getHouse().getGrade() / 5.0;

        // 3. Level 적용 → 부분 점수
        double crowdScore = getScoreByLevel(crowd, crowdLevel);
        double priceScore = getScoreByLevel(price, priceLevel);
        double areaScore = getScoreByLevel(area, areaLevel);
        double facilityScore = getScoreByLevel(facility, facilityLevel);
        double conditionScore = getScoreByLevel(condition, conditionLevel);

        // 4. 가중치 적용 + 합산
        double total =
                crowdScore * w.crowd +
                        priceScore * w.price +
                        areaScore * w.area +
                        facilityScore * w.facility +
                        conditionScore * w.condition;

        // 5. 100점 변환
        return Math.round(total * 100);
    }

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public List<RecommendHouse> getAllRecommendHouses() {
        return recommendHouseRepository.findAll();
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
