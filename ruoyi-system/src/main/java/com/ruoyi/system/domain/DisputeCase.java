package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 纠纷案件表 dispute_case
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DisputeCase extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 纠纷案件ID */
    @Excel(name = "纠纷案件ID")
    private Long caseId;

    /** 关联任务ID */
    @Excel(name = "关联任务ID")
    private Long taskId;

    /** 任务标题 (用于展示) */
    @Excel(name = "任务标题")
    private String taskTitle;

    /** 申请人(发起方)用户ID */
    @Excel(name = "申请人用户ID")
    private Long plaintiffId;

    /** 申请人用户名 (用于展示) */
    private String plaintiffName;

    /** 申请人头像 (用于展示) */
    private String plaintiffAvatar;

    /** 被申请人(被告方)用户ID */
    @Excel(name = "被申请人用户ID")
    private Long defendantId;

    /** 被申请人用户名 (用于展示) */
    private String defendantName;

    /** 被申请人头像 (用于展示) */
    private String defendantAvatar;

    /** 纠纷原因 */
    @Excel(name = "纠纷原因")
    private String reason;

    /** 申请人描述 */
    private String plaintiffDescription;

    /** 被申请人描述 */
    private String defendantDescription;

    /** 申请人证据图片，多个用逗号分隔 */
    private String plaintiffEvidenceImages;

    /** 被申请人证据图片，多个用逗号分隔 */
    private String defendantEvidenceImages;

    /** 纠纷状态（0待审理 1申请人责任 2被申请人责任 3驳回） */
    @Excel(name = "纠纷状态", readConverterExp = "0=待审理,1=申请人责任,2=被申请人责任,3=驳回")
    private String status;

    /** 处理人(管理员)ID */
    private Long handlerId;

    /** 处理人(管理员)名称 (用于展示) */
    private String handlerName;

    /** 处理理由 */
    private String handleReason;

    /** 处理时间 */
    private Date handleTime;

    // 仅用于展示的扩展字段
    private String roleType; // 当前用户的角色 (plaintiff 或 defendant)
}
