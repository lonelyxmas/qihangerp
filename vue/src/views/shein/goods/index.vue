<template>
  <div class="app-container">
        <!-- SHEIN内容 -->
          <div class="filter-container">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">

<!--              <el-form-item label="name">-->
<!--                <el-input v-model="queryParams.goodsName" placeholder="goods name" clearable />-->
<!--              </el-form-item>-->
              <el-form-item :label="$t('product.productNumber')">
                <el-input v-model="queryParams.supplierCode" :placeholder="$t('product.productNumber')" clearable />
              </el-form-item>
              <el-form-item label="spu name">
                <el-input v-model="queryParams.spuName" placeholder="spu name" clearable />
              </el-form-item>
              <el-form-item :label="$t('product.categoryId')">
                <el-input v-model="queryParams.categoryId" :placeholder="$t('product.categoryId')" clearable />
              </el-form-item>
              <el-form-item :label="$t('order.shop')" prop="shopId">
                <el-select v-model="queryParams.shopId" :placeholder="$t('order.shop')" clearable>
                  <el-option
                    v-for="item in shopList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuery">{{ $t('list.search') }}</el-button>
                <el-button @click="resetQuery">{{ $t('list.reset') }}</el-button>
              </el-form-item>
            </el-form>
            <el-row :gutter="10" class="mb8">
              <!--          <div class="operation-container">-->
              <!--            <el-button type="success" @click="handleImportIdoSell">拉取店铺商品</el-button>-->
              <el-col :span="1.5">
                <el-button
                  :loading="pullLoading"
                  type="success"
                  plain
                  icon="el-icon-download"
                  size="mini"
                  @click="handleBatchSync"
                >{{$t('shop.pullShopGoods')}}</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  :loading="pullLoading"
                  type="warning"
                  plain
                  icon="el-icon-upload"
                  size="mini"
                  @click="handlePushSheinGoodsOms"
                >{{$t('shop.pushShopGoodsToOms')}}</el-button>
              </el-col>
              <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
            </el-row>
          </div>

          <!-- 操作按钮区域 -->
<!--          <div class="operation-container">-->
<!--            <el-button type="success" @click="handleBatchSync">拉取店铺商品</el-button>-->
<!--            <el-button type="warning" @click="handlePushIdoSellGoodsOms">推送店铺商品到商品库</el-button>-->
<!--          </div>-->

          <!-- SHEIN表格区域 -->
          <el-table
            v-loading="loading"
            :data="goodsList"
            border
            style="width: 100%"
            ref="table"
            @selection-change="handleSelectionChange"
          >
<!--            <el-table-column type="selection" width="55" align="center" />-->
            <el-table-column :label="$t('product.productNumber')" prop="supplierCode" width="200" />
<!--            <el-table-column label="ID" prop="id" width="80" />-->
            <el-table-column label="Spu Name" prop="spuName" width="120" />
<!--            <el-table-column label="Skc Name" prop="skcName" width="120" />-->
<!--            <el-table-column label="Sku Code" prop="skuCode" width="120" />-->

            <el-table-column :label="$t('product.productInfo')" min-width="200">
              <template slot-scope="scope">
                <div class="goods-info">
                  <el-image
                    :src="scope.row.productImage"
                    :preview-src-list="[scope.row.productImage]"
                    class="goods-image"
                  />
                  <div class="goods-detail">
                    <div class="goods-name">{{ scope.row.productName }}</div>
                    <div class="goods-desc">{{ scope.row.productDesc }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
<!--            <el-table-column label="商品编号" prop="productNumber" width="120" />-->
            <el-table-column :label="$t('product.sku')" min-width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.skuList && scope.row.skuList.length > 0">
                  <el-popover
                    placement="right-start"
                    width="500"
                    popper-class="sku-popover"
                    trigger="hover">
                    <el-table :data="scope.row.skuList" size="mini" border>
                      <el-table-column property="skcName" label="skc name" width="120"></el-table-column>
                      <el-table-column property="colorValue" label="颜色" width="80"></el-table-column>
                      <el-table-column property="sizeValue" label="尺码" width="80"></el-table-column>
                      <el-table-column property="stockQuantity" label="库存" width="80" align="center"></el-table-column>
                      <el-table-column label="价格" width="100" align="right">
                        <template slot-scope="skuScope">
                          {{ skuScope.row.retailPrice | formatPrice }}
                        </template>
                      </el-table-column>
                    </el-table>
                    <div slot="reference" class="sku-summary">
                      <div class="sku-info">
                        <span>SKU: {{ scope.row.skuList.length }}</span>
                        <span class="divider">|</span>
                        <span>库存: {{ getTotalStock(scope.row.skuList) }}</span>
                      </div>
                      <div class="view-detail">查看详情 <i class="el-icon-arrow-right"></i></div>
                    </div>
                  </el-popover>
                </div>
                <div v-else>
                  暂无SKU信息
                </div>
              </template>
            </el-table-column>
<!--            <el-table-column label="库存信息" width="150" align="center">-->
<!--              <template slot-scope="scope">-->
<!--                <div>可用库存：{{ scope.row.usableInventory }}</div>-->
<!--                <div>总库存：{{ scope.row.inventoryQuantity }}</div>-->
<!--                <div>锁定库存：{{ scope.row.lockedQuantity }}</div>-->
<!--                <div>临时锁定：{{ scope.row.tempLockQuantity }}</div>-->
<!--              </template>-->
<!--            </el-table-column>-->
<!--            <el-table-column label="价格信息" width="150" align="center">-->
<!--              <template slot-scope="scope">-->
<!--                <div>商品价格：{{ scope.row.shopPrice }} {{ scope.row.currency }}</div>-->
<!--                <div>特殊价格：{{ scope.row.specialPrice }} {{ scope.row.currency }}</div>-->
<!--                <div>销售价格：{{ scope.row.salePrice }} {{ scope.row.currency }}</div>-->
<!--              </template>-->
<!--            </el-table-column>-->
            <el-table-column :label="$t('product.category')" width="150">
              <template slot-scope="scope">
                <div>{{ scope.row.categoryName }}</div>
                <div class="category-id">ID: {{ scope.row.categoryId }}</div>
              </template>
            </el-table-column>
<!--            <el-table-column label="包装信息" width="150" align="center">-->
<!--              <template slot-scope="scope">-->
<!--                <div>长：{{ scope.row.length }}cm</div>-->
<!--                <div>宽：{{ scope.row.width }}cm</div>-->
<!--                <div>高：{{ scope.row.height }}cm</div>-->
<!--                <div>重量：{{ scope.row.weight }}g</div>-->
<!--              </template>-->
<!--            </el-table-column>-->
<!--            <el-table-column label="创建时间" prop="createTime" width="160" />-->
            <el-table-column label="同步状态" width="120" align="center">
              <template slot-scope="scope">
                <el-tag :type="getSyncStatusType(scope.row.syncStatus)">
                  {{ getSyncStatusText(scope.row.syncStatus) }}
                </el-tag>
                <el-tooltip v-if="scope.row.errorMsg"
                            :content="scope.row.errorMsg"
                            placement="top">
                  <i class="el-icon-warning" style="color: #E6A23C; margin-left: 5px;"></i>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column :label="$t('menu.operate')" width="100" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button
                  plain
                  size="mini"
                  type="primary"
                  @click="handleSync(scope.row)"
                >同步到商品库</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页区域 -->
          <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />

  </div>
</template>

<script>
import { pullProductList,listGoods,syncSheinGoods,syncSheinGoodsBatch } from '@/api/shein/goods'
import request from '@/utils/request'
import { listShop } from "@/api/shop/shop";
import {syncIdoSellGoodsBatch} from "@/api/idosell/goods";

export default {
  name: 'GoodsShein',
  components: {
    Pagination: () => import('@/components/Pagination/index.vue')
  },
  filters: {
    formatPrice(value) {
      if (!value) return '0.00'
      return Number(value).toFixed(2)
    }
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      pullLoading: false,
      // 总条数
      total: 0,
      // 商品列表
      goodsList: [],
      shopList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        supplierCode: '',
        categoryId: '',
        spuName: '',
        shopId: '',
        status: '',
        goodsNum: null
      },
      // 店铺选项
      shopOptions: [
        { value: '1', label: '店铺1' },
        { value: '2', label: '店铺2' }
      ],
      // 选中数据的id数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      //
      showSearch: true,
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false
    }
  },
  created() {
    listShop({type: 1500}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });
  },
  methods: {
    // 标签页点击事件
    handleTabClick(tab) {
      // 更新路由参数
      this.$router.push({
        query: { ...this.$route.query, tab: tab.name }
      })
      this.resetQuery()
      // 加载数据
      this.getList()
    },
    // 获取列表数据
    getList() {
      this.loading = true
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 查询按钮点击事件
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    //
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        shopId: null,
        platformSkuId: null,
        sellerSkuCode: null,
        platformGoodsId: null,
        status: null,
        goodsNum: null
      }
      this.handleQuery()
    },
    // 同步商品
    handleSync(row) {
      this.$confirm('确认同步该商品数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        syncSheinGoods({ spuName: row.spuName }).then(response => {
          this.$message.success('商品同步成功')
          this.getList()
        }).catch(error => {
          this.$message.error(error.message || '同步失败')
        }).finally(() => {
          this.loading = false
        })
      })
    },
    //推送店铺商品到OMS
    handlePushSheinGoodsOms(){
      this.$confirm('确认同步所有商品到商品库吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        syncSheinGoodsBatch().then(response => {
          this.$message.success('商品同步成功')
          this.getList()
        }).finally(() => {
          this.loading = false
        })
      })

    },
    // 获取同步状态类型
    getSyncStatusType(status) {
      const statusMap = {
        0: 'info',    // 未同步
        1: 'success', // 已同步
        2: 'danger'   // 同步失败
      }
      return statusMap[status] || 'info'
    },
    // 获取同步状态文本
    getSyncStatusText(status) {
      const statusMap = {
        0: '未同步',
        1: '已同步',
        2: '同步失败'
      }
      return statusMap[status] || '未知'
    },
    // 批量同步
    handleBatchSync() {
      this.$confirm('确认批量SHEIN商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
          if(this.queryParams.shopId) {
            this.loading = true
            pullProductList({shopId:this.queryParams.shopId}).then(response => {
              this.$message.success('批量拉取成功')
              this.getList()
            }).catch(error => {
              this.$message.error(error.message || '批量拉取失败')
            }).finally(() => {
              this.loading = false
            })
          }else{
            this.$modal.msgSuccess("请先选择店铺");
          }
      }).catch(() => {
        // 取消操作
      })
    },
    // 表格多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 计算SKU总库存
    getTotalStock(skuList) {
      if (!skuList) return 0;
      return skuList.reduce((total, sku) => total + (sku.stockQuantity || 0), 0);
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.operation-container {
  margin-bottom: 20px;
}

.goods-info {
  display: flex;
  align-items: flex-start;

  .goods-image {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    border-radius: 4px;
  }

  .goods-detail {
    flex: 1;

    .goods-name {
      font-size: 14px;
      color: #303133;
      margin-bottom: 5px;
    }

    .goods-desc {
      font-size: 12px;
      color: #909399;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
  }
}

.category-id {
  font-size: 12px;
  color: #909399;
  margin-top: 3px;
}

.table-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
}

// 价格样式
.el-table {
  ::v-deep .cell {
    .price {
      color: #f56c6c;
      font-weight: bold;
    }
  }
}

.sku-summary {
  font-size: 12px;
  line-height: 1.5;
  cursor: pointer;
  padding: 8px 0;

  .sku-info {
    display: flex;
    align-items: center;
    color: #606266;

    .divider {
      margin: 0 8px;
      color: #DCDFE6;
    }
  }

  .view-detail {
    color: #409EFF;
    margin-top: 4px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    i {
      font-size: 12px;
      margin-left: 4px;
    }
  }

  &:hover {
    .view-detail {
      color: #66b1ff;
    }
  }
}
</style>

<style>
.sku-popover {
  padding: 12px !important;
}
.sku-popover .el-table {
  margin: 0 !important;
}
.sku-popover .el-table th {
  background-color: #f5f7fa !important;
}
.sku-popover .el-table td,
.sku-popover .el-table th {
  padding: 8px 0 !important;
}
</style>
