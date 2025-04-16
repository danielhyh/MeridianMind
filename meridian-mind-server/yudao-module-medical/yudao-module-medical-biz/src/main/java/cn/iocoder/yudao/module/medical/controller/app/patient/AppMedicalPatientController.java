package cn.iocoder.yudao.module.medical.controller.app.patient;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppMedicalPatientRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.convert.patient.MedicalPatientConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.medical.service.patient.MermaidPatientService;
import cn.iocoder.yudao.module.member.controller.app.user.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 患者中心")
@RestController
@RequestMapping("/medical/patient")
@Validated
public class AppMedicalPatientController {

    @Resource
    private MermaidPatientService patientService;
    @Resource
    private MemberUserService userService;

    @GetMapping("/get")
    @Operation(summary = "获得患者信息")
    public CommonResult<AppMedicalPatientRespVO> getPatient() {
        MemberUserDO user = userService.getUser(getLoginUserId());
        PatientProfileDO patientProfile = patientService.getPatientProfile(user.getId());
        return success(MedicalPatientConvert.INSTANCE.convert(user, patientProfile));
    }

    @PutMapping("/update")
    @Operation(summary = "更新患者基本信息")
    public CommonResult<Boolean> updatePatient(@RequestBody @Valid AppMemberUserUpdateReqVO reqVO) {
        userService.updateUser(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/update-mobile")
    @Operation(summary = "修改用户手机")
    public CommonResult<Boolean> updateUserMobile(@RequestBody @Valid AppMemberUserUpdateMobileReqVO reqVO) {
        userService.updateUserMobile(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/update-mobile-by-weixin")
    @Operation(summary = "基于微信小程序的授权码，修改用户手机")
    public CommonResult<Boolean> updateUserMobileByWeixin(@RequestBody @Valid AppMemberUserUpdateMobileByWeixinReqVO reqVO) {
        userService.updateUserMobileByWeixin(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户密码", description = "用户修改密码时使用")
    public CommonResult<Boolean> updateUserPassword(@RequestBody @Valid AppMemberUserUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/reset-password")
    @Operation(summary = "重置密码", description = "用户忘记密码时使用")
    @PermitAll
    public CommonResult<Boolean> resetUserPassword(@RequestBody @Valid AppMemberUserResetPasswordReqVO reqVO) {
        userService.resetUserPassword(reqVO);
        return success(true);
    }

    @PutMapping("/profile/update")
    @Operation(summary = "更新患者档案信息")
    public CommonResult<Boolean> updatePatientProfile(@Valid @RequestBody AppPatientProfileSaveReqVO updateReqVO) {
        patientService.updatePatientProfile(updateReqVO);
        return success(true);
    }
}