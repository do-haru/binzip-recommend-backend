package com.doharu.binzip_recommend.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class HouseApiClient {

    private final RestTemplate restTemplate;

    @Value("${odcloud.base-url}")
    private String baseUrl;

    @Value("${odcloud.service-key}")
    private String serviceKey;

    public HouseApiResponse fetchHouse() {

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("page", 1)
                .queryParam("perPage", 1000)
                .queryParam("serviceKey", serviceKey)
                .toUriString();

        HouseApiResponse response = restTemplate.getForObject(url, HouseApiResponse.class);

        // null 방어
        if (response == null) {
            log.error("API 응답 null");
            throw new RuntimeException("API 응답이 null입니다.");
        }

        if (response.getData() == null || response.getData().isEmpty()) {
            log.error("API 데이터 비어있음");
            throw new RuntimeException("API 데이터가 비어있습니다.");
        }

        log.info("API 응답 데이터 개수: {}", response.getData().size());

        return response;
    }

}
