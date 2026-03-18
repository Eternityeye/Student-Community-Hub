package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.TaskReview;

/**
 * 任务评价 服务层
 *
 * @author ruoyi
 */
public interface ITaskReviewService
{
    /**
     * 查询评价列表
     *
     * @param review 评价信息
     * @return 评价列表
     */
    public List<TaskReview> selectReviewList(TaskReview review);

    /**
     * 查询评价详情
     *
     * @param reviewId 评价ID
     * @return 评价详情
     */
    public TaskReview selectReviewById(Long reviewId);

    /**
     * 新增评价
     *
     * @param review 评价信息
     * @return 结果
     */
    public int insertReview(TaskReview review);

    /**
     * 查询用户收到的评价列表
     *
     * @param revieweeId 被评价者ID
     * @return 评价列表
     */
    public List<TaskReview> selectReviewsByReviewee(Long revieweeId);

    /**
     * 获取用户的评价统计（平均分、总数）
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    public Map<String, Object> selectReviewStats(Long userId);

    /**
     * 查询任务的评价
     *
     * @param taskId 任务ID
     * @return 评价列表
     */
    public List<TaskReview> selectReviewsByTask(Long taskId);
}
