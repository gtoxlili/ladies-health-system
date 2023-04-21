package com.system.ladiesHealth.dao;

import cn.hutool.core.date.DateUtil;
import com.system.ladiesHealth.domain.po.SleepRecordPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SleepRecordRepository extends JpaRepository<SleepRecordPO, String> {

    // 根据 createUserId 以及日期查询
    List<SleepRecordPO> findAllByCreateUserIdAndCreateDateBetween(String createUserId, Date startDate, Date endDate);

    // 根据 createUserId 获取今日创建的记录
    Optional<SleepRecordPO> findFirstByCreateUserIdAndCreateDateBetween(String createUserId, Date startDate, Date endDate);

    default Optional<SleepRecordPO> findTodayRecord(String createUserId) {
        Date startDate = DateUtil.beginOfDay(new Date());
        Date endDate = DateUtil.endOfDay(new Date());
        return findFirstByCreateUserIdAndCreateDateBetween(createUserId, startDate, endDate);
    }
}
