import request from '@/utils/request'

// 查询商品分类属性列表
export function listAttribute(query) {
  return request({
    url: '/api/open-api/shein/attribute/list',
    method: 'get',
    params: query
  })
}
export function listAttributeValue(id) {
  return request({
    url: '/api/open-api/shein/attribute/value_list/'+id,
    method: 'get'
  })
}
