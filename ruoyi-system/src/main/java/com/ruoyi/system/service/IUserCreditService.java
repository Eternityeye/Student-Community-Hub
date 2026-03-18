package com.ruoyi.system.service;

import com.ruoyi.system.domain.UserCredit;
import com.ruoyi.system.domain.CreditLog;

import java.util.List;

/**
 * 用户信誉服务接口
 *
 * @author student-hub
 * @date 2026-03-18
 */
public interface IUserCreditService {
    /**
     * 获取用户信誉信息
     *
     * @param userId 用户ID
     * @return 信誉信息
     */
    UserCredit getUserCredit(Long userId);

    /**
     * 初始化用户信誉信息（新用户注册时调用）
     *
     * @param userId 用户ID
     */
    void initializeUserCredit(Long userId);

    /**
     * 扣减用户信誉分
     *
     * @param userId 用户ID
     * @param score 扣分数值
     * @param reason 扣分原因
     * @param relatedId 关联ID(case_id)
     */
    void deductCreditScore(Long userId, Integer score, String reason, Long relatedId);

    /**
     * 恢复用户禁用状态
     *
     * @param userId 用户ID
     */
    void unbanUser(Long userId);

    /**
     * 获取用户信誉历史（最近10条）
     *
     * @param userId 用户ID
     * @return 历史记录
     */
    List<CreditLog> getCreditLogs(Long userId);

    /**
     * 检查用户是否被禁用接单
     *
     * @param userId 用户ID
     * @return true表示被禁用
     */
    boolean isUserBanned(Long userId);
}
