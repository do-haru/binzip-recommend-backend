package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.dto.QueryCondition;
import com.doharu.binzip_recommend.external.OpenAiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QueryParserService {

    // 나중에 OpenAIClient 주입
    private final OpenAiClient openAiClient;

    public QueryCondition parse(String query) {

        // 1. AI 호출(지금은 mock)
        String aiResponse = openAiClient.call(query);

        System.out.println("aiResponse = " + aiResponse);
        // 2. JSON -> QueryCondition 변환
        return convert(aiResponse);


    }

    private String callAi(String query) {
        // 🔥 지금은 임시
        return """
        {
          "purpose": "CAFE",
          "targetAges": ["20"],
          "areaLevel": null,
          "crowdLevel": "HIGH",
          "priceLevel": null,
          "facilityLevel": null,
          "conditionLevel": null
        }
        """;
    }

    private QueryCondition convert(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, QueryCondition.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }
}
