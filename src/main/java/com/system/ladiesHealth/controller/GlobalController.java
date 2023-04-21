package com.system.ladiesHealth.controller;

import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.service.GlobalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "【01】全局接口")
public class GlobalController {

    @Autowired
    private GlobalService globalService;

    @GetMapping("/rollback/{id}")
    @Operation(summary = "回滚接口", description = "提供 RollbackID 以进行回滚行为，有效期由 rollback.expire 决定")
    public Res<Void> rollback(
            @Parameter(description = "回滚 ID", required = true)
            @PathVariable(value = "id") String id
    ) {
        return globalService.rollback(id);
    }

    /*
    心跳接口
     */
    @GetMapping("/checkAuth")
    @Operation(summary = "心跳接口")
    public Res<Void> checkAuth() {
        return Res.ok();
    }
}
