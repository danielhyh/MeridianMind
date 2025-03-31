import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAccessToken, removeToken } from '@/utils/auth'
import { getInfo, loginOut } from '@/service/login/LoginAPI'

const initState = {
  permissions: [],
  roles: [],
  isSetUser: false,
  user: {
    id: 0,
    avatar: '',
    nickname: '',
    deptId: 0,
  },
}

export const useUserStore = defineStore(
  'user',
  () => {
    // state

    const userInfo = ref<UserInfoVO>({ ...initState })

    // actions methods
    const setUserInfoAction = async () => {
      if (!getAccessToken()) {
        // 获取不到accessToken，直接返回
        resetState()
        return
      }

      const data = await getInfo()
      userInfo.value = {
        ...data,
        isSetUser: true,
      }
    }

    const setUserAvatarAction = async (avatar: string) => {
      userInfo.value.user.avatar = avatar
    }

    const setUserNicknameAction = async (nickname: string) => {
      userInfo.value.user.nickname = nickname
    }

    const LogOut = async () => {
      await loginOut()
      removeToken()
      resetState()
    }

    const resetState = () => {
      console.log('initState', initState)
      userInfo.value = initState
      console.log('重置userInfo', userInfo.value)
    }

    // 暴露到外面的方法
    return {
      userInfo,
      setUserInfoAction,
      setUserAvatarAction,
      setUserNicknameAction,
      LogOut,
      resetState,
    }
  },
  {
    // 持久化
    persist: true,
  },
)
