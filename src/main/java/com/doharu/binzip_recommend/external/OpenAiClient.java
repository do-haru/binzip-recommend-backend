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
너는 사용자의 입력에서 “명확하게 언급된 조건”만 추출하는 시스템이다.

규칙:
1. 사용자가 직접 언급한 조건만 값을 설정하라.
2. 언급되지 않은 조건은 반드시 null로 설정하라.
3. purpose(목적)는 항상 추론하여 설정하라.
4. targetAges는 명확하게 언급된 경우에만 설정하라.
5. crowd, area, price, facility, condition은
   사용자가 직접 언급한 경우에만 설정하라.
6. 절대 임의로 조건을 채우지 마라.
7. 반드시 JSON만 반환하라. 코드블럭 금지.

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
