package com.system.ladiesHealth.domain.po;

import com.system.ladiesHealth.constants.RoleEnum;
import com.system.ladiesHealth.domain.po.base.BasePO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "T_USER",
        uniqueConstraints = {
                @UniqueConstraint(name = "username", columnNames = {"FUSERNAME", "FDEL_FLAG"}),
                @UniqueConstraint(name = "email", columnNames = {"FEMAIL", "FDEL_FLAG"}),
                @UniqueConstraint(name = "phone", columnNames = {"FPHONE", "FDEL_FLAG"})
        }
)
@EqualsAndHashCode(callSuper = true)
public class UserPO extends BasePO {

    @Column(name = "FUSERNAME", nullable = false, length = 20)
    private String username;

    @Column(name = "FPASSWORD", nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "FROLE", nullable = false)
    private RoleEnum role;

    @Column(name = "FEMAIL", length = 50)
    private String email;

    @Column(name = "FPHONE", length = 11)
    private String phone;
}
