import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import zhCN from '@/lang/zh-CN'
import enUS from '@/lang/en-US'

Vue.use(VueI18n)

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS
}

export function getLanguage() {
  const chooseLanguage = Cookies.get('language')
  if (chooseLanguage) return chooseLanguage

  // 如果没有选择语言
  const language = (navigator.language || navigator.browserLanguage).toLowerCase()
  const locales = Object.keys(messages)
  for (const locale of locales) {
    if (language.indexOf(locale.toLowerCase()) > -1) {
      return locale
    }
  }
  return 'zh-CN'
}

const i18n = new VueI18n({
  locale: getLanguage(),
  messages
})

export default i18n 