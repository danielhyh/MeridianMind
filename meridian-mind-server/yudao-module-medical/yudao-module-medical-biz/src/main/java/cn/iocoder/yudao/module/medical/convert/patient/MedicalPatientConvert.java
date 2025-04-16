package cn.iocoder.yudao.module.medical.convert.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.MedicalPatientRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfileRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppMedicalPatientRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MedicalPatientConvert {

    MedicalPatientConvert INSTANCE = Mappers.getMapper(MedicalPatientConvert.class);

    /**
     * MemberUserDO,PatientProfileDO 转换为 PatientRespVO
     */
    @Mappings({
            @Mapping(source = "profile", target = "profile"),
            @Mapping(source = "bean.id", target = "id"),
            @Mapping(source = "bean.createTime", target = "createTime")
    })
    AppMedicalPatientRespVO convert(MemberUserDO bean, PatientProfileDO profile);
    /**
     * MemberUserDO,PatientProfileDO 转换为 PatientRespVO
     */
    @Mappings({
            @Mapping(source = "profile", target = "profile"),
            @Mapping(source = "bean.id", target = "id"),
            @Mapping(source = "bean.createTime", target = "createTime")
    })
    MedicalPatientRespVO convert03(MemberUserDO bean, PatientProfileDO profile);
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
}