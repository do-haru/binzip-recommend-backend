package com.doharu.binzip_recommend.init;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.external.TmapApiClient;
import com.doharu.binzip_recommend.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RegionTestRunner {

    private final HouseRepository houseRepository;
    private final TmapApiClient tmapApiClient;



    @Bean
    public CommandLineRunner run() {
        return args -> {

            List<String> regionDetails = List.of(
                    "교촌리",
                    "금계리",
                    "동부리",
                    "미곡리",
                    "백리",
                    "백신리",
                    "산법리",
                    "삼가리",
                    "서부리",
                    "성내리",
                    "수철리",
                    "욱금리",
                    "전구리",
                    "창락리"
            );

            for (String detail : regionDetails) {
/*

                String address = "경상북도 영주시 풍기읍 " + detail;

                Map res = tmapApiClient.getCoordinates(address);
                double[] latlon = tmapApiClient.extractLatLon(res);

                if (latlon != null) {
                    int cafe = tmapApiClient.getFacilityCount(latlon[0], latlon[1], "카페");
                    int food = tmapApiClient.getFacilityCount(latlon[0], latlon[1], "음식점");
                    int pharmacy = tmapApiClient.getFacilityCount(latlon[0], latlon[1], "약국");
                    int hospital = tmapApiClient.getFacilityCount(latlon[0], latlon[1], "병원");
                    int transport = tmapApiClient.getFacilityCount(latlon[0], latlon[1], "버스정류장");

                    int total = cafe + food + pharmacy + hospital + transport;

                    System.out.println(
                            address + " → " +
                                    "카페:" + cafe +
                                    " 음식점:" + food +
                                    " 약국:" + pharmacy +
                                    " 병원:" + hospital +
                                    " 버스:" + transport +
                                    " 총합:" + total
                    );
                } else {
                    System.out.println(address + " → 좌표 없음");
                }
*/
            }

//            String poiId = "317817";
//
//            double crowd = tmapApiClient.getCrowdByPoi(poiId);
//
//            System.out.println("혼잡도 결과: " + crowd);

//            double lat = 36.8108607;   // ← 네가 찾은 좌표 넣기
//            double lon = 128.6249989;
//
//            tmapApiClient.printPoisAround(lat, lon);

        };
    }

}