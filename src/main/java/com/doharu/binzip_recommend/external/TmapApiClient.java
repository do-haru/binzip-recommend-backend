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

    public int getFacilityCount(double lat, double lon,String category) {

        String url = "https://apis.openapi.sk.com/tmap/pois/search/around";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("version", 1)                 // 🔥 필수
                .queryParam("centerLat", lat)
                .queryParam("centerLon", lon)
                .queryParam("radius", 1)                  // 🔥 km 단위 (1km)
                .queryParam("count", 100)
                .queryParam("categories", category);       // 🔥 일단 하나만


        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", appKey);
        headers.set("Accept", "application/json"); // 🔥 필수

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                Map.class
        );


        Map body = response.getBody();

        if (body == null) {
            return 0;
        }

        Map searchPoiInfo = (Map) body.get("searchPoiInfo");

        if (searchPoiInfo == null) {
            return 0;
        }


        int totalCount = Integer.parseInt(searchPoiInfo.get("totalCount").toString());

        return totalCount;
    }

    public double getCrowdByPoi(String poiId) {

        String url = "https://apis.openapi.sk.com/tmap/puzzle/pois/" + poiId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", appKey);
        headers.set("Accept", "application/json");

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        System.out.println("응답 상태: " + response.getStatusCode());
        System.out.println("전체 응답: " + response.getBody());

        Map body = response.getBody();
        if (body == null) return 0;

        List<Map<String, Object>> rltm =
                (List<Map<String, Object>>) body.get("rltm");

        if (rltm == null || rltm.isEmpty()) return 0;

        Object congestion = rltm.get(0).get("congestion");

        return Double.parseDouble(congestion.toString());
    }

    public void printPoisAround(double lat, double lon) {

        String url = "https://apis.openapi.sk.com/tmap/pois/search/around";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("version", 1)
                .queryParam("centerLat", lat)
                .queryParam("centerLon", lon)
                .queryParam("radius", 1) // 1km (좁게 잡는 게 정확함)
                .queryParam("count", 20); // 많이 가져오기

        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", appKey);
        headers.set("Accept", "application/json");

        ResponseEntity<Map> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        Map body = response.getBody();

        if (body == null) {
            System.out.println("응답 없음");
            return;
        }

        Map searchPoiInfo = (Map) body.get("searchPoiInfo");
        if (searchPoiInfo == null) {
            System.out.println("searchPoiInfo 없음");
            return;
        }

        Map pois = (Map) searchPoiInfo.get("pois");
        if (pois == null) {
            System.out.println("pois 없음");
            return;
        }

        List<Map<String, Object>> poiList =
                (List<Map<String, Object>>) pois.get("poi");

        if (poiList == null || poiList.isEmpty()) {
            System.out.println("POI 없음");
            return;
        }

        System.out.println("=== 주변 POI 목록 ===");

        for (Map<String, Object> poi : poiList) {
            String name = (String) poi.get("name");
            Object id = poi.get("id");

            System.out.println("이름: " + name + " / poiId: " + id);
        }
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
