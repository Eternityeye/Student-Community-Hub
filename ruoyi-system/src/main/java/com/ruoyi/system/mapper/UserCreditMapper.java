package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.UserCredit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信誉数据层
 */
public interface UserCreditMapper {

    int insert(UserCredit userCredit);

    int updateById(UserCredit userCredit);

    UserCredit selectByUserId(@Param("userId") Long userId);

    int insertDefault(@Param("userId") Long userId);

    /** 查询所有禁止期已到期的用户 */
    List<UserCredit> selectExpiredBans();
}
