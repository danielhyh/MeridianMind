package cn.iocoder.yudao.module.member.api.user.dto;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 Response DTO
 *
 * @author 芋道源码
 */
@Data
public class MemberUserRespDTO {

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 真实姓名
     */
    private String name;
    /**
     * 帐号状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 创建时间（注册时间）
     */
    private LocalDateTime createTime;
    /**
     * 出生日期
     */
    private LocalDateTime birthday;
    /**
     * 性别
     * <p>
     */
    private Integer sex;

    // ========== 其它信息 ==========

    /**
     * 会员级别编号
     */
    private Long levelId;

    /**
     * 积分
     */
    private Integer point;

}
