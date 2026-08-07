import request from '@/utils/request'

// 查询Shein店铺分类
export function listShopCategory(query) {
  return request({
    url: '/api/open-api/shein/shop/category_list',
    method: 'get',
    params: query
  })
}


// 同步Shein店铺分类
export function pullShopCategory(data) {
  return request({
    url: '/api/open-api/shein/shop/pull_category',
    method: 'post',
    data: data
  })
}
// 同步分类属性
export function pullProductTypeAttr(data) {
  return request({
    url: '/api/open-api/shein/shop/pull_product_type_attr',
    method: 'post',
    data: data
  })
}

export function getProductTypeAttr(productTypeId) {
  return request({
    url: '/api/open-api/shein/shop/productTypeAttr/'+productTypeId,
    method: 'get'
  })
}

// 拉取所有分类属性
export function pullProductTypeAttrAll() {
  return request({
    url: '/api/open-api/shein/shop/pull_product_type_attr_all',
    method: 'post'
  })
}

export function pushProductTypeAttrOms() {
  return request({
    url: '/api/open-api/shein/shop/push_product_type_attr_oms',
    method: 'post'
  })
}
