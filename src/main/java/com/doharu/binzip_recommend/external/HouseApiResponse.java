package com.doharu.binzip_recommend.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HouseApiResponse {

    private int page;           // 페이지 번호
    private int perPage;        // 한 페이지의 가져올 데이터 개수
    private int totalCount;     // 전체 데이터 개수

    private List<HouseApiItem> data;    // 실제 빈집 데이터 리스트
}
