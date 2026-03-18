package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.DisputeCase;
import com.ruoyi.system.service.IDisputeCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 纠纷处理控制器
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Slf4j
@RestController
@RequestMapping("/dispute")
public class DisputeController extends BaseController {

    @Autowired
    private IDisputeCaseService disputeCaseService;

    /**
     * 创建纠纷
     */
    @PostMapping("/create")
    @Log(title = "创建纠纷", businessType = BusinessType.INSERT)
    public AjaxResult createDispute(@RequestBody DisputeCase disputeCase) {
        // 验证任务ID和被申请人ID
        if (disputeCase.getTaskId() == null || disputeCase.getDefendantId() == null) {
            return AjaxResult.error("参数缺失");
        }

        return disputeCaseService.createDispute(disputeCase);
    }

    /**
     * 查询纠纷列表
     */
    @GetMapping("/list")
    public TableDataInfo selectDisputeList(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String roleType) {
        startPage();
        List<DisputeCase> list = disputeCaseService.selectDisputeList(status, roleType);
        return getDataTable(list);
    }

    /**
     * 查询纠纷详情
     */
    @GetMapping("/{caseId}")
    public AjaxResult selectDetailById(@PathVariable Long caseId) {
        DisputeCase caseDetail = disputeCaseService.selectDetailById(caseId);
        return AjaxResult.success(caseDetail);
    }

    /**
     * 被申请人提交答辩证据
     */
    @PostMapping("/submitDefense")
    @Log(title = "提交答辩证据", businessType = BusinessType.UPDATE)
    public AjaxResult submitDefenseEvidence(
        @RequestParam Long caseId,
        @RequestParam String defendantDescription,
        @RequestParam(required = false) String defendantEvidenceImages) {
        return disputeCaseService.submitDefenseEvidence(caseId, defendantDescription, defendantEvidenceImages);
    }

    /**
     * 管理员处理纠纷
     */
    @PutMapping("/{caseId}/handle")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "处理纠纷", businessType = BusinessType.UPDATE)
    public AjaxResult handleDispute(
        @PathVariable Long caseId,
        @RequestParam String status,
        @RequestParam String handleReason) {
        return disputeCaseService.handleDispute(caseId, status, handleReason);
    }
}
