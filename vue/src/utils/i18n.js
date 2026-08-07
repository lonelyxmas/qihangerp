import Vue from 'vue'
import i18n from '@/lang'

// 转换路由的标题
export function generateTitle(title) {
  const hasKey = i18n.te('route.' + title)
  if (hasKey) {
    return i18n.t('route.' + title)
  }
  return title
} 