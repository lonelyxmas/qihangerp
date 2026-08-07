import request from '@/utils/request'

// 查询商品列表
export function listGoods(query) {
  return request({
    url: '/api/open-api/shein/goods/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getGoodsSku(id) {
  return request({
    url: '/api/open-api/shein/goods/' + id,
    method: 'get'
  })
}


// 批量拉取商品列表
export function pullProductList(data) {
  return request({
    url: '/api/open-api/shein/goods/pull',
    method: 'post',
    data: data
  })
}
// 同步Shein商品
export function syncSheinGoods(data) {
  return request({
    url: '/api/open-api/shein/goods/sync',
    method: 'post',
    data: data
  })
}
export function syncSheinGoodsBatch(data) {
  return request({
    url: '/api/open-api/shein/goods/batch-sync',
    method: 'post',
    data: data
  })
}
