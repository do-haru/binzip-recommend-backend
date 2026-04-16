package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.dto.EstateDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstateService {

    private final List<EstateDto> estateList = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadCsv();
    }

    private void loadCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("estate_data.csv");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream())
            );

            String line;
            boolean isFirst = true;

            while ((line = br.readLine()) != null) {

                // 헤더 건너뛰기
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (tokens.length < 14) continue; // 안전하게 필터

                String name = tokens[3].trim();
                String address = tokens[12].trim();

                estateList.add(new EstateDto(name, address));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EstateDto> getAll() {
        return estateList;
    }

    public List<EstateDto> getRandomEstates(String regionName) {

        // 1. 지역 매칭
        List<EstateDto> matched = estateList.stream()
                .filter(e -> e.getAddress().contains(regionName))
                .collect(Collectors.toList());

        List<EstateDto> source;

        // 2. 4개 이상이면 매칭 사용, 아니면 전체
        if (matched.size() >= 4) {
            source = matched;
        } else {
            source = estateList;
        }

        // 3. 랜덤 섞기
        Collections.shuffle(source);

        // 4. 앞에서 4개
        return source.stream()
                .limit(4)
                .collect(Collectors.toList());
    }
}
