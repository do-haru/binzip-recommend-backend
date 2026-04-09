package com.doharu.binzip_recommend.controller;

import com.doharu.binzip_recommend.external.TmapApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TmapTestController {

    private final TmapApiClient tmapApiClient;

    @GetMapping("/api/tmap-test")
    public Map test() {
        return tmapApiClient.getCoordinates("경상북도 영주시 풍기읍");
    }
}