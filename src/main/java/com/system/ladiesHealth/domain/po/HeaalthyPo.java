package com.system.ladiesHealth.domain.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Table(name = "t_healthy")
public class HealthyPo  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "nanoId")
    @GenericGenerator(name = "nanoId", strategy = "com.system.ladiesHealth.utils.generator.NanoIdGenerator")
    @Column(name = "fid")
    private String fid;

    @Column(name = "fuser_id")
    private String fuserId;

    @Column(name = "theme")
    private String theme;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8",shape = JsonFormat.Shape.STRING)
    @Column(name = "create_time")
    private Date createTime;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8",shape = JsonFormat.Shape.STRING)
    @Column(name = "update_time")
    private Date updateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8",shape = JsonFormat.Shape.STRING)
    @Column(name = "reminder_time_start")
    private Date reminderTimeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8",shape = JsonFormat.Shape.STRING)
    @Column(name = "reminder_time_end")
    private Date reminderTimeEnd;


    @Column(name = "type")
    private int type;

    @Column(name = "statuc")
    private int statuc;

    @Column(name = "hdesc")
    private String hdesc;
}