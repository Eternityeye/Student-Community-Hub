package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DisputeCase;
import com.ruoyi.system.domain.RewardTask;
import com.ruoyi.system.mapper.DisputeCaseMapper;
import com.ruoyi.system.mapper.RewardTaskMapper;
import com.ruoyi.system.service.IDisputeCaseService;
import com.ruoyi.system.service.IUserCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 纠纷案件服务实现
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Slf4j
@Service
public class DisputeCaseServiceImpl implements IDisputeCaseService {

    @Autowired
    private DisputeCaseMapper disputeCaseMapper;

    @Autowired
    private RewardTaskMapper rewardTaskMapper;

    @Autowired
    private IUserCreditService userCreditService;

    /**
     * 创建纠纷
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createDispute(DisputeCase disputeCase) {
        // 检查任务是否已有纠纷
        if (checkDisputeExists(disputeCase.getTaskId())) {
            return AjaxResult.error("该任务已存在纠纷，请等待处理");
        }

        // 设置发起者为当前用户
        Long currentUserId = SecurityUtils.getUserId();
        disputeCase.setPlaintiffId(currentUserId);
        disputeCase.setStatus("0"); // 待审理
        disputeCase.setCreateTime(new Date());
        disputeCase.setCreateBy(SecurityUtils.getUsername());

        try {
            disputeCaseMapper.insert(disputeCase);
            // 更新任务状态为"纠纷中"
            RewardTask task = rewardTaskMapper.selectTaskById(disputeCase.getTaskId());
            if (task != null) {
                task.setStatus("4");
                rewardTaskMapper.updateTask(task);
            }
            return AjaxResult.success("纠纷已提交");
        } catch (Exception e) {
            log.error("创建纠纷失败", e);
            return AjaxResult.error("创建纠纷失败");
        }
    }

    /**
     * 查询纠纷列表
     */
    @Override
    public List<DisputeCase> selectDisputeList(String status, String roleType) {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            log.warn("无法获取当前用户ID");
            return new java.util.ArrayList<>();
        }
        return disputeCaseMapper.selectDisputeList(currentUserId, status, roleType);
    }

    /**
     * 查询纠纷详情
     */
    @Override
    public DisputeCase selectDetailById(Long caseId) {
        DisputeCase caseDetail = disputeCaseMapper.selectDetailById(caseId);

        if (caseDetail != null) {
            // 判断当前用户的角色
            Long currentUserId = SecurityUtils.getUserId();
            if (currentUserId.equals(caseDetail.getPlaintiffId())) {
                caseDetail.setRoleType("plaintiff");
            } else if (currentUserId.equals(caseDetail.getDefendantId())) {
                caseDetail.setRoleType("defendant");
            }
        }

        return caseDetail;
    }

    /**
     * 被申请人提交答辩证据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submitDefenseEvidence(Long caseId, String description, String evidenceImages) {
        DisputeCase caseDetail = disputeCaseMapper.selectById(caseId);

        if (caseDetail == null) {
            return AjaxResult.error("纠纷不存在");
        }

        // 检查是否为被申请人
        Long currentUserId = SecurityUtils.getUserId();
        if (!currentUserId.equals(caseDetail.getDefendantId())) {
            return AjaxResult.error("无权限提交答辩");
        }

        // 检查纠纷状态是否为待审理
        if (!"0".equals(caseDetail.getStatus())) {
            return AjaxResult.error("纠纷已处理，无法再提交答辩");
        }

        // 更新答辩信息
        caseDetail.setDefendantDescription(description);
        caseDetail.setDefendantEvidenceImages(evidenceImages);
        caseDetail.setUpdateTime(new Date());
        caseDetail.setUpdateBy(SecurityUtils.getUsername());

        try {
            disputeCaseMapper.updateById(caseDetail);
            return AjaxResult.success("答辩已提交");
        } catch (Exception e) {
            log.error("提交答辩失败", e);
            return AjaxResult.error("提交答辩失败");
        }
    }

    /**
     * 处理纠纷（管理员裁定）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult handleDispute(Long caseId, String status, String reason) {
        DisputeCase caseDetail = disputeCaseMapper.selectById(caseId);

        if (caseDetail == null) {
            return AjaxResult.error("纠纷不存在");
        }

        if (!"0".equals(caseDetail.getStatus())) {
            return AjaxResult.error("纠纷已处理");
        }

        // 确定责任人并扣分
        Long penalizedUserId = null;
        if ("1".equals(status)) {
            // 申请人责任
            penalizedUserId = caseDetail.getPlaintiffId();
        } else if ("2".equals(status)) {
            // 被申请人责任
            penalizedUserId = caseDetail.getDefendantId();
        } else if ("3".equals(status)) {
            // 驳回，无人责任
            penalizedUserId = null;
        } else {
            return AjaxResult.error("无效的裁定状态");
        }

        // 更新纠纷状态
        caseDetail.setStatus(status);
        caseDetail.setHandlerId(SecurityUtils.getUserId());
        caseDetail.setHandleReason(reason);
        caseDetail.setHandleTime(new Date());
        caseDetail.setUpdateTime(new Date());
        caseDetail.setUpdateBy(SecurityUtils.getUsername());

        try {
            disputeCaseMapper.updateById(caseDetail);

            // 如果有责任人，扣分
            if (penalizedUserId != null) {
                userCreditService.deductCreditScore(penalizedUserId, 10,
                    "纠纷案件" + caseId + "被判定有责任", caseId);
            }

            return AjaxResult.success("纠纷已处理");
        } catch (Exception e) {
            log.error("处理纠纷失败", e);
            return AjaxResult.error("处理纠纷失败");
        }
    }

    /**
     * 检查任务是否已有纠纷
     */
    @Override
    public boolean checkDisputeExists(Long taskId) {
        List<DisputeCase> cases = disputeCaseMapper.selectByTaskId(taskId);
        return cases != null && !cases.isEmpty();
    }

    /**
     * 管理员查询所有纠纷列表
     */
    @Override
    public List<DisputeCase> selectAllDisputeList(String status) {
        return disputeCaseMapper.selectAllDisputeList(status);
    }
}
