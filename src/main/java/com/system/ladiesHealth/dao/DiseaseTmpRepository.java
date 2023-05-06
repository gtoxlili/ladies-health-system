package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.DiseaseTmpPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseTmpRepository extends JpaRepository<DiseaseTmpPO, Long> {

}

