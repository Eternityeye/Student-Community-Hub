package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SocialReport;

/**
 * 社交举报 数据层
 *
 * @author ruoyi
 */
public interface SocialReportMapper
{
    /**
     * 查询举报列表
     *
     * @param report 举报信息
     * @return 举报列表
     */
    public List<SocialReport> selectReportList(SocialReport report);

    /**
     * 查询举报详情
     *
     * @param reportId 举报ID
     * @return 举报详情
     */
    public SocialReport selectReportById(@Param("reportId") Long reportId);

    /**
     * 新增举报
     *
     * @param report 举报信息
     * @return 结果
     */
    public int insertReport(SocialReport report);

    /**
     * 修改举报
     *
     * @param report 举报信息
     * @return 结果
     */
    public int updateReport(SocialReport report);
}
