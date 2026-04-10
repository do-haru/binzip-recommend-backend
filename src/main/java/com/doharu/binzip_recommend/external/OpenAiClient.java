package com.doharu.binzip_recommend.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate;

    @Value("${openai.api-key}")
    private String apiKey;

    public String call(String query) {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // 🔥 핵심

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-mini",
                "messages", new Object[]{
                        Map.of("role", "system", "content",
                                """
너는 사용자의 입력을 분석하여 적절한 추천 조건을 생성하는 시스템이다.

규칙:
1. 사용자의 의도가 명확하지 않아도 일반적인 기준으로 값을 추론하라.
2. null을 최소화하고 가능한 값을 채워라.
3. 일반적인 상식 기반으로 판단하라.
   - 음식점 → 유동인구 HIGH
   - 카페 → 20~30대 중심
   - 사무실 → 조용함, 중간 유동
4. 반드시 아래 JSON 형식으로만 반환하라.
5. 코드블럭(```) 절대 사용하지 마라.

형식:
{
  "purpose": "CAFE | RESTAURANT | STUDIO | OFFICE | VACATION | null",
  "targetAges": ["10", "20", "30", "40", "50", "60", "etc"],
  "areaLevel": "SMALL | MEDIUM | LARGE | null",
  "crowdLevel": "LOW | MEDIUM | HIGH | null",
  "priceLevel": "LOW | MEDIUM | HIGH | null",
  "facilityLevel": "LOW | MEDIUM | HIGH | null",
  "conditionLevel": "LOW | MEDIUM | HIGH | null"
}
"""),
                        Map.of("role", "user", "content", query)
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(url, request, Map.class);

        List<Map> choices = (List<Map>) response.get("choices");
        Map firstChoice = choices.get(0);
        Map message = (Map) firstChoice.get("message");
        String content = (String) message.get("content");

        return content;
    }
}
