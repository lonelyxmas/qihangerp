import request from '@/utils/request'

// 查询商品分类属性列表
export function listAttribute(query) {
  return request({
    url: '/api/oms-api/goods_attribute/list',
    method: 'get',
    params: query
  })
}
export function listAttributeValue(query) {
  return request({
    url: '/api/oms-api/goods_attribute/value_list',
    method: 'get',
    params: query
  })
}
