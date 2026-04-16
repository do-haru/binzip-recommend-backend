package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.Level;
import com.doharu.binzip_recommend.domain.Purpose;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.EstateDto;
import com.doharu.binzip_recommend.dto.QueryCondition;
import com.doharu.binzip_recommend.dto.RecommendResultDto;
import com.doharu.binzip_recommend.external.OpenAiClient;
import com.doharu.binzip_recommend.external.TmapApiClient;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RecommendService {

    private final HouseRepository houseRepository;
    private final RecommendHouseRepository recommendHouseRepository;
    private final TmapApiClient tmapApiClient;
    private final CsvService csvService;
    private final OpenAiClient openAiClient;
    private final EstateService estateService;


    // RecommendHouse 조회
    public List<RecommendHouse> getRecommendHouses() {
        return recommendHouseRepository.findAll();
    }

    // House, BackData -> RecommendHouse 변환
    public void toRecommendHouse() {

        List<House> houses = houseRepository.findAll();
        List<BackData> backDataList = csvService.readCsv();

        if (houses.size() != backDataList.size()) {
            throw new RuntimeException("House와 CSV 개수 다름");
        }
        for (int i = 0; i < houses.size(); i++) {

            House house = houses.get(i);
            BackData data = backDataList.get(i);

            RecommendHouse rh = RecommendHouse.builder()
                    // House 정보
                    .regionName(house.getRegionName())
                    .regionDetail(house.getRegionDetail())
                    .area(house.getArea())
                    .houseType(house.getHouseType())
                    .grade(house.getGrade())
                    .manager(house.getManager())
                    .phone(house.getPhone())
                    .updateDate(house.getUpdateDate())

                    // BackData 정보
                    .latitude(data.getLatitude())
                    .longitude(data.getLongitude())
                    .facilityCount(data.getFacilityCount())
                    .crowd(data.getCrowd())

                    .age20(data.getAge20())
                    .age30(data.getAge30())
                    .age40(data.getAge40())
                    .age50(data.getAge50())
                    .age60(data.getAge60())
                    .ageEtc(data.getAgeEtc())

                    .price(data.getPrice())

                    // 초기값
                    .score(0.0)

                    .build();

            recommendHouseRepository.save(rh);
        }
    }

    public List<RecommendResultDto> filterByRegion(List<String> regions, QueryCondition condition) {
        Map<String, String> reasonCache = new HashMap<>();

        List<RecommendResultDto> result = recommendHouseRepository.findAll().stream()
                .filter(h -> regions.contains(h.getRegionName()))
                .filter(h -> isTargetAgeMatched(h, condition))
                .filter(h -> isCrowdMatched(h, condition))
                .filter(h -> isAreaMatched(h, condition))
                .filter(h -> isFacilityMatched(h, condition))
                .filter(h -> isConditionMatched(h, condition))
                .filter(h -> isPriceMatched(h, condition))
                .map(h -> {
                    double score = calculateScore(h, condition);
                    h.setScore(score);
                    return h;
                })
                .sorted(Comparator.comparing(RecommendHouse::getScore).reversed())
                .limit(20)
                .map(h -> {
                    List<String> reasons = extractReasons(h, condition);
                    List<EstateDto> estates =
                            estateService.getRandomEstates(h.getRegionName());

                    String key = String.join(",", reasons);

                    String reasonText;

                    if (reasonCache.containsKey(key)) {
                        reasonText = reasonCache.get(key);
                    } else {
                        reasonText = openAiClient.generateReasonText(reasons);
                        reasonCache.put(key, reasonText);
                    }

                    return RecommendResultDto.builder()
                            .house(h)
                            .reasons(reasons)
                            .reasonText(reasonText)
                            .estates(estates)
                            .build();
                })
                .toList();

        System.out.println("===== 필터 결과 =====");
        for (RecommendResultDto h : result) {
            System.out.println(h);
        }

        return result;
    }

    // 나이 필터 함수
    private boolean isTargetAgeMatched(RecommendHouse h, QueryCondition condition) {

        if (condition.getTargetAges() == null || condition.getTargetAges().isEmpty()) {
            return true;
        }

        for (String age : condition.getTargetAges()) {
            switch (age) {
                case "20":
                    if (h.getAge20() >= 15) return true;
                    break;
                case "30":
                    if (h.getAge30() >= 15) return true;
                    break;
                case "40":
                    if (h.getAge40() >= 15) return true;
                    break;
                case "50":
                    if (h.getAge50() >= 15) return true;
                    break;
                case "60":
                    if (h.getAge60() >= 15) return true;
                    break;
            }
        }

        return false;
    }

    // 유동인구 필터 함수
    private boolean isCrowdMatched(RecommendHouse h, QueryCondition condition) {

        if (condition.getCrowdLevel() == null || condition.getCrowdLevel().equals("null")) {
            return true;
        }

        double crowd = h.getCrowd();

        switch (condition.getCrowdLevel()) {
            case "HIGH":
                return crowd > 50;
            case "MEDIUM":
                return crowd >= 20 && crowd <= 50;
            case "LOW":
                return crowd < 20;
        }

        return true;
    }

    // 빈집 면적 필터 함수
    private boolean isAreaMatched(RecommendHouse h, QueryCondition condition) {

        if (condition.getAreaLevel() == null || condition.getAreaLevel().equals("null")) {
            return true;
        }

        double area = h.getArea();

        switch (condition.getAreaLevel()) {
            case "SMALL":
                return area <= 50;
            case "MEDIUM":
                return area > 50 && area < 90;
            case "LARGE":
                return area >= 90;
        }

        return true;
    }

    private boolean isFacilityMatched(RecommendHouse h, QueryCondition condition) {

        if (condition.getFacilityLevel() == null || condition.getFacilityLevel().equals("null")) {
            return true;
        }

        int facility = h.getFacilityCount();

        switch (condition.getFacilityLevel()) {
            case "HIGH":
                return facility >= 8;
            case "MEDIUM":
                return facility >= 4 && facility <= 7;
            case "LOW":
                return facility <= 3;
        }

        return true;
    }

    private boolean isConditionMatched(RecommendHouse h, QueryCondition condition) {

        if (condition.getConditionLevel() == null || condition.getConditionLevel().equals("null")) {
            return true;
        }

        int grade = h.getGrade();

        switch (condition.getConditionLevel()) {
            case "HIGH":
                return grade <= 2;      // 1,2
            case "MEDIUM":
                return grade == 3;      // 3
            case "LOW":
                return grade >= 4;      // 4,5
        }

        return true;
    }

    private boolean isPriceMatched(RecommendHouse h, QueryCondition condition) {

        if (condition.getPriceLevel() == null || condition.getPriceLevel().equals("null")) {
            return true;
        }

        int price = h.getPrice();

        switch (condition.getPriceLevel()) {
            case "LOW":
                return price <= 10000;
            case "MEDIUM":
                return price > 10000 && price < 15000;
            case "HIGH":
                return price >= 15000;
        }

        return true;
    }

    private Level toLevel(String value) {
        if (value == null || value.equals("null")) {
            return Level.MID; // 기본값
        }

        return switch (value) {
            case "HIGH" -> Level.HIGH;
            case "LOW" -> Level.LOW;
            default -> Level.MID;
        };
    }

    private double calculateScore(RecommendHouse h, QueryCondition condition) {

        // 1. purpose → weight
        Purpose purpose = condition.getPurpose() == null
                ? Purpose.CAFE
                : Purpose.valueOf(condition.getPurpose());

        Weight w = getWeight(purpose);

        // 2. 값 정규화
        double crowd = h.getCrowd() / 100.0;
        double price = h.getPrice() / 20000.0;
        double area = Math.min(h.getArea() / 100.0, 1);
        double facility = h.getFacilityCount() / 10.0;
        double cond = (5 - h.getGrade()) / 4.0;

        // 3. Level 변환 (null → MID)
        Level crowdLevel = toLevel(condition.getCrowdLevel());
        Level priceLevel = toLevel(condition.getPriceLevel());
        Level areaLevel = toLevel(condition.getAreaLevel());
        Level facilityLevel = toLevel(condition.getFacilityLevel());
        Level condLevel = toLevel(condition.getConditionLevel());

        // 4. 각 점수 계산
        double crowdScore = getScoreByLevel(crowd, crowdLevel);
        double priceScore = getScoreByLevel(price, priceLevel);
        double areaScore = getScoreByLevel(area, areaLevel);
        double facilityScore = getScoreByLevel(facility, facilityLevel);
        double condScore = getScoreByLevel(cond, condLevel);

        // 5. 가중합
        double total =
                crowdScore * w.getCrowd() +
                        priceScore * w.getPrice() +
                        areaScore * w.getArea() +
                        facilityScore * w.getFacility() +
                        condScore * w.getCondition();

        return Math.round(total * 100);
    }

/*
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
                .sorted(Comparator.comparing(RecommendHouse::getScore).reversed())
                .toList();

        // 4. 저장
        recommendHouseRepository.saveAll(list);
    }
*/
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

    private List<String> extractReasons(RecommendHouse h, QueryCondition condition) {

        List<String> reasons = new ArrayList<>();

        // 1. 값 정규화 (calculateScore랑 동일)
        double crowd = h.getCrowd() / 100.0;
        double price = h.getPrice() / 20000.0;
        double area = Math.min(h.getArea() / 100.0, 1);
        double facility = h.getFacilityCount() / 10.0;
        double cond = (5 - h.getGrade()) / 4.0;

        // 2. Level 적용 (null → MID)
        double crowdScore = getScoreByLevel(crowd, toLevel(condition.getCrowdLevel()));
        double priceScore = getScoreByLevel(price, toLevel(condition.getPriceLevel()));
        double areaScore = getScoreByLevel(area, toLevel(condition.getAreaLevel()));
        double facilityScore = getScoreByLevel(facility, toLevel(condition.getFacilityLevel()));
        double condScore = getScoreByLevel(cond, toLevel(condition.getConditionLevel()));

        // 3. 기준: 0.7 이상만 이유로 채택

        if (crowdScore >= 0.7) {
            reasons.add("유동인구 많음");
        }

        if (facilityScore >= 0.7) {
            reasons.add("시설 풍부");
        }

        if (areaScore >= 0.7) {
            reasons.add("면적 넓음");
        }

        if (priceScore >= 0.7) {
            reasons.add("가격 적절");
        }

        if (condScore >= 0.7) {
            reasons.add("상태 좋음");
        }

        return reasons;
    }

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public List<RecommendHouse> getAllRecommendHouses() {
        return recommendHouseRepository.findAll();
    }

    private String generateReason(RecommendHouse r) {

        // 지금은 임시 로직
        return "유동인구와 입지가 좋아 추천됩니다.";
    }
}
