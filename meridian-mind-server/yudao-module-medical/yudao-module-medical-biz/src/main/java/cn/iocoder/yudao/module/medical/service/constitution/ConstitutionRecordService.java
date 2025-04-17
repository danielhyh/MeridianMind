package cn.iocoder.yudao.module.medical.service.constitution;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordDetailRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordRespVO;

/**
 * 体质评估记录 Service 接口
 */
public interface ConstitutionRecordService {

    /**
     * 获取体质评估记录分页
     *
     * @param pageReqVO 分页条件
     * @return 记录分页
     */
    PageResult<ConstitutionRecordRespVO> getRecordPage(ConstitutionRecordPageReqVO pageReqVO);

    /**
     * 获取体质评估记录详情
     *
     * @param id 记录ID
     * @return 记录详情
     */
    ConstitutionRecordDetailRespVO getRecordDetail(Long id);

    /**
     * 获取用户最新的体质评估记录
     *
     * @param userId 用户ID
     * @return 评估记录
     */
    ConstitutionRecordRespVO getUserLatestRecord(Long userId);
}