package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "T_EXERCISE_RECORD")
@EqualsAndHashCode(callSuper = true)
public class ExerciseRecordPO extends BasePO {

    /*
    1. 运动类型
    2. 运动时长
     */

    @Column(name = "FEXERCISE_TYPE", length = 128)
    private String exerciseType;

    @Column(name = "FEXERCISE_DURATION")
    private Double exerciseDuration;

}
