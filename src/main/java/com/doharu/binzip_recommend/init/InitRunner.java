package com.doharu.binzip_recommend.init;

import com.doharu.binzip_recommend.domain.RecommendHouse;
import com.doharu.binzip_recommend.repository.RecommendHouseRepository;
import com.doharu.binzip_recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InitRunner implements CommandLineRunner {

    private final RecommendService recommendService;
    private final RecommendHouseRepository recommendHouseRepository;

    @Override
    public void run(String... args) throws Exception {
        recommendService.toRecommendHouse();
    }
}
