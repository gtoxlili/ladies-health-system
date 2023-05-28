package com.system.ladiesHealth.service;

import com.system.ladiesHealth.dao.CommunityRepository;
import com.system.ladiesHealth.domain.po.CommunityPo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository repository;

    public List<Map<String, String>> getThemeList(Map map) {
        return  repository.getCommunityThemeList();
    }

    public List<Map<String, String>> getCommentList(Map map) {
        return  repository.getCommunityCommentList();
    }

    public List<Map<String, String>> getReplyList(Map map) {
        return  repository.getCommunityReplyList();
    }

    public List<Map<String, String>> getNotificationsList(Map map) {
        return  repository.getCommunityNotificationsList();
    }

    public Integer Discuss(Map map, String name) {
        return  repository.CommunityDiscuss(
                (String) map.get("reply_content"),
                (String) map.get("user_name"),
                (String) map.get("come_from"),
                (String) map.get("user_photo"),
                (Integer) map.get("comment_id")
        );
    }

    public Integer Recommended(Map map, String name) {
        return  repository.CommunityRecommended(
                (String) map.get("content"),
                (String) map.get("user_name"),
                (String) map.get("come_from"),
                (String) map.get("user_photo"),
                (String) map.get("theme_title"),
                (String) map.get("img"),
                (Integer) map.get("user_id")
        );
    }

    public List<Map<String, String>> getCommunityLikesList(Map map) {
        return  repository.getCommunityLikesList(
                (Integer) map.get("user_id")
        );
    }

    public List<Map<String, String>> getAllLikesList(Map map) {
        return  repository.getAllLikesList();
    }

    public Integer like(Map map) {
        return  repository.like(
                (Integer) map.get("comment_id"),
                (Integer) map.get("user_id")
        );
    }

    public Integer cancellike(Map map) {
        return  repository.cancellike(
                (Integer) map.get("comment_id"),
                (Integer) map.get("user_id")
        );
    }
}

