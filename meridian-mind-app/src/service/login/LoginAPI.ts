import {http, httpGet, httpPost} from '@/utils/http'

export const login = (data) => {
  return http({
    url: '/system/auth/login',
    method: 'POST',
    data,
  })
}

export const loginOut = () => {
  return httpPost('/system/auth/logout')
}

export const getInfo = (): Promise<UserInfoVO> => {
  return httpGet<UserInfoVO>('/system/auth/get-permission-info')
}

export const getTenantIdByName = (name: string) => {
  return httpGet('/system/tenant/get-id-by-name', { name })
}
/**
 * 发送手机验证码
 * @param data 手机号和场景
 */
export function sendSmsCode(data: AppAuthSmsSendReqVO) {
  return httpPost<boolean>('/member/auth/send-sms-code', data)
}

/**
 * 手机号+验证码登录
 * @param data 登录信息
 */
export function smsLogin(data: AppAuthSmsLoginReqVO) {
  return httpPost<AppAuthLoginRespVO>('/member/auth/sms-login', data)
}

/**
 * 微信小程序一键登录
 * @param data 登录信息
 */
export function weixinMiniAppLogin(data: AppAuthWeixinMiniAppLoginReqVO) {
  return httpPost<AppAuthLoginRespVO>('/member/auth/weixin-mini-app-login', data)
}
