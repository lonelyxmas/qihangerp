import request from '@/utils/request'

// 查询商品分类属性列表
export function listIdosellAttribute(query) {
  return request({
    url: '/api/open-api/idosell/attribute/list',
    method: 'get',
    params: query
  })
}
export function listIdosellAttributeValue(id) {
  return request({
    url: '/api/open-api/idosell/attribute/value_list/'+id,
    method: 'get'
  })
}

// 绑定OMS属性ID
export function bindOmsAttribute(data) {
  return request({
    url: '/api/open-api/idosell/attribute/bind_oms_attribute',
    method: 'post',
    data: data
  })
}
// 绑定OMS属性值ID
export function bindOmsAttributeValue(data) {
  return request({
    url: '/api/open-api/idosell/attribute/bind_oms_attribute_value',
    method: 'post',
    data: data
  })
}
