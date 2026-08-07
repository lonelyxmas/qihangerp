import request from '@/utils/request'

// 查询商品库存列表
export function listGoodsInventory(query) {
  return request({
    url: '/api/oms-api/goodsInventory/list',
    method: 'get',
    params: query
  })
}

// 查询商品库存详细
export function getGoodsInventory(id) {
  return request({
    url: '/api/oms-api/goodsInventory/' + id,
    method: 'get'
  })
}
//修改库存
export function updateGoodsInventoryQuantity(data) {
  return request({
    url: '/api/oms-api/goodsInventory/update',
    method: 'post',
    data: data
  })
}

//库存同步到平台
export function syncGoodsInventoryToShop(data) {
  return request({
    url: '/api/oms-api/goodsInventory/syncToShop',
    method: 'post',
    data: data
  })
}

