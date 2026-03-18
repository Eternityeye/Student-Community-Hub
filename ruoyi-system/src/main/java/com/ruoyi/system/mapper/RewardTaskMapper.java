package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.RewardTask;

/**
 * 悬赏任务 数据层
 *
 * @author ruoyi
 */
public interface RewardTaskMapper
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
    public RewardTask selectTaskById(@Param("taskId") Long taskId);

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
    public int deleteTaskById(@Param("taskId") Long taskId);

    /**
     * 接单
     *
     * @param taskId 任务ID
     * @param takerId 接单者ID
     * @return 结果
     */
    public int takeTask(@Param("taskId") Long taskId, @Param("takerId") Long takerId);

    /**
     * 获取用户发布的任务列表
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    public List<RewardTask> selectTasksByPublisher(@Param("userId") Long userId);

    /**
     * 获取用户接单的任务列表
     *
     * @param takerId 接单者ID
     * @return 任务列表
     */
    public List<RewardTask> selectTasksByTaker(@Param("takerId") Long takerId);
}
