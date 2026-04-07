package com.doharu.binzip_recommend.repository;

import com.doharu.binzip_recommend.domain.RegionDetailMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionDetailMetaRepository extends JpaRepository<RegionDetailMeta, Long> {
    Optional<RegionDetailMeta> findByRegionKey(String regionKey);
}
