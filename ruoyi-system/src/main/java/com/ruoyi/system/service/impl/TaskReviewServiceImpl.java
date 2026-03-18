package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TaskReview;
import com.ruoyi.system.mapper.TaskReviewMapper;
import com.ruoyi.system.service.ITaskReviewService;

/**
 * 任务评价 业务层处理
 *
 * @author ruoyi
 */
@Service
public class TaskReviewServiceImpl implements ITaskReviewService
{
    @Autowired
    private TaskReviewMapper taskReviewMapper;

    /**
     * 查询评价列表
     */
    @Override
    public List<TaskReview> selectReviewList(TaskReview review)
    {
        return taskReviewMapper.selectReviewList(review);
    }

    /**
     * 查询评价详情
     */
    @Override
    public TaskReview selectReviewById(Long reviewId)
    {
        return taskReviewMapper.selectReviewById(reviewId);
    }

    /**
     * 新增评价
     */
    @Override
    public int insertReview(TaskReview review)
    {
        return taskReviewMapper.insertReview(review);
    }

    /**
     * 查询用户收到的评价列表
     */
    @Override
    public List<TaskReview> selectReviewsByReviewee(Long revieweeId)
    {
        return taskReviewMapper.selectReviewsByReviewee(revieweeId);
    }

    /**
     * 获取用户的评价统计（平均分、总数）
     */
    @Override
    public Map<String, Object> selectReviewStats(Long userId)
    {
        return taskReviewMapper.selectReviewStats(userId);
    }

    /**
     * 查询任务的评价
     */
    @Override
    public List<TaskReview> selectReviewsByTask(Long taskId)
    {
        return taskReviewMapper.selectReviewsByTask(taskId);
    }
}
