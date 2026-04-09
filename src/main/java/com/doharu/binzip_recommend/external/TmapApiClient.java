package com.doharu.binzip_recommend.external;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TmapApiClient {

    private final RestTemplate restTemplate;

    @Value("${tmap.app-key}")
    private String appKey;

    public Map getCoordinates(String address) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://apis.openapi.sk.com/tmap/geo/fullAddrGeo")
                .queryParam("version", 1)
                .queryParam("format", "json")
                .queryParam("fullAddr", address)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", appKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }

    public double[] extractLatLon(Map response) {

        if (response == null) return null;

        Map coordinateInfo = (Map) response.get("coordinateInfo");
        if (coordinateInfo == null) return null;

        List coordinates = (List) coordinateInfo.get("coordinate");
        if (coordinates == null || coordinates.isEmpty()) return null;

        Map first = (Map) coordinates.get(0);

        String latStr = (String) first.get("lat");
        String lonStr = (String) first.get("lon");

        if (latStr == null || lonStr == null) return null;

        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);

        return new double[]{lat, lon};
    }
}
