package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信誉表 user_credit
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Data
public class UserCredit implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 信誉记录ID */
    @Excel(name = "信誉记录ID")
    private Long creditId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 信誉分（0-100） */
    @Excel(name = "信誉分")
    private Integer creditScore;

    /** 纠纷次数 */
    @Excel(name = "纠纷次数")
    private Integer disputeCount;

    /** 禁止接单至(NULL表示未禁，过期时间则自动解禁) */
    @Excel(name = "禁止接单至")
    private Date banUntil;

    /** 禁止原因 */
    @Excel(name = "禁止原因")
    private String banReason;

    /** 最后更新时间 */
    private Date lastUpdate;

    /**
     * 获取是否被禁用
     * @return true表示当前被禁用
     */
    public boolean isBanned() {
        return banUntil != null && new Date().before(banUntil);
    }
}
