package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "T_SIGN_INFO")
@EqualsAndHashCode(callSuper = true)
public class SignInfoPO extends BasePO {

    /*
    1. 年龄
    2. 身高
    3. 体重
    5. 血压
    6. 心率
     */

    @Column(name = "FAGE")
    private Integer age;

    @Column(name = "FHEIGHT")
    private Integer height;

    @Column(name = "FWEIGHT")
    private Integer weight;

    @Column(name = "FBLOOD_PRESSURE", length = 20)
    private String bloodPressure;

    @Column(name = "FHEART_RATE")
    private Integer heartRate;


}
