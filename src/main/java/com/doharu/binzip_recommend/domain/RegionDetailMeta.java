package com.doharu.binzip_recommend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDetailMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 핵심 키 (리 or 동)
    @Column(unique = true)
    private String regionKey;

    // 읍면동 (표시용)
    private String regionName;

    // 대표 좌표
    private double latitude;
    private double longitude;

    // 편의시설 수
    private int facilityCount;

    // 평균 가격
    private long avgPrice;
}
