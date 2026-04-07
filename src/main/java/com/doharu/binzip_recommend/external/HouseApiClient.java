package com.doharu.binzip_recommend.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HouseApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public HouseApiResponse fetchHouse() {

        String url = "https://api.odcloud.kr/api/15144406/v1/uddi:601778db-fb09-4eed-bc35-c502c972364c?page=1&perPage=1000&serviceKey=11d2d450fb2f979bfed1f79e7ad67305fde4391088eb536fe233524be8220191";
        return restTemplate.getForObject(url, HouseApiResponse.class);
    }

}
