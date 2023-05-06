package com.system.ladiesHealth.domain.po;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "T_DISEASE_TMP")
public class DiseaseTmpPO {
    @Id
    @Column(name = "FID", nullable = false)
    private Long id;

    @Lob
    @Column(name = "FCONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(name = "FVECTOR", nullable = false, columnDefinition = "TEXT")
    private String vector;
}
