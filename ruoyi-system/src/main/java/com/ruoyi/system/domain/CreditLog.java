package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 信誉历史记录表 credit_log
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Data
public class CreditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @Excel(name = "日志ID")
    private Long logId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 变动类型（dispute_loss纠纷扣分、appeal_resolve申诉解禁等） */
    @Excel(name = "变动类型")
    private String changeType;

    /** 变动分数（扣分为负数） */
    @Excel(name = "变动分数")
    private Integer changeScore;

    /** 关联ID(case_id或task_id) */
    @Excel(name = "关联ID")
    private Long relatedId;

    /** 变动原因 */
    @Excel(name = "变动原因")
    private String reason;

    /** 创建时间 */
    private Date createTime;
}
