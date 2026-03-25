package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.RewardTask;
import com.ruoyi.system.domain.TaskReview;
import com.ruoyi.system.mapper.RewardTaskMapper;
import com.ruoyi.system.service.IRewardTaskService;

/**
 * 悬赏任务 业务层处理
 *
 * @author ruoyi
 */
@Service
public class RewardTaskServiceImpl implements IRewardTaskService
{
    @Autowired
    private RewardTaskMapper rewardTaskMapper;

    /**
     * 查询任务列表
     */
    @Override
    public List<RewardTask> selectTaskList(RewardTask task)
    {
        List<RewardTask> list = rewardTaskMapper.selectTaskList(task);
        for (RewardTask t : list) {
            convertStringToList(t);
        }
        return list;
    }

    /**
     * 查询任务详情
     */
    @Override
    public RewardTask selectTaskById(Long taskId)
    {
        RewardTask task = rewardTaskMapper.selectTaskById(taskId);
        if (task != null) {
            convertStringToList(task);
        }
        return task;
    }

    /**
     * 新增任务
     */
    @Override
    public int insertTask(RewardTask task)
    {
        // 将List转换为逗号分隔字符串存储
        if (task.getImages() != null && !task.getImages().isEmpty()) {
            String imagesStr = task.getImages().stream()
                .filter(image -> image != null && !image.trim().isEmpty())
                .collect(Collectors.joining(","));
            task.setImagesStr(imagesStr.isEmpty() ? null : imagesStr);
        }

        task.setStatus("0");
        return rewardTaskMapper.insertTask(task);
    }

    /**
     * 修改任务
     */
    @Override
    public int updateTask(RewardTask task)
    {
        // 如果更新了images，需要转换为字符串
        if (task.getImages() != null) {
            String imagesStr = task.getImages().stream()
                .filter(image -> image != null && !image.trim().isEmpty())
                .collect(Collectors.joining(","));
            task.setImagesStr(imagesStr.isEmpty() ? null : imagesStr);
        }
        return rewardTaskMapper.updateTask(task);
    }

    /**
     * 删除任务
     */
    @Override
    public int deleteTaskById(Long taskId)
    {
        return rewardTaskMapper.deleteTaskById(taskId);
    }

    /**
     * 接单
     */
    @Override
    @Transactional
    public int takeTask(Long taskId, Long takerId)
    {
        RewardTask task = rewardTaskMapper.selectTaskById(taskId);
        if (task == null || !task.getStatus().equals("0")) {
            return 0; // 任务不存在或已被接单
        }

        int result = rewardTaskMapper.takeTask(taskId, takerId);
        if (result > 0) {
            // 更新任务状态为 "1" (进行中)
            task.setTakerId(takerId);
            task.setStatus("1");
            rewardTaskMapper.updateTask(task);
        }
        return result;
    }

    /**
     * 接单者确认完成
     */
    @Override
    @Transactional
    public int confirmComplete(Long taskId)
    {
        RewardTask task = rewardTaskMapper.selectTaskById(taskId);
        if (task == null || !task.getStatus().equals("1")) {
            return 0; // 任务不存在或状态不对
        }

        task.setStatus("2"); // 设置为 "2" (已完成待确认)
        return rewardTaskMapper.updateTask(task);
    }

    /**
     * 发布者确认完成
     */
    @Override
    @Transactional
    public int publisherConfirm(Long taskId)
    {
        RewardTask task = rewardTaskMapper.selectTaskById(taskId);
        if (task == null || !task.getStatus().equals("2")) {
            return 0; // 任务不存在或状态不对
        }

        task.setStatus("3"); // 设置为 "3" (已关闭)
        return rewardTaskMapper.updateTask(task);
    }

    /**
     * 获取用户发布的任务列表
     */
    @Override
    public List<RewardTask> selectTasksByPublisher(Long userId)
    {
        List<RewardTask> list = rewardTaskMapper.selectTasksByPublisher(userId);
        for (RewardTask t : list) {
            convertStringToList(t);
        }
        return list;
    }

    /**
     * 获取用户接单的任务列表
     */
    @Override
    public List<RewardTask> selectTasksByTaker(Long takerId)
    {
        List<RewardTask> list = rewardTaskMapper.selectTasksByTaker(takerId);
        for (RewardTask t : list) {
            convertStringToList(t);
        }
        return list;
    }

    /**
     * 获取用户发布的任务列表（带评价状态）
     */
    @Override
    public List<RewardTask> selectTasksByPublisherWithReview(Long userId, Long currentUserId)
    {
        List<RewardTask> list = selectTasksByPublisher(userId);
        loadReviewStatus(list, currentUserId);
        return list;
    }

    /**
     * 获取用户接单的任务列表（带评价状态）
     */
    @Override
    public List<RewardTask> selectTasksByTakerWithReview(Long takerId, Long currentUserId)
    {
        List<RewardTask> list = selectTasksByTaker(takerId);
        loadReviewStatus(list, currentUserId);
        return list;
    }

    /**
     * 加载评价状态（判断当前用户是否已对任务评价）
     */
    private void loadReviewStatus(List<RewardTask> tasks, Long currentUserId)
    {
        if (tasks == null || tasks.isEmpty() || currentUserId == null) {
            return;
        }

        for (RewardTask task : tasks) {
            // 只在任务已完成(status='3')时检查评价状态
            if ("3".equals(task.getStatus())) {
                List<TaskReview> reviews = rewardTaskMapper.selectReviewByTaskAndReviewer(task.getTaskId(), currentUserId);
                task.setIsReviewed(!reviews.isEmpty());
            } else {
                task.setIsReviewed(false);
            }
        }
    }

    /**
     * 转换字符串到列表
     */
    private void convertStringToList(RewardTask task)
    {
        if (task == null) {
            return;
        }
        // 转换images字符串到列表
        if (StringUtils.isNotEmpty(task.getImagesStr())) {
            task.setImages(Arrays.stream(task.getImagesStr().split(","))
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList()));
        }
    }
}
