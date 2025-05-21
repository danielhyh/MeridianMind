type UserVO = {
  id: number
  avatar: string
  nickname: string
  gender: number
  mobile: string
}

// USER 缓存
type UserInfoVO = {
  permissions: string[]
  roles: string[]
  isSetUser: boolean
  user: UserVO
}

declare namespace JSX {
  interface IntrinsicElements {
    block: any // 或者更具体的类型定义
  }
}
