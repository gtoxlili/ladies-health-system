package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.ExerciseRecordPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecordPO, String> {

    // 获取用户记录过的所有颜色
    @Query(value = "select distinct FEXERCISE_TYPE from t_exercise_record where FCREATE_USER_ID = ?1", nativeQuery = true)
    List<String> findDistinctExerciseTypeByCreateUserId(String createUserId);

    // 根据 createUserId 以及日期查询
    List<ExerciseRecordPO> findAllByCreateUserIdAndCreateDateBetween(String createUserId, Date startDate, Date endDate);

}
