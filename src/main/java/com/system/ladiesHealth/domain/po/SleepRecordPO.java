package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "T_SLEEP_RECORD")
@EqualsAndHashCode(callSuper = true)
public class SleepRecordPO extends BasePO {

    /*
    1. 睡眠时长
     */

    @Column(name = "FSLEEP_DURATION")
    private Double sleepDuration;

}
