package com.system.ladiesHealth.service;


import com.github.benmanes.caffeine.cache.Cache;
import com.system.ladiesHealth.domain.pojo.RollbackPOJO;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GlobalService {

    @Autowired
    private Cache<String, RollbackPOJO> rollbackCache;

    /**
     * 回滚用户行为
     */
    public Res<Void> rollback(String id) {
        RollbackPOJO rollbackPOJO = rollbackCache.getIfPresent(id);
        if (rollbackPOJO == null) {
            throw new BusinessException("no corresponding rollback record exists");
        }
        rollbackCache.invalidate(id);
        try {
            rollbackPOJO.rollBack();
        } catch (Exception e) {
            log.error("ID :{} [{}] 行为回滚失败", id, rollbackPOJO.getAction());
            throw new BusinessException(e);
        }
        log.info("ID :{} [{}] 回滚成功", id, rollbackPOJO.getAction());
        return Res.ok();
    }

}
