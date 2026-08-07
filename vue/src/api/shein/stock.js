import request from '@/utils/request'

// 查询订单列表
export function listShopStock(query) {
  return request({
    url: '/api/open-api/shein/stock/list',
    method: 'get',
    params: query
  })
}


export function syncStockToOms(data) {
  return request({
    url: '/api/open-api/shein/stock/pushToOms',
    method: 'post',
    data: data
  })
}


// 接口拉取
export function pullShopStock(query) {
  return request({
    url: '/api/open-api/shein/stock/pull',
    method: 'get',
    params: query
  })
}


