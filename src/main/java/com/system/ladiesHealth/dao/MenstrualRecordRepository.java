package com.system.ladiesHealth.dao;

import cn.hutool.core.date.DateUtil;
import com.system.ladiesHealth.domain.po.MenstrualRecordPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MenstrualRecordRepository extends JpaRepository<MenstrualRecordPO, String> {


    Boolean existsByCreateUserIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(String createUserId, Date endTime, Date startTime);

    List<MenstrualRecordPO> findAllByCreateUserIdOrderByStartTimeDesc(String createUserId);

    // 根据 createUserId 以及日期查询
    List<MenstrualRecordPO> findAllByCreateUserIdAndStartTimeBetween(String createUserId, Date startDate, Date endDate);

    // 一年内记录
    default List<MenstrualRecordPO> findOneYearRecord(String createUserId) {
        Date endDate = DateUtil.endOfDay(new Date());
        Date startDate = DateUtil.offsetDay(endDate, -360);
        return findAllByCreateUserIdAndStartTimeBetween(createUserId, startDate, endDate);
    }

}
