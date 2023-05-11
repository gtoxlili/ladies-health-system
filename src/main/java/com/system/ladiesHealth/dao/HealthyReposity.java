package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.HealthyPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthyReposity extends JpaRepository<HealthyPo, String> {

    Page<HealthyPo> findAllByFuserIdOrderByCreateTimeDesc(Pageable pageable,
                                                          String fUerId);


    Page<HealthyPo> findAllByFuserIdAndStatucOrderByCreateTimeDesc(Pageable pageable,
                                                                   String fUerId, int statuc);
    // 0 待启动, 1 已经提醒，处于待办中。 2: 处于已办中

    @Modifying
    @Query(value = "update t_healthy set statuc=1 where statuc=0 and fuser_id=:userId and type =0 and  NOW() BETWEEN reminder_time_start AND reminder_time_end", nativeQuery = true)
    int updateHealthyReminder(@Param("userId") String userId);

    @Modifying
    int deleteByFidAndAndFuserId(String fid, String fUserId);

    @Modifying
    @Query(value = "update t_healthy set statuc =2 where statuc =1 and fid=:fid and fuser_id=:name ", nativeQuery = true)
    void finishHealthy(String fid, String name);
}
