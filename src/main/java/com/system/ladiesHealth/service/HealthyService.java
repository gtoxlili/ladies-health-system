package com.system.ladiesHealth.service;

import com.system.ladiesHealth.dao.HealthyReposity;
import com.system.ladiesHealth.domain.po.HealthyPo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HealthyService {

    @Autowired
    private HealthyReposity reposity;

    /**
     * 新增健康提醒
     *
     * @param healthyPo
     * @return
     */
    public HealthyPo saveHealthy(HealthyPo healthyPo, Authentication authentication) {
        healthyPo.setCreateTime(new Date());
        healthyPo.setUpdateTime(new Date());
        healthyPo.setStatuc(0);
        healthyPo.setFuserId(authentication.getName());
        return reposity.save(healthyPo);
    }

    /**
     * 删除提醒
     *
     * @param id
     */
    @Transactional
    public int deleteHealthy(String id, String fUserId) {
       return reposity.deleteByFidAndAndFuserId(id, fUserId);
    }

    /**
     * 修改提醒信息
     *
     * @param healthyPo
     * @return
     */
    public HealthyPo upadteHealthy(HealthyPo healthyPo) {
         HealthyPo result = reposity.findById(healthyPo.getFid()).orElse(null);
        if (null == result) {
            return null;
        }
        if(result.getStatuc() != 0){
            return  null ;
        }
        //原则上不允许修改 标题主题
        result.setHdesc(healthyPo.getHdesc());
        result.setType(healthyPo.getType());
        result.setReminderTimeStart(healthyPo.getReminderTimeStart());
        result.setReminderTimeEnd(healthyPo.getReminderTimeEnd());
        result.setType(healthyPo.getType());
        result.setUpdateTime(new Date());
        return reposity.save(result);
    }

    /**
     * 获取分页数据
     *
     * @param pageable
     * @param userId
     * @param statuc
     * @return
     */
    public Page<HealthyPo> getHealthyList(Pageable pageable,
                                          int statuc, String userId) {

         if(statuc == 0){
             return  reposity.findAllByFuserIdOrderByCreateTimeDesc(pageable, userId);
         }

        return reposity.findAllByFuserIdAndStatucOrderByCreateTimeDesc(pageable,userId, statuc);
    }

    /**
     * 更新数据库当前用户的的提醒的状态
     */
    @Transactional
    public int changeHealthy(String name) {
//        reposity
        return reposity.updateHealthyReminder(name);
    }

    /**
     * 完成健康提醒
     * @param fid
     * @param name
     */
    @Transactional
    public void finishHealthy(String fid, String name) {

        reposity.finishHealthy(fid,name ) ;
    }

    public HealthyPo getDetail(String fid) {
        HealthyPo healthyPo = reposity.findById(fid).orElse(null);
        return  healthyPo;
    }
}
