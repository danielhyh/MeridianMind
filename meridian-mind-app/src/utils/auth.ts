const AccessTokenKey = 'ACCESS_TOKEN'
const RefreshTokenKey = 'REFRESH_TOKEN'
const TenantIdKey = 'tenantId'

// ========== Token 相关 ==========

// 获取 Token
export function getAccessToken() {
  return uni.getStorageSync(AccessTokenKey)
}

// 获取 RefreshToken
export function getRefreshToken() {
  return uni.getStorageSync(RefreshTokenKey)
}

// 设置 Token
export function setToken(token) {
  uni.setStorageSync(AccessTokenKey, token.accessToken)
  uni.setStorageSync(RefreshTokenKey, token.refreshToken)
}

// 移除 Token
export function removeToken() {
  uni.removeStorageSync(AccessTokenKey)
  uni.removeStorageSync(RefreshTokenKey)
}

// ========== 租户相关 ==========

export const getTenantId = () => {
  return uni.getStorageSync(TenantIdKey)
}

export const setTenantId = (username: string) => {
  uni.setStorageSync(TenantIdKey, username)
}
