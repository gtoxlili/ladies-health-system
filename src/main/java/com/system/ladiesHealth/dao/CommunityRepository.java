package com.system.ladiesHealth.dao;

import com.system.ladiesHealth.domain.po.CommunityPo;
import com.system.ladiesHealth.domain.po.HealthyPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityPo, String> {

    @Query(value = "select * from t_community_theme", nativeQuery = true )
    List<Map<String, String>> getCommunityThemeList();

    @Query(value = "select * from t_community_comment ORDER BY release_time DESC", nativeQuery = true)
    List<Map<String, String>> getCommunityCommentList();

    @Query(value = "select * from t_community_reply", nativeQuery = true)
    List<Map<String, String>> getCommunityReplyList();

    @Query(value = "select * from t_community_notifications", nativeQuery = true)
    List<Map<String, String>> getCommunityNotificationsList();

    @Query(value = "insert into t_community_reply (reply_content, user_name, come_from, comment_id, user_photo, release_time) values (:replyContent, :name, :come_from, :commentId, :user_photo, CURRENT_TIMESTAMP)", nativeQuery = true)
    @Modifying
    @Transactional
    Integer CommunityDiscuss(
            @Param("replyContent") String reply_content,
            @Param("name") String name,
            @Param("come_from") String come_from,
            @Param("user_photo") String user_photo,
            @Param("commentId") Integer comment_id);

    @Query(value = "insert into t_community_comment (content, user_name, come_from, user_id, user_photo, release_time, theme_title, img) values (:content, :name, :come_from, :userId, :user_photo, CURRENT_TIMESTAMP, :theme_title, :img)", nativeQuery = true)
    @Modifying
    @Transactional
    Integer CommunityRecommended(
            @Param("content") String content,
            @Param("name") String name,
            @Param("come_from") String come_from,
            @Param("user_photo") String user_photo,
            @Param("theme_title") String theme_title,
            @Param("img") String img,
            @Param("userId") Integer user_id);

    @Query(value = "select * from t_community_commentlike WHERE user_id = :user_id", nativeQuery = true)
    List<Map<String, String>> getCommunityLikesList(@Param("user_id") Integer user_id);

    @Query(value = "select * from t_community_commentlike", nativeQuery = true)
    List<Map<String, String>> getAllLikesList();

    @Query(value = "insert into t_community_commentlike (comment_id, user_id) values (:commentId, :user_id)", nativeQuery = true)
    @Modifying
    @Transactional
    Integer like(
            @Param("commentId") Integer comment_id,
            @Param("user_id") Integer user_id
    );

    @Query(value = "DELETE FROM t_community_commentlike WHERE comment_id = :comment_id AND user_id = :user_id", nativeQuery = true)
    @Modifying
    @Transactional
    Integer cancellike(
            @Param("comment_id") Integer comment_id,
            @Param("user_id") Integer user_id
    );
}
