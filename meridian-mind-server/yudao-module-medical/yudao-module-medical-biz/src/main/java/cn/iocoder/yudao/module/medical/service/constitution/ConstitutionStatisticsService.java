package cn.iocoder.yudao.module.medical.service.constitution;

import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;

import java.util.List;

/**
 * 体质评估统计 Service 接口
 */
public interface ConstitutionStatisticsService {
    /**
     * 获取首页卡片统计数据
     *
     * @return 卡片统计数据
     */
    ConstitutionDashboardCardRespVO getDashboardCardData();

    /**
     * 获取体质分布统计
     *
     * @return 体质分布统计
     */
    ConstitutionDistributionRespVO getConstitutionDistribution();

    /**
     * 获取年龄段体质分布统计
     *
     * @return 年龄段体质分布统计
     */
    AgeConstitutionDistributionRespVO getAgeConstitutionDistribution();

    /**
     * 获取性别体质分布统计
     *
     * @return 性别体质分布统计
     */
    GenderConstitutionDistributionRespVO getGenderConstitutionDistribution();

    /**
     * 获取月度评估趋势统计
     *
     * @return 月度评估趋势统计
     */
    MonthlyAssessmentTrendRespVO getMonthlyAssessmentTrend();
    /**
     * 获取体质评估历史雷达图数据
     *
     * @param userId 用户ID
     * @param recordId 指定记录ID，如果为空则获取最新记录
     * @return 雷达图数据
     */
    ConstitutionRadarRespVO getRadarData(Long userId, Long recordId);
    /**
     * 获取用户历史体质评估记录列表
     *
     * @param userId 用户ID
     * @return 历史记录列表
     */
    List<ConstitutionRadarRespVO> getHistoryRadarData(Long userId);
}