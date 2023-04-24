package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.InquiryTopicsPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryTopicsRepository extends JpaRepository<InquiryTopicsPO, String> {

    List<InquiryTopicsPO> findAllByCreateUserIdAndDelTimeIsNull(String userId);

}
