package com.doharu.binzip_recommend.controller;

import com.doharu.binzip_recommend.domain.House;
import com.doharu.binzip_recommend.dto.RecommendHouse;
import com.doharu.binzip_recommend.repository.HouseRepository;
import com.doharu.binzip_recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseRepository houseRepository;
    private final RecommendService recommendService;

    @GetMapping
    public List<House> getHouses() {
        return houseRepository.findAll();
    }

    @GetMapping("/recommend")
    public List<RecommendHouse> getRecommendHouses() {
        return recommendService.getRecommendHouses();
    }
}
