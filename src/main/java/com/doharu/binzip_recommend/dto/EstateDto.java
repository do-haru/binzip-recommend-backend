package com.doharu.binzip_recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstateDto {

    private String name;     // 사업장상호
    private String address;  // 도로명주소

    @Override
    public String toString() {
        return "EstateDto{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}