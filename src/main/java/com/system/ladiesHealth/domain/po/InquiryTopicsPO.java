package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@Table(name = "T_INQUIRY_TOPICS")
@EqualsAndHashCode(callSuper = true)
public class InquiryTopicsPO extends BasePO {
    /*
    1. 会话主题
    2. 会话ID
     */
    @Column(name = "FTOPICS_TITLE", length = 128)
    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "FTOPICS_ID")
    @OrderBy("createDate ASC")
    private List<InquiryRecordPO> records;

    // 绑定的有关疾病项
    @ManyToMany
    private List<DiseasePO> diseases;

    // 创建主题时刻的身体状况
    @Lob
    @Column(name = "FPHYSICAL_CONDITION", columnDefinition = "TEXT")
    private String physicalCondition;
}
