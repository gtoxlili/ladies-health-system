package com.system.ladiesHealth.domain.po;


import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "T_INQUIRY_RECORD")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InquiryRecordPO extends BasePO {
    /*
    1. 会话ID
    2. 角色
    3. 会话内容
     */

    @Column(name = "FTOPICS_ID", length = 32)
    private String topicsId;

    @Column(name = "FROLE", length = 16)
    private String role;

    @Lob
    @Column(name = "FMESSAGE", columnDefinition = "TEXT")
    private String message;

    public InquiryRecordPO(String role, String massage) {
        this.role = role;
        this.message = massage;
    }


}
