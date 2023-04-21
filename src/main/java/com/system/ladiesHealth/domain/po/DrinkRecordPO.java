package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "T_DRINK_RECORD")
@EqualsAndHashCode(callSuper = true)
public class DrinkRecordPO extends BasePO {

    /*
    1. 饮水量
    2. 饮水次数
     */

    @Column(name = "FDRINK_VOLUME", nullable = false)
    private Double drinkVolume;

    @Column(name = "FDRINK_TIMES", nullable = false)
    private Integer drinkTimes;

}
