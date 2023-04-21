package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Entity
@Data
@Table(name = "T_MENSTRUAL_RECORD")
@EqualsAndHashCode(callSuper = true)
public class MenstrualRecordPO extends BasePO {

    /*
    时间范围 只需要日期
    流量 ： 1 - 5
    疼痛 ： 1 - 5
    便秘 ： true / false
    恶心 ： true / false
    发冷 ： true / false
    膀胱失禁 ： true / false
    潮热 ： true / false
     */

    @Temporal(TemporalType.DATE)
    @Column(name = "FMENSTRUAL_START_TIME")
    private Date startTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "FMENSTRUAL_END_TIME")
    private Date endTime;

    @Column(name = "FMENSTRUAL_FLOW")
    private Double flow;

    @Column(name = "FMENSTRUAL_PAIN")
    private Double pain;

    @Column(name = "FMENSTRUAL_CONSTIPATION")
    private Boolean constipation;

    @Column(name = "FMENSTRUAL_NAUSEA")
    private Boolean nausea;

    @Column(name = "FMENSTRUAL_COLD")
    private Boolean cold;

    @Column(name = "FMENSTRUAL_INCONTINENCE")
    private Boolean incontinence;

    @Column(name = "FMENSTRUAL_HOT")
    private Boolean hot;

}
