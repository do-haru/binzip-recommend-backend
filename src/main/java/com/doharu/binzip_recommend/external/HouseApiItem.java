package com.doharu.binzip_recommend.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseApiItem {

    @JsonProperty("읍면동")
    private String regionName;

    @JsonProperty("리")
    private String regionDetail;

    @JsonProperty("건축면적(제곱미터)")
    private String area;

    @JsonProperty("주용도")
    private String houseType;

    @JsonProperty("등급판정결과")
    private String grade;

    @JsonProperty("관리기관명")
    private String manager;

    @JsonProperty("관리기관 전화번호")
    private String phone;

    @JsonProperty("데이터 기준일")
    private String updateDate;
}
