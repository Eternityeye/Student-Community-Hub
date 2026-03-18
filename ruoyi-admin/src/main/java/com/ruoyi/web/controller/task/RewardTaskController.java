package com.ruoyi.web.controller.task;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.RewardTask;
import com.ruoyi.system.domain.TaskReview;
import com.ruoyi.system.service.IRewardTaskService;
import com.ruoyi.system.service.ITaskReviewService;

/**
 * 悬赏任务 控制层
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/task")
public class RewardTaskController extends BaseController
{
    @Autowired
    private IRewardTaskService rewardTaskService;

    @Autowired
    private ITaskReviewService taskReviewService;

    /**
     * 查询任务列表
     */
    @GetMapping("/list")
    public TableDataInfo list(RewardTask task)
    {
        startPage();
        List<RewardTask> list = rewardTaskService.selectTaskList(task);
        return getDataTable(list);
    }

    /**
     * 查询我发布的任务列表
     */
    @GetMapping("/my/published")
    public TableDataInfo myPublished()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        List<RewardTask> list = rewardTaskService.selectTasksByPublisher(userId);
        return getDataTable(list);
    }

    /**
     * 查询我接单的任务列表
     */
    @GetMapping("/my/taken")
    public TableDataInfo myTaken()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        List<RewardTask> list = rewardTaskService.selectTasksByTaker(userId);
        return getDataTable(list);
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        RewardTask task = rewardTaskService.selectTaskById(taskId);
        if (task != null) {
            // 加载该任务的评价
            List<TaskReview> reviews = taskReviewService.selectReviewsByTask(taskId);
            task.setMyReviews(reviews);
        }
        return AjaxResult.success(task);
    }

    /**
     * 新增任务
     */
    @PostMapping
    @Log(title = "悬赏任务", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody RewardTask task)
    {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return AjaxResult.error("任务标题不能为空");
        }
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            return AjaxResult.error("任务描述不能为空");
        }
        if (task.getReward() == null || task.getReward().trim().isEmpty()) {
            return AjaxResult.error("悬赏说明不能为空");
        }

        task.setUserId(SecurityUtils.getUserId());
        task.setCreateBy(SecurityUtils.getUsername());
        int rows = rewardTaskService.insertTask(task);
        return toAjax(rows);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    @Log(title = "悬赏任务", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable("taskId") Long taskId)
    {
        RewardTask task = rewardTaskService.selectTaskById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        Long currentUserId = SecurityUtils.getUserId();
        if (!task.getUserId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            return AjaxResult.error("只能删除自己的任务");
        }

        if (!"0".equals(task.getStatus()) && !SecurityUtils.isAdmin()) {
            return AjaxResult.error("只能删除待接单的任务");
        }

        int rows = rewardTaskService.deleteTaskById(taskId);
        return toAjax(rows);
    }

    /**
     * 接单
     */
    @PostMapping("/take/{taskId}")
    @Log(title = "悬赏任务", businessType = BusinessType.UPDATE)
    public AjaxResult takeTask(@PathVariable("taskId") Long taskId)
    {
        Long takerId = SecurityUtils.getUserId();
        RewardTask task = rewardTaskService.selectTaskById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        if (!task.getStatus().equals("0")) {
            return AjaxResult.error("任务已被接单或已关闭");
        }

        if (task.getUserId().equals(takerId)) {
            return AjaxResult.error("不能接自己发布的任务");
        }

        int rows = rewardTaskService.takeTask(taskId, takerId);
        return toAjax(rows);
    }

    /**
     * 接单者确认完成
     */
    @PutMapping("/complete/{taskId}")
    @Log(title = "悬赏任务", businessType = BusinessType.UPDATE)
    public AjaxResult completeTask(@PathVariable("taskId") Long taskId)
    {
        Long currentUserId = SecurityUtils.getUserId();
        RewardTask task = rewardTaskService.selectTaskById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        if (!task.getTakerId().equals(currentUserId)) {
            return AjaxResult.error("只有接单者才能确认完成");
        }

        int rows = rewardTaskService.confirmComplete(taskId);
        return toAjax(rows);
    }

    /**
     * 发布者确认完成
     */
    @PutMapping("/confirm/{taskId}")
    @Log(title = "悬赏任务", businessType = BusinessType.UPDATE)
    public AjaxResult confirmTask(@PathVariable("taskId") Long taskId)
    {
        Long currentUserId = SecurityUtils.getUserId();
        RewardTask task = rewardTaskService.selectTaskById(taskId);
        if (task == null) {
            return AjaxResult.error("任务不存在");
        }

        if (!task.getUserId().equals(currentUserId) && !SecurityUtils.isAdmin()) {
            return AjaxResult.error("只有发布者才能确认完成");
        }

        int rows = rewardTaskService.publisherConfirm(taskId);
        return toAjax(rows);
    }

    /**
     * 提交评价
     */
    @PostMapping("/review")
    @Log(title = "任务评价", businessType = BusinessType.INSERT)
    public AjaxResult addReview(@RequestBody TaskReview review)
    {
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return AjaxResult.error("评分必须在1-5之间");
        }

        Long currentUserId = SecurityUtils.getUserId();
        review.setReviewerId(currentUserId);

        int rows = taskReviewService.insertReview(review);
        return toAjax(rows);
    }

    /**
     * 查询我的评价记录（收到的评价）
     */
    @GetMapping("/review/my")
    public TableDataInfo myReviews()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        List<TaskReview> list = taskReviewService.selectReviewsByReviewee(userId);
        return getDataTable(list);
    }

    /**
     * 获取用户评价统计
     */
    @GetMapping("/review/stat/{userId}")
    public AjaxResult getReviewStats(@PathVariable("userId") Long userId)
    {
        java.util.Map<String, Object> stats = taskReviewService.selectReviewStats(userId);
        return AjaxResult.success(stats);
    }
}
