import request from '@/utils/request'

// 查询IdoSell商品列表
export function listIdoSellGoods(query) {
  return request({
    url: '/api/open-api/idosell/goods/list',
    method: 'get',
    params: query
  })
}

// 获取IdoSell商品详细信息
export function getIdoSellGoods(id) {
  return request({
    url: '/api/open-api/idosell/goods/' + id,
    method: 'get'
  })
}

// 新增IdoSell商品
export function addIdoSellGoods(data) {
  return request({
    url: '/api/open-api/idosell/goods',
    method: 'post',
    data: data
  })
}

// 修改IdoSell商品
export function updateIdoSellGoods(data) {
  return request({
    url: '/api/open-api/idosell/goods',
    method: 'put',
    data: data
  })
}

// 删除IdoSell商品
export function delIdoSellGoods(id) {
  return request({
    url: '/api/open-api/idosell/goods/' + id,
    method: 'delete'
  })
}

// 同步IdoSell商品
export function syncIdoSellGoods(data) {
  return request({
    url: '/api/open-api/idosell/goods/sync',
    method: 'post',
    data: data
  })
}

// API导入IdoSell商品
export function importIdoSellGoods(data) {
  return request({
    url: '/api/open-api/idosell/goods/import',
    method: 'post',
    data: data
  })
}

// 同步IdoSell商品库存
export function syncIdoSellGoodsBatch(data) {
  return request({
    url: '/api/open-api/idosell/goods/batch-sync',
    method: 'post',
    data: data
  })
}

// 同步IdoSell商品价格
export function syncIdoSellGoodsPrice(data) {
  return request({
    url: '/api/open-api/idosell/goods/sync/price',
    method: 'post',
    data: data
  })
}
