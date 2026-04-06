package com.doharu.binzip_recommend.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regionName;
    private String regionDetail;

    private double area;
    private String houseType;
    private int grade;

    private String manager;
    private String phone;

    private LocalDate updateDate;
}
