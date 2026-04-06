package com.doharu.binzip_recommend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/houses")
public class HouseController {

    @GetMapping
    public String getHouses() {
        return "ok";
    }
}
