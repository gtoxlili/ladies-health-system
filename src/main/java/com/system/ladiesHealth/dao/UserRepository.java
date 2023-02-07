package com.system.ladiesHealth.dao;


import com.system.ladiesHealth.domain.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserPO, String>, JpaSpecificationExecutor<UserPO> {

    Optional<UserPO> findByUsernameAndDelFlag(String username, Long delFlag);

    default Optional<UserPO> findByUsername(String username) {
        return findByUsernameAndDelFlag(username, 0L);
    }

    Optional<UserPO> findByEmailAndDelFlag(String email, Long delFlag);

    default Optional<UserPO> findByEmail(String email) {
        return findByEmailAndDelFlag(email, 0L);
    }

    Optional<UserPO> findByPhoneAndDelFlag(String phone, Long delFlag);

    default Optional<UserPO> findByPhone(String phone) {
        return findByPhoneAndDelFlag(phone, 0L);
    }

    List<UserPO> findAllByUsernameInAndDelFlag(List<String> usernames, Long delFlag);

    default List<UserPO> findAllByUsernameIn(List<String> usernames) {
        return findAllByUsernameInAndDelFlag(usernames, 0L);
    }
}
