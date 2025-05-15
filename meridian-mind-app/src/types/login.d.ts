// 发送验证码请求参数
interface AppAuthSmsSendReqVO {
  mobile: string       // 手机号
  scene: number        // 场景 (1=登录)
}

// 手机号登录请求参数
interface AppAuthSmsLoginReqVO {
  mobile: string       // 手机号
  code: string         // 验证码
  socialType: number   // 社交平台类型
  socialCode: string   // 授权码
  socialState: string  // state
  socialCodeValid: boolean // 社交授权码是否有效
}

// 微信小程序登录请求参数
interface AppAuthWeixinMiniAppLoginReqVO {
  phoneCode: string    // 手机号码
  loginCode: string    // 登录code
  state: string        // state
}

// 登录响应结果
interface AppAuthLoginRespVO {
  userId: number       // 用户ID
  accessToken: string  // 访问令牌
  refreshToken: string // 刷新令牌
  expiresTime: string  // 过期时间
  openid?: string      // 社交用户openid
}
