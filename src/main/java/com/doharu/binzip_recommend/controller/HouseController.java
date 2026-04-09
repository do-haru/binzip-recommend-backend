package com.doharu.binzip_recommend.controller;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RegionDetailMeta;
import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.dto.RecommendHouseResponse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RegionDetailMetaRepository;
import com.doharu.binzip_recommend.service.RecommendHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final RecommendHouseService recommendService;
    private final RegionDetailMetaRepository regionDetailMetaRepository;
    private final HouseRepository houseRepository;

    @GetMapping("/test1")
    public List<House> recommendTest(
            @RequestParam String regionName,
            @RequestParam String query
    ) {
        System.out.println("regionName = " + regionName);
        System.out.println("query = " + query);

        return houseRepository.findByRegionName(regionName);
    }

    @GetMapping
    public List<House> getHouses() {
        return recommendService.getAllHouses();
    }

    @GetMapping("/recommend")
    public List<RecommendHouse> getRecommendHouses() {
        recommendService.generateRecommendHouse();
        return recommendService.getAllRecommendHouses();
    }

    @GetMapping("/test-recommend")
    public List<RecommendHouse> test(@RequestParam String regionName) {
        return recommendService.createRecommendByRegion(regionName);
    }

    @GetMapping("/recommend-dto")
    public List<RecommendHouseResponse> getRecommendHousesDto() {
        return recommendService.getRecommendResponse();
    }

    @GetMapping("/region-meta")
    public List<RegionDetailMeta> getRegionMeta() {
        return regionDetailMetaRepository.findAll();
    }

    @GetMapping("/test-region")
    public List<House> testRegion(@RequestParam String regionName) {
        return houseRepository.findByRegionName(regionName);
    }
}
