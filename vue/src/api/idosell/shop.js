import request from '@/utils/request'

// 查询IdoSell商品列表
export function listShopCategory(query) {
  return request({
    url: '/api/open-api/idosell/shop/category_list',
    method: 'get',
    params: query
  })
}


// 同步IdoSell分类
export function pullShopCategory(data) {
  return request({
    url: '/api/open-api/idosell/shop/pull_category',
    method: 'post',
    data: data
  })
}


export function pullSizeAttrAll(data) {
  return request({
    url: '/api/open-api/idosell/shop/pull_size_attr',
    method: 'post',
    data: data
  })
}

// 绑定OMS分类ID
export function bindOmsCategory(data) {
  return request({
    url: '/api/open-api/idosell/shop/bind_oms_category',
    method: 'post',
    data: data
  })
}
