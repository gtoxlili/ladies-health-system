package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.SignInfoPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignInfoRepository extends JpaRepository<SignInfoPO, String> {

    Optional<SignInfoPO> findByCreateUserId(String createUserId);

}
