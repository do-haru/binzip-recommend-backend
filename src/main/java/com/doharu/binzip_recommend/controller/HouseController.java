package com.doharu.binzip_recommend.controller;

import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.QueryCondition;
import com.doharu.binzip_recommend.dto.RecommendResultDto;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import com.doharu.binzip_recommend.service.QueryParserService;
import com.doharu.binzip_recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final RecommendService recommendService;
    private final QueryParserService queryParserService;
    private final RecommendHouseRepository recommendHouseRepository;

    @GetMapping("/test1")
    public List<RecommendResultDto> recommendTest(
            @RequestParam String regionName,
            @RequestParam String query
    ) {

        List<String> regions = Arrays.asList(regionName.split(","));

        System.out.println("regionName = " + regionName);
        System.out.println("query = " + query);

        QueryCondition parse = queryParserService.parse(query);
        System.out.println("parse = " + parse);

        return recommendService.filterByRegion(regions, parse);
    }

    @GetMapping("/recommend")
    public List<RecommendHouse> test2() {
        return recommendService.getRecommendHouses();
    }
}
