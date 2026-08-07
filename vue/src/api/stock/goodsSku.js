import request from '@/utils/request'


// 查询商品规格列表
export function searchSku(query) {
  return request({
    url: 'api/oms-api/goods/searchSku',
    method: 'get',
    params: query
  })
}
