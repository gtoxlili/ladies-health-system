package com.system.ladiesHealth.controller;

import com.system.ladiesHealth.constants.ErrorStatus;
import com.system.ladiesHealth.domain.po.CommunityPo;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "【05】社区交流相关接口")
@RequestMapping("/community")
@SecurityRequirement(name = "Bearer Authentication")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/themelist")
    public Res<List<Map<String, String>>> getThemeList(@RequestBody Map map) {
        return Res.ok(
                communityService.getThemeList(map)
        );
    }

    @PostMapping("/commentlist")
    public Res<List<Map<String, String>>> getCommentList(@RequestBody Map map) {
        return Res.ok(
                communityService.getCommentList(map)
        );
    }

    @PostMapping("/replylist")
    public Res<List<Map<String, String>>> getReplyList(@RequestBody Map map) {
        return Res.ok(
                communityService.getReplyList(map)
        );
    }

    @PostMapping("/notificatonslist")
    public Res<List<Map<String, String>>> getNotificationsList(@RequestBody Map map) {
        return Res.ok(
                communityService.getNotificationsList(map)
        );
    }



    @PostMapping("/discuss")
    public Res<Integer> Discuss(@RequestBody Map map, Authentication authentication) {
        return Res.ok(
                communityService.Discuss(map, authentication.getName())
        );
    }

    @PostMapping("/recommended")
    public Res<Integer> Recommended(@RequestBody Map map, Authentication authentication) {
        return Res.ok(
                communityService.Recommended(map, authentication.getName())
        );
    }

    @PostMapping("/likesList")
    public Res<List<Map<String, String>>> getLikesList(@RequestBody Map map, Authentication authentication) {
        return Res.ok(
                communityService.getCommunityLikesList(map)
        );
    }

    @PostMapping("/alllikesList")
    public Res<List<Map<String, String>>> getAllLikesList(@RequestBody Map map, Authentication authentication) {
        return Res.ok(
                communityService.getAllLikesList(map)
        );
    }

    @PostMapping("/like")
    public Res<Integer> like(@RequestBody Map map, Authentication authentication) {
        return Res.ok(
                communityService.like(map)
        );
    }

    @PostMapping("/cancelLike")
    public Res<Integer> cancellike(@RequestBody Map map, Authentication authentication) {
        return Res.ok(
                communityService.cancellike(map)
        );
    }
}
