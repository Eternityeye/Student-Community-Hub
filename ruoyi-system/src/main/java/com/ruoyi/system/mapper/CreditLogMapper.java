package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.CreditLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 信誉历史记录数据层
 */
public interface CreditLogMapper {

    int insert(CreditLog creditLog);

    List<CreditLog> selectByUserId(@Param("userId") Long userId);
}
