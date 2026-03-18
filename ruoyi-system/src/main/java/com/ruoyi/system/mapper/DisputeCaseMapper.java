package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.DisputeCase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 纠纷案件数据层
 *
 * @author student-hub
 * @date 2026-03-18
 */
public interface DisputeCaseMapper extends BaseMapper<DisputeCase> {
    /**
     * 查询纠纷列表
     *
     * @param userId 当前用户ID
     * @param status 纠纷状态
     * @param roleType 角色类型（plaintiff/defendant）
     * @return 纠纷列表
     */
    List<DisputeCase> selectDisputeList(@Param("userId") Long userId,
                                        @Param("status") String status,
                                        @Param("roleType") String roleType);

    /**
     * 查询某个任务的纠纷是否存在
     *
     * @param taskId 任务ID
     * @return 纠纷列表
     */
    List<DisputeCase> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * 获取纠纷详情（包含用户信息）
     *
     * @param caseId 纠纷ID
     * @return 纠纷信息
     */
    DisputeCase selectDetailById(@Param("caseId") Long caseId);
}
