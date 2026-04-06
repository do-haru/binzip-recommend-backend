package com.doharu.binzip_recommend;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final HouseRepository houseRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            houseRepository.save(
                    House.builder()
                            .regionName("풍기읍")
                            .regionDetail("교촌리")
                            .area(45.5)
                            .houseType("단독")
                            .grade(3)
                            .manager("영주시")
                            .phone("010-1234-5678")
                            .updateDate(LocalDate.now())
                            .build()
            );

            houseRepository.save(
                    House.builder()
                            .regionName("풍기읍")
                            .regionDetail("서부리")
                            .area(60.0)
                            .houseType("공동")
                            .grade(2)
                            .manager("영주시")
                            .phone("010-5678-1234")
                            .updateDate(LocalDate.now())
                            .build()
            );
        };
    }
}