package com.doharu.binzip_recommend.controller;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.domain.RegionDetailMeta;
import com.doharu.binzip_recommend.dto.RecommendHouse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.repository.RegionDetailMetaRepository;
import com.doharu.binzip_recommend.service.RecommendService;
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

    private final RecommendService recommendService;
    private final RegionDetailMetaRepository regionDetailMetaRepository;

    @GetMapping
    public List<House> getHouses() {
        return recommendService.getAllHouses();
    }

    @GetMapping("/recommend")
    public List<RecommendHouse> getRecommendHouses(@RequestParam String regionName) {
        return recommendService.getRecommendHouses(regionName);
    }

    @GetMapping("/region-meta")
    public List<RegionDetailMeta> getRegionMeta() {
        return regionDetailMetaRepository.findAll();
    }
}
