package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CreditLog;
import com.ruoyi.system.domain.UserCredit;
import com.ruoyi.system.mapper.CreditLogMapper;
import com.ruoyi.system.mapper.UserCreditMapper;
import com.ruoyi.system.service.IUserCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户信誉服务实现
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Slf4j
@Service
public class UserCreditServiceImpl implements IUserCreditService {

    private static final Integer DEDUCT_SCORE = 10; // 一次纠纷扣10分
    private static final Integer BAN_THRESHOLD = 0; // 信誉分≤0则禁止接单
    private static final Integer BAN_DAYS = 30; // 禁止接单天数

    @Autowired
    private UserCreditMapper userCreditMapper;

    @Autowired
    private CreditLogMapper creditLogMapper;

    /**
     * 获取用户信誉信息
     */
    @Override
    public UserCredit getUserCredit(Long userId) {
        UserCredit credit = userCreditMapper.selectByUserId(userId);

        // 如果记录不存在，初始化
        if (credit == null) {
            initializeUserCredit(userId);
            credit = userCreditMapper.selectByUserId(userId);
        } else {
            // 检查禁用状态是否过期
            if (credit.isBanned() && credit.getBanUntil().before(new Date())) {
                unbanUser(userId);
                credit.setBanUntil(null);
            }
        }

        return credit;
    }

    /**
     * 初始化用户信誉信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initializeUserCredit(Long userId) {
        UserCredit existingCredit = userCreditMapper.selectByUserId(userId);
        if (existingCredit == null) {
            try {
                userCreditMapper.insertDefault(userId);
                log.info("用户{}信誉信息已初始化", userId);
            } catch (Exception e) {
                log.error("初始化用户{}信誉信息失败", userId, e);
            }
        }
    }

    /**
     * 扣减用户信誉分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductCreditScore(Long userId, Integer score, String reason, Long relatedId) {
        try {
            UserCredit credit = getUserCredit(userId);

            if (credit == null) {
                log.warn("用户{}信誉信息不存在", userId);
                return;
            }

            // 扣分
            int newScore = credit.getCreditScore() - score;
            newScore = Math.max(newScore, 0); // 不能低于0

            credit.setCreditScore(newScore);
            credit.setDisputeCount(credit.getDisputeCount() + 1);
            credit.setLastUpdate(new Date());

            // 如果分数≤0，禁止接单30天
            if (newScore <= BAN_THRESHOLD && !credit.isBanned()) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, BAN_DAYS);
                credit.setBanUntil(calendar.getTime());
                credit.setBanReason("信誉分不足");
            }

            // 更新信誉分
            userCreditMapper.updateById(credit);

            // 记录日志
            CreditLog creditLog = new CreditLog();
            creditLog.setUserId(userId);
            creditLog.setChangeType("dispute_loss");
            creditLog.setChangeScore(-score);
            creditLog.setRelatedId(relatedId);
            creditLog.setReason(reason);
            creditLog.setCreateTime(new Date());
            creditLogMapper.insert(creditLog);

            log.info("用户{}信誉分已扣减{}分，当前分数{}", userId, score, newScore);
        } catch (Exception e) {
            log.error("扣减用户{}信誉分失败", userId, e);
            throw new RuntimeException("扣减信誉分失败", e);
        }
    }

    /**
     * 恢复用户禁用状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbanUser(Long userId) {
        try {
            UserCredit credit = userCreditMapper.selectByUserId(userId);

            if (credit != null && credit.isBanned()) {
                credit.setBanUntil(null);
                credit.setBanReason(null);
                credit.setLastUpdate(new Date());
                userCreditMapper.updateById(credit);

                // 记录日志
                CreditLog creditLog = new CreditLog();
                creditLog.setUserId(userId);
                creditLog.setChangeType("appeal_resolve");
                creditLog.setChangeScore(0);
                creditLog.setReason("禁止期满自动解禁");
                creditLog.setCreateTime(new Date());
                creditLogMapper.insert(creditLog);

                log.info("用户{}已解禁", userId);
            }
        } catch (Exception e) {
            log.error("解禁用户{}失败", userId, e);
        }
    }

    /**
     * 获取用户信誉历史
     */
    @Override
    public List<CreditLog> getCreditLogs(Long userId) {
        return creditLogMapper.selectByUserId(userId);
    }

    /**
     * 检查用户是否被禁用接单
     */
    @Override
    public boolean isUserBanned(Long userId) {
        UserCredit credit = getUserCredit(userId);
        return credit != null && credit.isBanned();
    }
}
