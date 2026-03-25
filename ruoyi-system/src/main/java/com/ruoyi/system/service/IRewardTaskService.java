package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.RewardTask;

/**
 * 悬赏任务 服务层
 *
 * @author ruoyi
 */
public interface IRewardTaskService
{
    /**
     * 查询任务列表
     *
     * @param task 任务信息
     * @return 任务列表
     */
    public List<RewardTask> selectTaskList(RewardTask task);

    /**
     * 查询任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    public RewardTask selectTaskById(Long taskId);

    /**
     * 新增任务
     *
     * @param task 任务信息
     * @return 结果
     */
    public int insertTask(RewardTask task);

    /**
     * 修改任务
     *
     * @param task 任务信息
     * @return 结果
     */
    public int updateTask(RewardTask task);

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteTaskById(Long taskId);

    /**
     * 接单
     *
     * @param taskId 任务ID
     * @param takerId 接单者ID
     * @return 结果
     */
    public int takeTask(Long taskId, Long takerId);

    /**
     * 接单者确认完成
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int confirmComplete(Long taskId);

    /**
     * 发布者确认完成
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int publisherConfirm(Long taskId);

    /**
     * 获取用户发布的任务列表
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    public List<RewardTask> selectTasksByPublisher(Long userId);

    /**
     * 获取用户接单的任务列表
     *
     * @param takerId 接单者ID
     * @return 任务列表
     */
    public List<RewardTask> selectTasksByTaker(Long takerId);

    /**
     * 获取用户发布的任务列表（带评价状态）
     *
     * @param userId 用户ID
     * @param currentUserId 当前用户ID（用于判断是否已评价）
     * @return 任务列表
     */
    public List<RewardTask> selectTasksByPublisherWithReview(Long userId, Long currentUserId);

    /**
     * 获取用户接单的任务列表（带评价状态）
     *
     * @param takerId 接单者ID
     * @param currentUserId 当前用户ID（用于判断是否已评价）
     * @return 任务列表
     */
    public List<RewardTask> selectTasksByTakerWithReview(Long takerId, Long currentUserId);
}
