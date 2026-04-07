package com.doharu.binzip_recommend;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RegionDetailMeta;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RegionDetailMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Order(2)
public class RegionMetaInitializer {

    private final HouseRepository houseRepository;
    private final RegionDetailMetaRepository regionDetailMetaRepository;

    @Bean
    public CommandLineRunner initRegionMeta() {
        return args -> {

            List<House> houses = houseRepository.findAll();

            // regionKey 리스트
            List<String> regionKeys = houses.stream()
                    .map(h -> h.getRegionDetail() != null
                            ? h.getRegionDetail()
                            : h.getRegionName())
                    .distinct()
                    .toList();

            // regionKey → regionName 매핑
            Map<String, String> regionMap = houses.stream()
                    .collect(Collectors.toMap(
                            h -> h.getRegionDetail() != null
                                    ? h.getRegionDetail()
                                    : h.getRegionName(),
                            House::getRegionName,
                            (existing, replacement) -> existing
                    ));


            // regionMap → RegionDetailMeta 저장
            regionMap.forEach((regionKey, regionName) -> {

                // 이미 있으면 skip
                if (regionDetailMetaRepository.findByRegionKey(regionKey).isPresent()) {
                    return;
                }

                RegionDetailMeta meta = RegionDetailMeta.builder()
                        .regionKey(regionKey)
                        .regionName(regionName)
                        .build();

                regionDetailMetaRepository.save(meta);
            });
        };
    }
}