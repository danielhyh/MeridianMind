package cn.iocoder.yudao.module.medical.convert.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfileRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PatientProfileConvert {

    PatientProfileConvert INSTANCE = Mappers.getMapper(PatientProfileConvert.class);
    /**
     * MemberUserDO 转换为 PatientRespVO
     */

    /**
     * PatientProfileDO 列表转换为 PatientProfileRespVO 列表
     */
    List<PatientProfileRespVO> convertList(List<PatientProfileDO> list);

    /**
     * PatientProfileSaveReqVO 转换为 PatientProfileDO
     */
    PatientProfileDO convert(AppPatientProfileSaveReqVO bean);

    /**
     * 分页结果转换
     */
    PageResult<PatientProfileRespVO> convertPage(PageResult<PatientProfileDO> page);

    /**
     * 将DO转换为响应VO，不包括JSON字段的转换
     */
    @Mappings({
            @Mapping(target = "allergies", ignore = true),
            @Mapping(target = "medicalHistory", ignore = true),
            @Mapping(target = "familyMedicalHistory", ignore = true),
            @Mapping(source = "user.createTime", target = "createTime")
    })
    AppPatientProfileRespVO convert(PatientProfileDO bean, MemberUserRespDTO user);
}