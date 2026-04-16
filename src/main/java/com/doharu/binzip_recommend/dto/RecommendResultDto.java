package com.doharu.binzip_recommend.dto;

import com.doharu.binzip_recommend.domain.RecommendHouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RecommendResultDto {

    private RecommendHouse house;
    private List<String> reasons;
    private String reasonText;

    @Override
    public String toString() {
        return "RecommendResultDto{" +
                "house=" + house +
                ", reasons=" + reasons +
                ", reasonText='" + reasonText + '\'' +
                '}';
    }
}
