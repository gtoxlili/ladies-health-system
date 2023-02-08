package com.system.ladiesHealth.dao;


import com.system.ladiesHealth.domain.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserPO, String>, JpaSpecificationExecutor<UserPO> {

    Optional<UserPO> findByUsernameAndDelTimeIsNull(String username);

    Optional<UserPO> findByEmailAndDelTimeIsNull(String email);

    Optional<UserPO> findByPhoneAndDelTimeIsNull(String phone);

    List<UserPO> findAllByUsernameInAndDelTimeIsNull(List<String> usernames);

}
