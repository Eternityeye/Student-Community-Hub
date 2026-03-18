package com.ruoyi.system.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.UserCredit;
import com.ruoyi.system.mapper.UserCreditMapper;
import com.ruoyi.system.service.IUserCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 自动解禁定时任务
 * 每天凌晨2点执行，检查是否有用户禁止期满需要解禁
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Slf4j
@Component
public class CreditUnbanTask {

    @Autowired
    private UserCreditMapper userCreditMapper;

    @Autowired
    private IUserCreditService userCreditService;

    /**
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeUnbanTask() {
        log.info("开始执行自动解禁任务");
        try {
            // 查询所有禁止期满的用户
            QueryWrapper<UserCredit> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("ban_until")
                .lt("ban_until", new Date());

            List<UserCredit> bannedUsers = userCreditMapper.selectList(queryWrapper);

            if (bannedUsers != null && !bannedUsers.isEmpty()) {
                for (UserCredit credit : bannedUsers) {
                    userCreditService.unbanUser(credit.getUserId());
                }
                log.info("共解禁{}个用户", bannedUsers.size());
            } else {
                log.info("无需解禁的用户");
            }
        } catch (Exception e) {
            log.error("自动解禁任务执行失败", e);
        }
    }
}
