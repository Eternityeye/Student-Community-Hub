package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TaskReview;

/**
 * 任务评价 数据层
 *
 * @author ruoyi
 */
public interface TaskReviewMapper
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
    public TaskReview selectReviewById(@Param("reviewId") Long reviewId);

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
    public List<TaskReview> selectReviewsByReviewee(@Param("revieweeId") Long revieweeId);

    /**
     * 获取用户的评价统计（平均分、总数、各级数量）
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    public java.util.Map<String, Object> selectReviewStats(@Param("userId") Long userId);

    /**
     * 查询任务的评价
     *
     * @param taskId 任务ID
     * @return 评价列表
     */
    public List<TaskReview> selectReviewsByTask(@Param("taskId") Long taskId);
}
