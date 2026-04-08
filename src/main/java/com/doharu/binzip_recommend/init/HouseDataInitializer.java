package com.doharu.binzip_recommend.init;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.external.HouseApiClient;
import com.doharu.binzip_recommend.external.HouseApiItem;
import com.doharu.binzip_recommend.external.HouseApiResponse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class HouseDataInitializer implements CommandLineRunner {

    private final HouseApiClient houseApiClient;
    private final HouseRepository houseRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            // 1. API 호출
            HouseApiResponse response = houseApiClient.fetchHouse();

            List<HouseApiItem> items = response.getData();

            // 2. 기존 데이터 삭제
            houseRepository.deleteAll();

            // 3. 변환 후 저장
            List<House> houses = items.stream()
                    .map(item -> House.builder()
                            .regionName(item.getRegionName())
                            .regionDetail(item.getRegionDetail())
                            .area(parseDouble(item.getArea()))
                            .houseType(item.getHouseType())
                            .grade(parseGrade(item.getGrade()))
                            .manager(item.getManager())
                            .phone(item.getPhone())
                            .updateDate(parseDate(item.getUpdateDate()))
                            .build()
                    )
                    .toList();
            houseRepository.saveAll(houses);

            log.info("API 데이터 저장 완료");
        } catch (Exception e) {
            log.info("API 데이터 로딩 실패: " + e.getMessage());
        }
    }

    // ===== 변환 함수 =====

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseGrade(String grade) {
        try {
            return Integer.parseInt(grade.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
