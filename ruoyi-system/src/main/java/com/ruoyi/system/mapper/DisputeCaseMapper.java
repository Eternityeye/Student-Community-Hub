package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.DisputeCase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 纠纷案件数据层
 */
public interface DisputeCaseMapper {

    int insert(DisputeCase disputeCase);

    int updateById(DisputeCase disputeCase);

    DisputeCase selectById(@Param("caseId") Long caseId);

    List<DisputeCase> selectDisputeList(@Param("userId") Long userId,
                                        @Param("status") String status,
                                        @Param("roleType") String roleType);

    List<DisputeCase> selectByTaskId(@Param("taskId") Long taskId);

    DisputeCase selectDetailById(@Param("caseId") Long caseId);

    List<DisputeCase> selectAllDisputeList(@Param("status") String status);
}
