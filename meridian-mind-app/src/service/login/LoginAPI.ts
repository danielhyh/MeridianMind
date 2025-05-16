import {http, httpGet, httpPost} from '@/utils/http'

/**
 * 账号密码登录
 */
export const login = (data) => {
  return httpPost('/member/auth/login', {
    mobile: data.username,
    password: data.password
  })
}

/**
 * 退出登录
 */
export const loginOut = () => {
  return httpPost('/member/auth/logout')
}

/**
 * 获取用户信息
 */
export const getInfo = (): Promise<UserInfoVO> => {
  return httpGet<UserInfoVO>('/system/auth/get-permission-info')
}

/**
 * 根据租户名获取租户ID
 */
export const getTenantIdByName = (name: string) => {
  return httpGet('/system/tenant/get-id-by-name', { name })
}

/**
 * 发送短信验证码
 * 精简参数，只传递需要的参数
 */
export function sendSmsCode(data: {
  mobile: string,
  scene: number
}) {
  return httpPost<boolean>('/member/auth/send-sms-code', data)
}

/**
 * 手机号+验证码登录
 * 精简参数，不需要传社交参数
 */
export function smsLogin(data: {
  mobile: string,
  code: string,
  socialType?: number,
  socialCode?: string,
  socialState?: string,
  socialCodeValid?: boolean
}) {
  // 仅传递必要参数
  return httpPost<AppAuthLoginRespVO>('/member/auth/sms-login', {
    mobile: data.mobile,
    code: data.code
  })
}

/**
 * 微信小程序一键登录
 */
export function weixinMiniAppLogin(data: {
  phoneCode: string,
  loginCode: string,
  state: string
}) {
  return httpPost<AppAuthLoginRespVO>('/member/auth/weixin-mini-app-login', data)
}

/**
 * 刷新令牌
 */
export function refreshToken(refreshToken: string) {
  return httpPost<AppAuthLoginRespVO>(`/member/auth/refresh-token?refreshToken=${refreshToken}`)
}
