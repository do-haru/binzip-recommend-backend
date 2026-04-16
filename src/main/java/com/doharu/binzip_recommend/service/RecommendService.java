package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.Level;
import com.doharu.binzip_recommend.domain.Purpose;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.QueryCondition;
import com.doharu.binzip_recommend.dto.RecommendHouseResponse;
import com.doharu.binzip_recommend.external.TmapApiClient;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecommendService {

    private final HouseRepository houseRepository;
    private final RecommendHouseRepository recommendHouseRepository;
    private final TmapApiClient tmapApiClient;
    private final CsvService csvService;


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

    public List<RecommendHouse> filterByRegion(List<String> regions, QueryCondition condition) {
        return recommendHouseRepository.findAll().stream()
                .filter(h -> regions.contains(h.getRegionName()))
                .filter(h -> isTargetAgeMatched(h, condition))
                .toList();
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
    /*
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
*/
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public List<RecommendHouse> getAllRecommendHouses() {
        return recommendHouseRepository.findAll();
    }
/*
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
*/
    private String generateReason(RecommendHouse r) {

        // 지금은 임시 로직
        return "유동인구와 입지가 좋아 추천됩니다.";
    }
}
