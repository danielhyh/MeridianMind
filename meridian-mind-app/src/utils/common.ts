/**
 * 显示模态弹窗
 *
 * @param content 提示的标题
 */
export function showConfirm(content: string) {
  return new Promise((resolve, reject) => {
    uni.showModal({
      title: '提示',
      content,
      cancelText: '取消',
      confirmText: '确定',
      success: function (res) {
        resolve(res)
      },
    })
  })
}

/**
 * 显示消息提示框
 *
 * @param content 提示的标题
 */
export function toast(content: string) {
  uni.showToast({
    title: content,
    icon: 'none',
  })
}
