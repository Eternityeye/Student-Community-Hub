package com.ruoyi.system.task;

import com.ruoyi.system.domain.UserCredit;
import com.ruoyi.system.mapper.UserCreditMapper;
import com.ruoyi.system.service.IUserCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CreditUnbanTask {

    @Autowired
    private UserCreditMapper userCreditMapper;

    @Autowired
    private IUserCreditService userCreditService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void executeUnbanTask() {
        log.info("开始执行自动解禁任务");
        try {
            List<UserCredit> bannedUsers = userCreditMapper.selectExpiredBans();
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
