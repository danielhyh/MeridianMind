package cn.iocoder.yudao.module.medical.service.constitution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.convert.constitution.ConstitutionQuestionnaireConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionRecordMapper;
import cn.iocoder.yudao.module.medical.enums.ConstitutionTypeEnum;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.RECORD_NOT_EXISTS;

/**
 * 体质评估统计 Service 实现类
 */
@Service
@Validated
@Slf4j
public class ConstitutionStatisticsServiceImpl implements ConstitutionStatisticsService {

    @Resource
    private ConstitutionRecordMapper recordMapper;

    @Resource
    private MemberUserApi memberUserApi;

    @Override
    public ConstitutionDashboardCardRespVO getDashboardCardData() {
        ConstitutionDashboardCardRespVO result = new ConstitutionDashboardCardRespVO();

        // 1. 计算今日评估数据
        result.setDailyAssessment(calculateDailyAssessment());

        // 2. 计算本周评估数据
        result.setWeeklyAssessment(calculateWeeklyAssessment());

        // 3. 计算体质分布数据
        result.setConstitutionDistribution(calculateConstitutionDistribution());

        // 4. 计算评估完成率
        result.setCompletionRate(calculateCompletionRate());

        return result;
    }

    /**
     * 计算今日评估数据
     */
    private ConstitutionDashboardCardRespVO.DailyAssessment calculateDailyAssessment() {
        ConstitutionDashboardCardRespVO.DailyAssessment result = new ConstitutionDashboardCardRespVO.DailyAssessment();

        // 获取今日开始和结束时间
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);

        // 获取昨日开始和结束时间
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime yesterdayStart = LocalDateTime.of(yesterday, LocalTime.MIN);
        LocalDateTime yesterdayEnd = LocalDateTime.of(yesterday, LocalTime.MAX);

        // 查询今日评估人数
        Integer todayCount = recordMapper.countByCreateTimeBetween(todayStart, todayEnd);
        result.setCount(todayCount);

        // 查询昨日评估人数
        Integer yesterdayCount = recordMapper.countByCreateTimeBetween(yesterdayStart, yesterdayEnd);

        // 计算增长率
        if (yesterdayCount != null && yesterdayCount > 0) {
            BigDecimal growthRate = BigDecimal.valueOf(todayCount - yesterdayCount)
                    .divide(BigDecimal.valueOf(yesterdayCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
            result.setGrowthRate(growthRate);
        } else {
            result.setGrowthRate(BigDecimal.ZERO);
        }

        return result;
    }

    /**
     * 计算本周评估数据
     */
    private ConstitutionDashboardCardRespVO.WeeklyAssessment calculateWeeklyAssessment() {
        ConstitutionDashboardCardRespVO.WeeklyAssessment result = new ConstitutionDashboardCardRespVO.WeeklyAssessment();

        // 获取本周的开始和结束时间（周一和周日）
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate currentWeekStart = today.with(weekFields.dayOfWeek(), 1);
        LocalDate currentWeekEnd = today.with(weekFields.dayOfWeek(), 7);

        LocalDateTime currentWeekStartTime = LocalDateTime.of(currentWeekStart, LocalTime.MIN);
        LocalDateTime currentWeekEndTime = LocalDateTime.of(currentWeekEnd, LocalTime.MAX);

        // 查询本周评估人数
        Integer currentWeekCount = recordMapper.countByCreateTimeBetween(currentWeekStartTime, currentWeekEndTime);
        result.setCount(currentWeekCount);

        // 获取上周的开始和结束时间
        LocalDate previousWeekStart = currentWeekStart.minusWeeks(1);
        LocalDate previousWeekEnd = currentWeekEnd.minusWeeks(1);

        LocalDateTime previousWeekStartTime = LocalDateTime.of(previousWeekStart, LocalTime.MIN);
        LocalDateTime previousWeekEndTime = LocalDateTime.of(previousWeekEnd, LocalTime.MAX);

        // 查询上周评估人数
        Integer previousWeekCount = recordMapper.countByCreateTimeBetween(previousWeekStartTime, previousWeekEndTime);

        // 计算增长率
        if (previousWeekCount != null && previousWeekCount > 0) {
            BigDecimal growthRate = BigDecimal.valueOf(currentWeekCount - previousWeekCount)
                    .divide(BigDecimal.valueOf(previousWeekCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
            result.setGrowthRate(growthRate);
        } else {
            result.setGrowthRate(BigDecimal.ZERO);
        }

        return result;
    }

    /**
     * 计算体质分布数据
     */
    private ConstitutionDashboardCardRespVO.ConstitutionDistribution calculateConstitutionDistribution() {
        ConstitutionDashboardCardRespVO.ConstitutionDistribution result = new ConstitutionDashboardCardRespVO.ConstitutionDistribution();

        // 查询所有评估记录
        List<ConstitutionRecordDO> records = recordMapper.selectList();

        // 统计各体质类型数量
        Map<String, Integer> typeCounts = new HashMap<>();
        for (ConstitutionRecordDO record : records) {
            // 主要体质 - 跳过空值
            String primaryType = record.getPrimaryType();
            if (primaryType != null && !primaryType.isEmpty()) {
                typeCounts.put(primaryType, typeCounts.getOrDefault(primaryType, 0) + 1);
            }
        }

        // 计算总数 - 使用有效记录数量
        int total = typeCounts.values().stream().mapToInt(Integer::intValue).sum();

        // 计算各体质的百分比并排序
        List<ConstitutionDashboardCardRespVO.ConstitutionDistribution.ConstitutionTypeData> typeDataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            ConstitutionDashboardCardRespVO.ConstitutionDistribution.ConstitutionTypeData typeData =
                    new ConstitutionDashboardCardRespVO.ConstitutionDistribution.ConstitutionTypeData();
            typeData.setType(entry.getKey());
            typeData.setName(ConstitutionTypeEnum.nameOf(entry.getKey()));

            // 计算百分比
            if (total > 0) {
                BigDecimal percentage = BigDecimal.valueOf(entry.getValue())
                        .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(1, RoundingMode.HALF_UP);
                typeData.setPercentage(percentage);
            } else {
                typeData.setPercentage(BigDecimal.ZERO);
            }

            typeDataList.add(typeData);
        }

        // 按百分比降序排序并取前3
        typeDataList.sort((a, b) -> b.getPercentage().compareTo(a.getPercentage()));
        List<ConstitutionDashboardCardRespVO.ConstitutionDistribution.ConstitutionTypeData> topTypes =
                typeDataList.stream().limit(3).collect(Collectors.toList());

        result.setTopTypes(topTypes);
        return result;
    }

    /**
     * 计算评估完成率
     */
    private ConstitutionDashboardCardRespVO.CompletionRate calculateCompletionRate() {
        ConstitutionDashboardCardRespVO.CompletionRate result = new ConstitutionDashboardCardRespVO.CompletionRate();

        // 获取当前月的开始和结束时间
        LocalDate today = LocalDate.now();
        LocalDate currentMonthStart = today.withDayOfMonth(1);
        LocalDate currentMonthEnd = today.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime currentMonthStartTime = LocalDateTime.of(currentMonthStart, LocalTime.MIN);
        LocalDateTime currentMonthEndTime = LocalDateTime.of(currentMonthEnd, LocalTime.MAX);

        // 查询当月已完成的评估记录数
        Integer completedCount = recordMapper.countCompletedAssessmentsByCreateTimeBetween(currentMonthStartTime, currentMonthEndTime);

        // 查询当月总评估记录数（包括未完成的）
        Integer totalCount = recordMapper.countTotalAssessmentsByCreateTimeBetween(currentMonthStartTime, currentMonthEndTime);

        // 计算完成率
        if (totalCount != null && totalCount > 0) {
            BigDecimal completionRate = BigDecimal.valueOf(completedCount)
                    .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
            result.setRate(completionRate);
        } else {
            result.setRate(BigDecimal.ZERO);
        }

        // 获取上月的开始和结束时间
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDate previousMonthEnd = previousMonthStart.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime previousMonthStartTime = LocalDateTime.of(previousMonthStart, LocalTime.MIN);
        LocalDateTime previousMonthEndTime = LocalDateTime.of(previousMonthEnd, LocalTime.MAX);

        // 查询上月已完成的评估记录数
        Integer previousCompletedCount = recordMapper.countCompletedAssessmentsByCreateTimeBetween(previousMonthStartTime, previousMonthEndTime);

        // 查询上月总评估记录数
        Integer previousTotalCount = recordMapper.countTotalAssessmentsByCreateTimeBetween(previousMonthStartTime, previousMonthEndTime);

        // 计算上月完成率
        BigDecimal previousCompletionRate = BigDecimal.ZERO;
        if (previousTotalCount != null && previousTotalCount > 0) {
            previousCompletionRate = BigDecimal.valueOf(previousCompletedCount)
                    .divide(BigDecimal.valueOf(previousTotalCount), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // 计算增长率
        if (previousCompletionRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal growthRate = result.getRate().subtract(previousCompletionRate)
                    .setScale(1, RoundingMode.HALF_UP);
            result.setGrowthRate(growthRate);
        } else {
            result.setGrowthRate(BigDecimal.ZERO);
        }

        return result;
    }

    /**
     * 获取体质评估历史雷达图数据
     *
     * @param userId   用户ID
     * @param recordId 指定记录ID，如果为空则获取最新记录
     * @return 雷达图数据
     */
    @Override
    public ConstitutionRadarRespVO getRadarData(Long userId, Long recordId) {
        ConstitutionRecordDO record;
        if (recordId != null) {
            // 获取指定记录
            record = recordMapper.selectById(recordId);
            if (record == null || !record.getUserId().equals(userId)) {
                throw exception(RECORD_NOT_EXISTS);
            }
        } else {
            // 获取最新记录
            record = recordMapper.selectLatestByUserId(userId);
            if (record == null) {
                throw exception(RECORD_NOT_EXISTS);
            }
        }

        return ConstitutionQuestionnaireConvert.INSTANCE.convertToRadar(record);
    }

    /**
     * 获取用户历史体质评估记录列表
     *
     * @param userId 用户ID
     * @return 历史记录列表
     */
    @Override
    public List<ConstitutionRadarRespVO> getHistoryRadarData(Long userId) {
        // 获取用户的历史记录
        List<ConstitutionRecordDO> records = recordMapper.selectListByUserId(userId);
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }

        // 转换为雷达图数据
        return records.stream()
                .map(ConstitutionQuestionnaireConvert.INSTANCE::convertToRadar)
                .collect(Collectors.toList());
    }

    @Override
    public ConstitutionDistributionRespVO getConstitutionDistribution() {
        // 查询所有评估记录
        List<ConstitutionRecordDO> records = recordMapper.selectList();

        // 统计各体质类型数量
        Map<String, Integer> typeCounts = new HashMap<>();
        for (ConstitutionRecordDO record : records) {
            // 主要体质
            String primaryType = record.getPrimaryType();
            typeCounts.put(primaryType, typeCounts.getOrDefault(primaryType, 0) + 1);

            // 次要体质（权重较低）
            if (CollUtil.isNotEmpty(record.getSecondaryTypes())) {
                for (String secondaryType : record.getSecondaryTypes()) {
                    typeCounts.put(secondaryType, typeCounts.getOrDefault(secondaryType, 0) + 1);
                }
            }
        }

        // 计算总数
        int total = 0;
        for (Integer count : typeCounts.values()) {
            total += count;
        }

        // 组装返回结果
        ConstitutionDistributionRespVO respVO = new ConstitutionDistributionRespVO();
        respVO.setStatisticTime(LocalDateTime.now());

        List<ConstitutionDistributionRespVO.ConstitutionData> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            ConstitutionDistributionRespVO.ConstitutionData item = new ConstitutionDistributionRespVO.ConstitutionData();
            item.setType(entry.getKey());
            item.setName(ConstitutionTypeEnum.nameOf(entry.getKey()));
            item.setCount(entry.getValue());

            // 计算占比
            if (total > 0) {
                BigDecimal percentage = BigDecimal.valueOf(entry.getValue())
                        .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
                item.setPercentage(percentage);
            } else {
                item.setPercentage(BigDecimal.ZERO);
            }

            data.add(item);
        }

        // 按数量降序排序
        data.sort((a, b) -> b.getCount().compareTo(a.getCount()));

        respVO.setData(data);
        return respVO;
    }

    @Override
    public AgeConstitutionDistributionRespVO getAgeConstitutionDistribution() {
        // 查询所有评估记录
        List<ConstitutionRecordDO> records = recordMapper.selectList();
        if (CollUtil.isEmpty(records)) {
            return new AgeConstitutionDistributionRespVO();
        }

        // 获取用户信息
        Set<Long> userIds = records.stream().map(ConstitutionRecordDO::getUserId).collect(Collectors.toSet());
        Map<Long, MemberUserRespDTO> userMap = memberUserApi.getUserMap(userIds);

        // 定义年龄段
        Map<String, Integer[]> ageRanges = new LinkedHashMap<>();
        ageRanges.put("0-18", new Integer[]{0, 18});
        ageRanges.put("19-30", new Integer[]{19, 30});
        ageRanges.put("31-45", new Integer[]{31, 45});
        ageRanges.put("46-60", new Integer[]{46, 60});
        ageRanges.put("60+", new Integer[]{61, 150});

        // 统计各年龄段各体质类型数量
        Map<String, Map<String, Integer>> ageTypeCountMap = new HashMap<>();
        for (ConstitutionRecordDO record : records) {
            MemberUserRespDTO user = userMap.get(record.getUserId());
            if (user == null || user.getBirthday() == null) {
                continue;
            }

            // 计算年龄
            int age = DateUtil.ageOfNow(DateUtil.date(user.getBirthday()));

            // 确定年龄段
            String ageRange = null;
            for (Map.Entry<String, Integer[]> entry : ageRanges.entrySet()) {
                Integer[] range = entry.getValue();
                if (age >= range[0] && age <= range[1]) {
                    ageRange = entry.getKey();
                    break;
                }
            }

            if (ageRange == null) {
                continue;
            }

            // 初始化年龄段体质类型计数map
            Map<String, Integer> typeCountMap = ageTypeCountMap.computeIfAbsent(ageRange, k -> new HashMap<>());

            // 统计主要体质
            String primaryType = record.getPrimaryType();
            typeCountMap.put(primaryType, typeCountMap.getOrDefault(primaryType, 0) + 1);
        }

        // 组装返回结果
        AgeConstitutionDistributionRespVO respVO = new AgeConstitutionDistributionRespVO();
        respVO.setStatisticTime(LocalDateTime.now());

        List<AgeConstitutionDistributionRespVO.AgeRangeData> data = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : ageTypeCountMap.entrySet()) {
            AgeConstitutionDistributionRespVO.AgeRangeData ageData = new AgeConstitutionDistributionRespVO.AgeRangeData();
            ageData.setAgeRange(entry.getKey());

            List<AgeConstitutionDistributionRespVO.ConstitutionData> typeData = new ArrayList<>();
            for (Map.Entry<String, Integer> typeEntry : entry.getValue().entrySet()) {
                AgeConstitutionDistributionRespVO.ConstitutionData item = new AgeConstitutionDistributionRespVO.ConstitutionData();
                item.setType(typeEntry.getKey());
                item.setName(ConstitutionTypeEnum.nameOf(typeEntry.getKey()));
                item.setCount(typeEntry.getValue());
                typeData.add(item);
            }

            // 按数量降序排序
            typeData.sort((a, b) -> b.getCount().compareTo(a.getCount()));
            ageData.setData(typeData);

            data.add(ageData);
        }

        respVO.setData(data);
        return respVO;
    }

    @Override
    public GenderConstitutionDistributionRespVO getGenderConstitutionDistribution() {
        // 查询所有评估记录
        List<ConstitutionRecordDO> records = recordMapper.selectList();
        if (CollUtil.isEmpty(records)) {
            return new GenderConstitutionDistributionRespVO();
        }

        // 获取用户信息
        Set<Long> userIds = records.stream().map(ConstitutionRecordDO::getUserId).collect(Collectors.toSet());
        Map<Long, MemberUserRespDTO> userMap = memberUserApi.getUserMap(userIds);

        // 统计各性别各体质类型数量
        Map<Integer, Map<String, Integer>> genderTypeCountMap = new HashMap<>();
        for (ConstitutionRecordDO record : records) {
            MemberUserRespDTO user = userMap.get(record.getUserId());
            if (user == null || user.getSex() == null) {
                continue;
            }

            Integer gender = user.getSex();

            // 初始化性别体质类型计数map
            Map<String, Integer> typeCountMap = genderTypeCountMap.computeIfAbsent(gender, k -> new HashMap<>());

            // 统计主要体质
            String primaryType = record.getPrimaryType();
            typeCountMap.put(primaryType, typeCountMap.getOrDefault(primaryType, 0) + 1);
        }

        // 组装返回结果
        GenderConstitutionDistributionRespVO respVO = new GenderConstitutionDistributionRespVO();
        respVO.setStatisticTime(LocalDateTime.now());

        // 男性数据
        GenderConstitutionDistributionRespVO.GenderData maleData = new GenderConstitutionDistributionRespVO.GenderData();
        maleData.setGender(1);
        maleData.setGenderName("男");

        Map<String, Integer> maleTypeCountMap = genderTypeCountMap.getOrDefault(1, new HashMap<>());
        List<GenderConstitutionDistributionRespVO.ConstitutionData> maleTypeData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : maleTypeCountMap.entrySet()) {
            GenderConstitutionDistributionRespVO.ConstitutionData item = new GenderConstitutionDistributionRespVO.ConstitutionData();
            item.setType(entry.getKey());
            item.setName(ConstitutionTypeEnum.nameOf(entry.getKey()));
            item.setCount(entry.getValue());
            maleTypeData.add(item);
        }
        maleTypeData.sort((a, b) -> b.getCount().compareTo(a.getCount()));
        maleData.setData(maleTypeData);

        // 女性数据
        GenderConstitutionDistributionRespVO.GenderData femaleData = new GenderConstitutionDistributionRespVO.GenderData();
        femaleData.setGender(2);
        femaleData.setGenderName("女");

        Map<String, Integer> femaleTypeCountMap = genderTypeCountMap.getOrDefault(2, new HashMap<>());
        List<GenderConstitutionDistributionRespVO.ConstitutionData> femaleTypeData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : femaleTypeCountMap.entrySet()) {
            GenderConstitutionDistributionRespVO.ConstitutionData item = new GenderConstitutionDistributionRespVO.ConstitutionData();
            item.setType(entry.getKey());
            item.setName(ConstitutionTypeEnum.nameOf(entry.getKey()));
            item.setCount(entry.getValue());
            femaleTypeData.add(item);
        }
        femaleTypeData.sort((a, b) -> b.getCount().compareTo(a.getCount()));
        femaleData.setData(femaleTypeData);

        respVO.setData(Arrays.asList(maleData, femaleData));
        return respVO;
    }

    @Override
    public MonthlyAssessmentTrendRespVO getMonthlyAssessmentTrend() {
        // 获取过去12个月的评估记录
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMonths(12);
        List<ConstitutionRecordDO> records = recordMapper.selectListByCreateTimeBetween(startTime, endTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        // 按月份分组
        Map<String, List<ConstitutionRecordDO>> monthlyRecords = new HashMap<>();
        for (ConstitutionRecordDO record : records) {
            String month = record.getCreateTime().format(formatter);
            monthlyRecords.computeIfAbsent(month, k -> new ArrayList<>()).add(record);
        }

        // 生成所有月份
        List<String> allMonths = new ArrayList<>();
        LocalDateTime current = startTime;
        while (!current.isAfter(endTime)) {
            allMonths.add(current.format(formatter));
            current = current.plusMonths(1);
        }

        // 组装返回结果
        MonthlyAssessmentTrendRespVO respVO = new MonthlyAssessmentTrendRespVO();
        respVO.setStatisticTime(LocalDateTime.now());

        List<MonthlyAssessmentTrendRespVO.MonthData> data = new ArrayList<>();
        for (String month : allMonths) {
            MonthlyAssessmentTrendRespVO.MonthData monthData = new MonthlyAssessmentTrendRespVO.MonthData();
            monthData.setMonth(month);

            List<ConstitutionRecordDO> monthRecords = monthlyRecords.getOrDefault(month, new ArrayList<>());
            monthData.setCount(monthRecords.size());

            data.add(monthData);
        }

        respVO.setData(data);
        return respVO;
    }
}