package com.doharu.binzip_recommend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 빈집 데이터 고유 식별자(Primary Key)

    private String regionName;      // 행정구역 이름(예: 풍기읍, 가흥동)
    private String regionDetail;    // 세부 행정구역(리 단위 정보, 동인 경우 null)

    private double area;            // 건축물 면적
    private String houseType;       // 주택 유형(예: 단독, 기타)
    private int grade;              // 빈집 상태 등급

    private String manager;         // 관리 기관명(예: 영주시청)
    private String phone;           // 관리 기관 연락처

    private LocalDate updateDate;   // 데이터 기준일
}
