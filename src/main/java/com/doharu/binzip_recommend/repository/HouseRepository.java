package com.doharu.binzip_recommend.repository;

import com.doharu.binzip_recommend.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
}
