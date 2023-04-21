package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.DiseasePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseRepository extends JpaRepository<DiseasePO, Long> {

}

