package com.doharu.binzip_recommend.service;

import lombok.Getter;

@Getter
public class Weight {

    public double crowd;
    public double facility;
    public double price;
    public double condition;
    public double area;

    public String targetAge;

    public Weight(double crowd, double facility, double price, double condition, double area, String targetAge) {
        this.crowd = crowd;
        this.facility = facility;
        this.price = price;
        this.condition = condition;
        this.area = area;
        this.targetAge = targetAge;
    }
}
