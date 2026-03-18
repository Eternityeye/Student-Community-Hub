package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.UserCredit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户信誉数据层
 *
 * @author student-hub
 * @date 2026-03-18
 */
public interface UserCreditMapper extends BaseMapper<UserCredit> {
    /**
     * 根据用户ID查询信誉信息
     *
     * @param userId 用户ID
     * @return 信誉信息
     */
    UserCredit selectByUserId(@Param("userId") Long userId);

    /**
     * 初始化用户信誉信息
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    int insertDefault(@Param("userId") Long userId);
}
