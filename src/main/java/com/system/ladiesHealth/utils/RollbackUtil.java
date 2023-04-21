package com.system.ladiesHealth.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.system.ladiesHealth.domain.pojo.RollbackPOJO;
import com.system.ladiesHealth.domain.vo.OperateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RollbackUtil {

    @Autowired
    private Cache<String, RollbackPOJO> rollbackCache;

    public OperateVO builder(String actionName, RollbackPOJO.Action action) {
        String nanoid = NanoIdUtils.randomNanoId();
        rollbackCache.put(nanoid,
                RollbackPOJO.builder()
                        .action(actionName)
                        .rollback(action)
                        .build());
        return OperateVO.builder()
                .action(actionName)
                .rollbackUrl("/rollback/" + nanoid)
                .build();
    }

}
