/**
 * by 菲鸽 on 2024-03-06
 * 路由拦截，通常也是登录拦截
 * 可以设置路由白名单，或者黑名单，看业务需要选哪一个
 * 我这里应为大部分都可以随便进入，所以使用黑名单
 *
 * update 2024-10-09
 * 登录之后，会进行页面跳转。在完成页面跳转之前，就需要获取到用户信息
 * 并设置到 userStore 中。
 *
 * 如何设置当前页面需要校验登录状态？
 * 在 router 块中设置
 *
 * <route lang="json5">
    {
      layout: 'tabbar',
      style: {
        navigationStyle: 'custom',
        navigationBarTitleText: '首页',
      },
      needLogin: true
    }
    </route>
 */
import { useUserStore } from '@/store'
import { getNeedLoginPages, needLoginPages as _needLoginPages } from '@/utils'
import { getAccessToken } from '@/utils/auth'

// TODO Check
const loginRoute = '/pages/login/index'

// const isSetUserInfo = () => {
//   const userStore = useUserStore()
//   return userStore.userInfo.isSetUser
// }

const isDev = import.meta.env.DEV

// 黑名单登录拦截器 - （适用于大部分页面不需要登录，少部分页面需要登录）
const navigateToInterceptor = {
  // 注意，这里的url是 '/' 开头的，如 '/pages/index/index'，跟 'pages.json' 里面的 path 不同
  // 拦截前触发
  async invoke(option) {
    const { url } = option
    console.log('navigateToInterceptor invoke')
    // console.log(url) // /pages/route-interceptor/index?name=feige&age=30
    const path = url.split('?')[0]
    let needLoginPages: string[] = []
    // 为了防止开发时出现BUG，这里每次都获取一下。生产环境可以移到函数外，性能更好
    if (isDev) {
      needLoginPages = getNeedLoginPages()
    } else {
      needLoginPages = _needLoginPages
    }
    const isNeedLogin = needLoginPages.includes(path)
    // console.log('the path: {} is needLogin? {}', path, isNeedLogin)
    if (!isNeedLogin) {
      // 当前页面不需要登录
      console.log('the path: {} is not needLogin', url)
      return option
    }

    // 下面的逻辑跟 PC 端差不多
    if (getAccessToken()) {
      if (path === '/pages/login/index') {
        // 这里写死，避免在 login 页面中设置了 needLogin，导致死循环
        return option
      } else {
        // todo  获取字典数据并保存到本地

        // 获取用户的相关信息
        const userStore = useUserStore()
        if (!userStore.userInfo.isSetUser) {
          // todo 显示加载状态
          await userStore.setUserInfoAction()
          // todo 菜单权限应该也需从这里进行逻辑上的设置
        } else {
          return option
        }
      }
    } else {
      // 当前页面需要登录，但是获取不到token，这个时候跳转到登录页面
      uni.navigateTo({ url: '/pages/login/index' })
      return option
    }
  },
}

export const routeInterceptor = {
  install() {
    uni.addInterceptor('navigateTo', navigateToInterceptor)
    uni.addInterceptor('reLaunch', navigateToInterceptor)
    uni.addInterceptor('redirectTo', navigateToInterceptor)
  },
}
