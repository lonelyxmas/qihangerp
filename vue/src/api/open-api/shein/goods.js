// import request from '@/utils/request'
//
// // 查询商品列表
// export function listGoods(query) {
//   return request({
//     url: '/api/open-api/shein/goods/list',
//     method: 'get',
//     params: query
//   })
// }
//
// // 查询商品详细
// export function getGoods(id) {
//   return request({
//     url: '/api/open-api/shein/goods/' + id,
//     method: 'get'
//   })
// }
//
// // 关联商品
// export function linkGoods(data) {
//   return request({
//     url: '/api/open-api/shein/goods/link',
//     method: 'post',
//     data: data
//   })
// }
//
// // API更新商品数据
// export function updateGoods(shopId) {
//   return request({
//     url: '/api/oms-api/shein/goods/update',
//     method: 'post',
//     params: { shopId }
//   })
// }
//
// // API拉取全量数据
// export function pullGoods(shopId) {
//   return request({
//     url: '/api/oms-api/shein/goods/pull',
//     method: 'post',
//     params: { shopId }
//   })
// }
//
// // 批量拉取商品列表
// export function pullProductList(data) {
//   return request({
//     url: '/api/oms-api/shein/goods/pull',
//     method: 'post',
//     data: data
//   })
// }
//
// // 获取商品列表
// export function listSheinGoods(query) {
//   return request({
//     url: '/api/open-api/shein/goods/list',
//     method: 'get',
//     params: query
//   })
// }
//
// // 同步单个商品
// export function syncSheinGoods(data) {
//   return request({
//     url: '/api/open-api/shein/goods/sync/single',
//     method: 'post',
//     data: data
//   })
// }
