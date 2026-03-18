package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.CreditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 信誉历史记录数据层
 *
 * @author student-hub
 * @date 2026-03-18
 */
public interface CreditLogMapper extends BaseMapper<CreditLog> {
    /**
     * 查询用户信誉历史（最近10条）
     *
     * @param userId 用户ID
     * @return 历史记录列表
     */
    List<CreditLog> selectByUserId(@Param("userId") Long userId);
}
