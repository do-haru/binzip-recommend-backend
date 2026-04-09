package com.doharu.binzip_recommend.service;

import com.doharu.binzip_recommend.dto.QueryCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryParserService {

    // 나중에 OpenAIClient 주입

    public QueryCondition parse(String query) {

        // 1. AI 호출(지금은 mock)
        String json = callAi(query);

        // 2. JSON -> QueryCondition 변환
        return convert(json);
/*
        String purpose = null;
        List<String> targetAges = new ArrayList<>();
        String areaLevel = null;
        String crowdLevel = null;
        String priceLevel = null;
        String facilityLevel = null;
        String conditionLevel = null;

        String q = query.toLowerCase();

        if (q.contains("카페")) purpose = "CAFE";

        if (q.contains("20대")) targetAges.add("20");

        if (targetAges.isEmpty()) targetAges = null;

        return QueryCondition.builder()
                .purpose(purpose)
                .targetAges(targetAges)
                .areaLevel(areaLevel)
                .crowdLevel(crowdLevel)
                .priceLevel(priceLevel)
                .facilityLevel(facilityLevel)
                .conditionLevel(conditionLevel)
                .build();
*/

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
