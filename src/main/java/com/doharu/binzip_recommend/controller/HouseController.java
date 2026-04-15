package com.doharu.binzip_recommend.controller;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RegionDetailMeta;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.QueryCondition;
import com.doharu.binzip_recommend.dto.RecommendHouseResponse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import com.doharu.binzip_recommend.repository.RegionDetailMetaRepository;
import com.doharu.binzip_recommend.service.CsvService;
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
    private final RegionDetailMetaRepository regionDetailMetaRepository;
    private final HouseRepository houseRepository;
    private final CsvService csvService;
    private final RecommendHouseRepository recommendHouseRepository;

    @GetMapping("/test1")
    public List<RecommendHouse> recommendTest(
            @RequestParam String regionName,
            @RequestParam String query
    ) {

        List<String> regions = Arrays.asList(regionName.split(","));

        System.out.println("regionName = " + regionName);
        System.out.println("query = " + query);

        QueryCondition parse = queryParserService.parse(query);
        System.out.println("parse = " + parse);


        recommendService.toRecommendHouse();

        return recommendHouseRepository.findAll().stream()
                .filter(h -> regions.contains(h.getRegionName()))
                .toList();
//        return houseRepository.findByRegionName(regionName);

//        recommendService.generateRecommendHouse(regionName);
//        return recommendService.getRecommendHouses();
    }
}
