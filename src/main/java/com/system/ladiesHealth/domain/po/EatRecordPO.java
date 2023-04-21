package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "T_EAT_RECORD")
@EqualsAndHashCode(callSuper = true)
public class EatRecordPO extends BasePO {

    /*
    1. 食物名称
    2. 食物类型
    3. 食物热量
     */

    @Column(name = "FEAT_FOOD_NAME", length = 128)
    private String eatFoodName;

    @Column(name = "FEAT_FOOD_TYPE", length = 128)
    private String eatFoodType;

    @Column(name = "FEAT_FOOD_CALORIE")
    private Integer eatFoodCalorie;
}
