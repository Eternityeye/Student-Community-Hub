package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.DisputeCase;

import java.util.List;

/**
 * 纠纷案件服务接口
 *
 * @author student-hub
 * @date 2026-03-18
 */
public interface IDisputeCaseService {
    /**
     * 创建纠纷
     *
     * @param disputeCase 纠纷信息
     * @return 结果
     */
    AjaxResult createDispute(DisputeCase disputeCase);

    /**
     * 查询纠纷列表
     *
     * @param status 状态
     * @param roleType 角色类型
     * @return 纠纷列表
     */
    List<DisputeCase> selectDisputeList(String status, String roleType);

    /**
     * 查询纠纷详情
     *
     * @param caseId 纠纷ID
     * @return 纠纷信息
     */
    DisputeCase selectDetailById(Long caseId);

    /**
     * 被申请人提交答辩证据
     *
     * @param caseId 纠纷ID
     * @param description 答辩描述
     * @param evidenceImages 证据图片
     * @return 结果
     */
    AjaxResult submitDefenseEvidence(Long caseId, String description, String evidenceImages);

    /**
     * 处理纠纷（管理员裁定）
     *
     * @param caseId 纠纷ID
     * @param status 裁定状态（1 or 2）
     * @param reason 裁定理由
     * @return 结果
     */
    AjaxResult handleDispute(Long caseId, String status, String reason);

    /**
     * 检查任务是否已有纠纷
     *
     * @param taskId 任务ID
     * @return true表示存在
     */
    boolean checkDisputeExists(Long taskId);
}
