package com.system.ladiesHealth.domain.po.base;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@RequiredArgsConstructor
@MappedSuperclass
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class BasePO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "nanoId")
    @GenericGenerator(name = "nanoId", strategy = "com.system.ladiesHealth.utils.generator.NanoIdGenerator")
    @Column(name = "FID", nullable = false, updatable = false)
    private String id;

    @CreatedBy
    @Column(name = "FCREATE_USER_ID", updatable = false)
    private String createUserId;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "FCREATE_DATE", updatable = false)
    private Date createDate;

    @LastModifiedBy
    @Column(name = "FUPDATE_USER_ID")
    private String updateUserId;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "FUPDATE_DATE")
    private Date updateDate;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FDEL_TIME", insertable = false)
    private Date delTime;
}


