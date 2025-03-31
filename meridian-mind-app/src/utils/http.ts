import {CustomRequestOptions} from '@/interceptors/request'
import {useUserStore} from '@/store'
import {getAccessToken, getRefreshToken, setToken} from '@/utils/auth'
import {showConfirm, toast} from '@/utils/common'
import {getEvnBaseUrl} from '@/utils'
import errorCode from './errorCode'

/*
  双 token 刷新说明：
  1. token 和 tenantId 都是在拦截器中赋值
  2. 后端并不直接更改 http statusCode，而是返回一个重新包装后的对象，所以不能使用失败回调函数就行判断token是否过期需要重新获取
  3. 应该在 success 中，解析 res，如果 code 是 401，则说明 access token 过期，需要重新获取
*/

// 是否正在刷新中
let isRefreshToken = false
// 请求的方法栈
let requestList = []
// 请求基准地址
const baseUrl = getEvnBaseUrl()

export const http = <T>(options: CustomRequestOptions) => {
  // 1. 返回 Promise 对象
  return new Promise<T>((resolve, reject) => {
    uni.request({
      ...options,
      dataType: 'json',
      // #ifndef MP-WEIXIN
      responseType: 'json',
      // #endif
      // 响应成功
      async success(res) {
        const resp = res.data as IResData<T>
        const { data, code, msg } = resp

        if (code === 401) {
          // 未授权
          const userStore = useUserStore()

          if (!isRefreshToken) {
            // 设置为正在刷新
            isRefreshToken = true

            // 1. 如果获取不到刷新令牌，则只能执行登出操作
            if (!getRefreshToken()) {
              showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(
                (res: any) => {
                  if (res.confirm) {
                    // 清除缓存起来的用户信息
                    userStore.LogOut().then((res) => {
                      uni.reLaunch({ url: '/pages/login/index' })
                    })
                  }
                },
              )
            }

            // 2. 刷新accesstoken
            try {
              const refreshTokenRes: any = await refreshToken()
              if (refreshTokenRes.data.code !== 0) {
                // 如果获取不到refresh token，就直接跳转到登录页面
                showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(
                  (res: any) => {
                    if (res.confirm) {
                      // 清除缓存起来的用户信息
                      userStore.LogOut().then((res) => {
                        uni.reLaunch({ url: '/pages/login/index' })
                      })
                    }
                  },
                )
                const rejMsg = '无效的会话，或者会话已过期，请重新登录。'
                reject(rejMsg)
              }

              // 2.1 刷新成功，则回放队列的请求 + 当前请求
              setToken(refreshTokenRes.data.data)
              options.header.Authorization = 'Bearer ' + getAccessToken()
              // 将所有的请求方法都进行调用
              requestList.forEach((cb) => {
                cb()
              })
              // 调用之后就清空
              requestList = []

              // 到达这里，属于是请求已经失败了，需要重新请求的
              // 所以这里是一个递归调用
              resolve(http(options))
            } catch (e) {
              // 为什么需要 catch 异常呢？刷新失败时，请求因为 Promise.reject 触发异常。
              // 2.2 刷新失败，只回放队列的请求
              requestList.forEach((cb) => {
                cb()
              })
              const rejMsg = '无效的会话，或者会话已过期，请重新登录。'
              reject(rejMsg)
            } finally {
              requestList = []
              isRefreshToken = false
            }
          } else {
            // 添加到队列，等待刷新获取到新的令牌
            return new Promise((resolve) => {
              requestList.push(() => {
                options.header.Authorization = 'Bearer ' + getAccessToken()
                resolve(http(options))
              })
            })
          }
        } else if (code === 500) {
          // 服务器错误
          toast(msg)
          reject(errorCode['500'])
        } else if (code !== 0) {
          // 其他的错误
          // ps：yudao 的 success 的 code，默认是 0 而不是 200
          toast(msg)
          reject(code)
        }

        resolve(data)
      },
      // 响应失败
      fail(err) {
        uni.showToast({
          icon: 'none',
          title: '网络错误，换个网络试试',
        })
        reject(err)
      },
    })
  })
}

/**
 * GET 请求
 * @param url 后台地址
 * @param query 请求query参数
 * @returns
 */
export const httpGet = <T>(url: string, query?: Record<string, any>) => {
  return http<T>({
    url,
    query,
    method: 'GET',
  })
}

/**
 * POST 请求
 * @param url 后台地址
 * @param data 请求body参数
 * @param query 请求query参数，post请求也支持query，很多微信接口都需要
 * @returns
 */
export const httpPost = <T>(
  url: string,
  data?: Record<string, any>,
  query?: Record<string, any>,
) => {
  return http<T>({
    url,
    query,
    data,
    method: 'POST',
  })
}

http.get = httpGet
http.post = httpPost

const refreshToken = async () => {
  return await uni.request({
    method: 'POST',
    url: baseUrl + '/system/auth/refresh-token?refreshToken=' + getRefreshToken(),
    header: {
      'tenant-id': 1,
    },
  })
}
