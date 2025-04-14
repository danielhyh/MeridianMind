package cn.iocoder.yudao.module.medical.controller.app.patient;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfileRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.medical.service.patient.PatientService;
import cn.iocoder.yudao.module.member.controller.admin.user.vo.MemberUserPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.user.vo.MemberUserRespVO;
import cn.iocoder.yudao.module.member.controller.admin.user.vo.MemberUserUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.user.vo.*;
import cn.iocoder.yudao.module.member.convert.user.MemberUserConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.level.MemberLevelDO;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.service.group.MemberGroupService;
import cn.iocoder.yudao.module.member.service.tag.MemberTagService;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 患者管理")
@RestController
@RequestMapping("/medical/patient")
@Validated
public class AppPatientController {

    @Resource
    private PatientService patientProfileService;
    @Resource
    private MemberUserService userService;
    @Resource
    private MemberTagService memberTagService;
    @Resource
    private MemberGroupService memberGroupService;

    @GetMapping("/get")
    @Operation(summary = "获得患者基本信息")
    public CommonResult<AppMemberUserInfoRespVO> getPatient() {
        MemberUserDO user = userService.getUser(getLoginUserId());
        return success(MemberUserConvert.INSTANCE.convert(user));
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
    @GetMapping("/profile/get")
    @Operation(summary = "获得患者档案信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<PatientProfileRespVO> getPatientProfile(@RequestParam("id") Long id) {
        PatientProfileDO profile = patientProfileService.getPatientProfile(id);
        return success(BeanUtils.toBean(profile, PatientProfileRespVO.class));
    }

    @GetMapping("/profile/create")
    @Operation(summary = "提交健康档案信息")
    public CommonResult<Long> createPatientProfile(@Valid @RequestBody PatientProfileSaveReqVO createReqVO) {
        return success(patientProfileService.createPatientProfile(createReqVO));
    }

    @PutMapping("/profile/update")
    @Operation(summary = "更新患者档案信息")
    public CommonResult<Boolean> updatePatientProfile(@Valid @RequestBody PatientProfileSaveReqVO updateReqVO) {
        patientProfileService.updatePatientProfile(updateReqVO);
        return success(true);
    }
}