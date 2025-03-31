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
