package com.system.ladiesHealth.controller;

import com.system.ladiesHealth.constants.ErrorStatus;
import com.system.ladiesHealth.domain.po.HealthyPo;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.service.HealthyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "【05】健康提醒相关接口")
@RequestMapping("/healthy")
@SecurityRequirement(name = "Bearer Authentication")
public class HealthyController {

    @Autowired
    private HealthyService healthyService;

    @GetMapping("/getHealthy")
    @Operation(summary = "获取当前用户所有的提醒")
    public Res<Page<HealthyPo>> getHealthyList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int statuc,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);

        Page<HealthyPo> healthyList = healthyService.getHealthyList(pageable, statuc, authentication.getName());
        return Res.ok(healthyList);
    }

    @Operation(summary = "删除当前用户指定的提醒")
    @DeleteMapping("/deleteHealthy")

    public Res<Integer> delete(@RequestParam("fid") String fid, Authentication authentication) {
        return Res.ok(healthyService.deleteHealthy(fid, authentication.getName()));
    }


    @PostMapping("/update")
    public Res<HealthyPo> updateHealthy(@RequestBody HealthyPo healthyPo) {

        final HealthyPo po = healthyService.upadteHealthy(healthyPo);
        if (po == null) {
            return Res.fail(ErrorStatus.BUSINESS_EXCEPTION, "当前提醒状态已经发生变化,不允许修改");
        }
        return Res.ok(healthyService.upadteHealthy(healthyPo));
    }


    @PostMapping("/save")
    public Res<HealthyPo> saveHealthy(@RequestBody HealthyPo healthyPo, Authentication authentication) {

        return Res.ok(healthyService.saveHealthy(healthyPo, authentication));
    }

    /**
     * 提供给前端 定时轮询
     *
     * @param authentication
     * @return
     */
    @GetMapping("/getCount")
    public Res<Integer> getCurrentCount(Authentication authentication) {
        return Res.ok(healthyService.changeHealthy(authentication.getName()));
    }

    @GetMapping("/finish")
    public Res finish(@RequestParam("fid") String fid, Authentication authentication) {
        healthyService.finishHealthy(fid, authentication.getName());
        return Res.ok();
    }

    @GetMapping("/detail")
    public Res<HealthyPo> detail(@RequestParam("fid") String fid) {
        final HealthyPo detail = healthyService.getDetail(fid);
        if (detail == null) {
            return Res.fail(ErrorStatus.BUSINESS_EXCEPTION, "查询详情数据失败!");
        }
        return Res.ok(detail);
    }
}
