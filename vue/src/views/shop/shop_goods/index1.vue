<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane v-for="tab in tabList"
                   :key="tab.name"
                   :label="tab.label"
                   :name="tab.name">
        <!-- IdoSell内容 -->
        <template v-if="tab.name === 'idosell'">
          <div class="filter-container">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">
              <el-form-item label="商品编号">
                <el-input v-model="queryParams.goodsNum" placeholder="请输入商品编号" clearable />
              </el-form-item>
              <el-form-item label="平台sku id">
                <el-input v-model="queryParams.platformSkuId" placeholder="请输入平台sku id" clearable />
              </el-form-item>
              <el-form-item label="商家SKU编码">
                <el-input v-model="queryParams.sellerSkuCode" placeholder="请输入商家SKU编码" clearable />
              </el-form-item>
              <el-form-item label="平台商品ID">
                <el-input v-model="queryParams.platformGoodsId" placeholder="请输入平台商品ID" clearable />
              </el-form-item>
              <el-form-item label="店铺">
                <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable>
                  <el-option
                    v-for="item in shopOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="商品状态">
                <el-select v-model="queryParams.status" placeholder="请选择商品状态" clearable>
                  <el-option label="在售" value="1" />
                  <el-option label="下架" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuery">查询</el-button>
                <el-button @click="resetQuery">{{ $t('list.reset') }}</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 操作按钮区域 -->
          <div class="operation-container">
            <el-button type="primary" @click="handleImportIdoSell">API导入商品</el-button>
            <el-button type="success" @click="handleSyncStock">同步库存</el-button>
            <el-button type="warning" @click="handleSyncPrice">同步价格</el-button>
          </div>

          <!-- 表格区域 -->
          <el-table
            v-loading="loading"
            :data="goodsList"
            border
            style="width: 100%"
            ref="table"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="商品ID" prop="productId" width="120" />
            <el-table-column label="商品编号" prop="goodsNum" width="120" />
            <el-table-column label="商品信息" min-width="300">
              <template slot-scope="scope">
                <div class="goods-info">
                  <el-image
                    :src="scope.row.mainImage"
                    :preview-src-list="[scope.row.mainImage]"
                    class="goods-image"
                  />
                  <div class="goods-detail">
                    <div class="goods-name">{{ scope.row.productName }}</div>
                    <div class="goods-desc">{{ scope.row.productDesc }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="SKU列表" min-width="150">
              <template slot-scope="scope">
                <div v-if="scope.row.idosellGoodsSkuList && scope.row.idosellGoodsSkuList.length > 0">
                  <el-popover
                    placement="right-start"
                    width="500"
                    popper-class="sku-popover"
                    trigger="hover">
                    <el-table :data="scope.row.idosellGoodsSkuList" size="mini" border>
                      <el-table-column property="skuId" label="SKU ID" width="120"></el-table-column>
                      <el-table-column property="color" label="颜色" width="80"></el-table-column>
                      <el-table-column property="sizeName" label="尺码" width="80"></el-table-column>
                      <el-table-column property="stockQuantity" label="库存" width="80" align="center"></el-table-column>
                      <el-table-column label="价格" width="100" align="right">
                        <template slot-scope="skuScope">
                          {{ skuScope.row.retailPrice | formatPrice }}
                        </template>
                      </el-table-column>
                    </el-table>
                    <div slot="reference" class="sku-summary">
                      <div class="sku-info">
                        <span>SKU: {{ scope.row.idosellGoodsSkuList.length }}</span>
                        <span class="divider">|</span>
                        <span>库存: {{ getTotalStock(scope.row.idosellGoodsSkuList) }}</span>
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
            <el-table-column label="分类" width="150">
              <template slot-scope="scope">
                <div>{{ scope.row.categoryName }}</div>
                <div class="category-id">ID: {{ scope.row.categoryId }}</div>
              </template>
            </el-table-column>
            <el-table-column label="价格信息" width="150" align="center">
              <template slot-scope="scope">
                <div>零售价：{{ scope.row.retailPrice | formatPrice }}</div>
                <div>批发价：{{ scope.row.wholesalePrice | formatPrice }}</div>
              </template>
            </el-table-column>
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
            <el-table-column :label="$t('menu.operate')" width="100" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="success"
                  @click="handleSync(scope.row)"
                >同步</el-button>
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
        </template>
        <!-- SHEIN内容 -->
        <template v-else-if="tab.name === 'shein'">
          <div class="filter-container">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">
              <el-form-item label="平台sku id">
                <el-input v-model="queryParams.platformSkuId" placeholder="请输入平台sku id" clearable />
              </el-form-item>
              <el-form-item label="商家SKU编码">
                <el-input v-model="queryParams.sellerSkuCode" placeholder="请输入商家SKU编码" clearable />
              </el-form-item>
              <el-form-item label="平台商品ID">
                <el-input v-model="queryParams.platformGoodsId" placeholder="请输入平台商品ID" clearable />
              </el-form-item>
              <el-form-item label="店铺" prop="shopId">
                <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable>
                  <el-option
                    v-for="item in shopList"
                    :key="item.id"
                    :label="item.shopName"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuery">查询</el-button>
                <el-button @click="resetQuery">{{ $t('list.reset') }}</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 操作按钮区域 -->
          <div class="operation-container">
            <el-button type="success" @click="handleBatchSync">批量拉取</el-button>
          </div>

          <!-- SHEIN表格区域 -->
          <el-table
            v-loading="loading"
            :data="goodsList"
            border
            style="width: 100%"
            ref="table"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="SPU名称" prop="spuName" width="120" />
            <el-table-column label="SKC名称" prop="skcName" width="120" />
            <el-table-column label="SKU编码" prop="skuCode" width="120" />
            <el-table-column label="商品信息" min-width="200">
              <template slot-scope="scope">
                <div class="goods-info">
                  <el-image
                    :src="scope.row.imageSmallUrl"
                    :preview-src-list="[scope.row.imageSmallUrl]"
                    class="goods-image"
                  />
                  <div class="goods-detail">
                    <div class="goods-name">{{ scope.row.productName }}</div>
                    <div class="goods-desc">{{ scope.row.productDesc }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="商品编号" prop="productNumber" width="120" />
            <el-table-column label="库存信息" width="150" align="center">
              <template slot-scope="scope">
                <div>可用库存：{{ scope.row.usableInventory }}</div>
                <div>总库存：{{ scope.row.inventoryQuantity }}</div>
                <div>锁定库存：{{ scope.row.lockedQuantity }}</div>
                <div>临时锁定：{{ scope.row.tempLockQuantity }}</div>
              </template>
            </el-table-column>
            <el-table-column label="价格信息" width="150" align="center">
              <template slot-scope="scope">
                <div>商品价格：{{ scope.row.shopPrice }} {{ scope.row.currency }}</div>
                <div>特殊价格：{{ scope.row.specialPrice }} {{ scope.row.currency }}</div>
                <div>销售价格：{{ scope.row.salePrice }} {{ scope.row.currency }}</div>
              </template>
            </el-table-column>
            <el-table-column label="分类信息" width="150">
              <template slot-scope="scope">
                <div>{{ scope.row.categoryName }}</div>
                <div class="category-id">ID: {{ scope.row.categoryId }}</div>
              </template>
            </el-table-column>
            <el-table-column label="包装信息" width="150" align="center">
              <template slot-scope="scope">
                <div>长：{{ scope.row.length }}cm</div>
                <div>宽：{{ scope.row.width }}cm</div>
                <div>高：{{ scope.row.height }}cm</div>
                <div>重量：{{ scope.row.weight }}g</div>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime" width="160" />
            <el-table-column :label="$t('menu.operate')" width="100" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="success"
                  disabled
                  @click="handleSync(scope.row)"
                >同步</el-button>
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
        </template>
        <!-- 其他标签页的内容 -->
        <template v-else>
          <div class="filter-container">
            <el-form :inline="true" :model="queryParams" class="demo-form-inline">
              <el-form-item label="平台sku id">
                <el-input v-model="queryParams.platformSkuId" placeholder="请输入平台sku id" clearable />
              </el-form-item>
              <el-form-item label="商家SKU编码">
                <el-input v-model="queryParams.sellerSkuCode" placeholder="请输入商家SKU编码" clearable />
              </el-form-item>
              <el-form-item label="平台商品ID">
                <el-input v-model="queryParams.platformGoodsId" placeholder="请输入平台商品ID" clearable />
              </el-form-item>
              <el-form-item label="店铺">
                <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable>
                  <el-option
                    v-for="item in shopOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="商品状态">
                <el-select v-model="queryParams.status" placeholder="请选择商品状态" clearable>
                  <el-option label="在售" value="1" />
                  <el-option label="下架" value="0" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuery">查询</el-button>
                <el-button @click="resetQuery">{{ $t('list.reset') }}</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 操作按钮区域 -->
          <div class="operation-container">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="success" @click="handleBatchSync">批量同步</el-button>
            <el-button type="warning" @click="handleBatchDelete">批量删除</el-button>
          </div>

          <!-- 表格区域 -->
          <el-table
            v-loading="loading"
            :data="goodsList"
            border
            style="width: 100%"
            ref="table"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="平台sku id" prop="platformSkuId" width="120" />
            <el-table-column label="商品编码" prop="goodsCode" width="120" />
            <el-table-column label="商品标题" prop="goodsTitle" min-width="200">
              <template slot-scope="scope">
                <div class="goods-info">
                  <el-image
                    :src="scope.row.mainImage"
                    :preview-src-list="[scope.row.mainImage]"
                    class="goods-image"
                  />
                  <span class="goods-title">{{ scope.row.goodsTitle }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="主图" prop="mainImage" width="100">
              <template slot-scope="scope">
                <el-image
                  :src="scope.row.mainImage"
                  :preview-src-list="[scope.row.mainImage]"
                  class="table-image"
                />
              </template>
            </el-table-column>
            <el-table-column label="SKU属性" prop="skuProperties" width="150" />
            <el-table-column label="商品库存" prop="stock" width="100" align="center" />
            <el-table-column label="商品价格/元" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.price | formatPrice }}
              </template>
            </el-table-column>
            <el-table-column label="商品状态" prop="status" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">
                  {{ scope.row.status === '1' ? '在售' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column :label="$t('menu.operate')" width="200" align="center">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  @click="handleEdit(scope.row)"
                >编辑</el-button>
                <el-button
                  size="mini"
                  type="success"
                  @click="handleSync(scope.row)"
                >同步</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  @click="handleDelete(scope.row)"
                >删除</el-button>
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
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { listIdoSellGoods, importIdoSellGoods, syncIdoSellGoodsStock, syncIdoSellGoodsPrice, syncIdoSellGoods, delIdoSellGoods } from '@/api/idosell/goods'
import { pullProductList } from '@/api/open-api/shein/goods'
import request from '@/utils/request'

export default {
  name: 'ShopGoods',
  components: {
    Pagination: () => import('@/components/Pagination')
  },
  filters: {
    formatPrice(value) {
      if (!value) return '0.00'
      return Number(value).toFixed(2)
    }
  },
  data() {
    return {
      // 标签页配置
      tabList: [
        { label: 'SHEIN', name: 'shein' },
        { label: 'IdoSell', name: 'idosell' }
      ],
      // 激活的标签页
      activeTab: 'shein',
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 商品列表
      goodsList: [],
      shopList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        platformSkuId: '',
        sellerSkuCode: '',
        platformGoodsId: '',
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
    // 根据路由参数设置初始标签页
    const tab = this.$route.query.tab
    if (tab) {
      this.activeTab = tab
    }
    // 加载当前标签页的数据
    this.getList()
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
      const api = this.getApiByTab(this.activeTab)

      // 根据不同的标签页使用不同的请求参数
      const params = { ...this.queryParams }
      if (this.activeTab === 'shein') {
        // SHEIN的特殊处理
        params.platformCode = 'SHEIN'
      }

      // 发送请求
      request({
        url: api,
        method: 'get',
        params: params
      }).then(response => {
        this.goodsList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
        this.goodsList = []
        this.total = 0
      })
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
    // API导入商品
    handleImportIdoSell() {
      this.$confirm('确认从IdoSell导入商品数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        importIdoSellGoods().then(response => {
          this.$message.success('商品导入成功')
          this.getList()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 同步库存
    handleSyncStock() {
      this.$confirm('确认同步所选商品的库存数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        syncIdoSellGoodsStock().then(response => {
          this.$message.success('库存同步成功')
          this.getList()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 同步价格
    handleSyncPrice() {
      this.$confirm('确认同步所选商品的价格数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        syncIdoSellGoodsPrice().then(response => {
          this.$message.success('价格同步成功')
          this.getList()
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 编辑商品
    handleEdit(row) {
      // TODO: 跳转到编辑页面
      this.$router.push({ path: '/shop/shop_goods/edit', query: { id: row.productId }})
    },
    // 同步商品
    handleSync(row) {
      this.$confirm('确认同步该商品数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        syncIdoSellGoods({ productId: row.productId }).then(response => {
          this.$message.success('商品同步成功')
          this.getList()
        }).catch(error => {
          this.$message.error(error.message || '同步失败')
        }).finally(() => {
          this.loading = false
        })
      })
    },
    // 删除商品
    handleDelete(row) {
      this.$confirm('确认删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }).then(() => {
        this.loading = true
        delIdoSellGoods(row.productId).then(response => {
          this.$message.success('删除成功')
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
    // 新增商品
    handleAdd() {
      this.$router.push({ path: '/shop/shop_goods/add' })
    },
    // 批量同步
    handleBatchSync() {
      this.$confirm('确认批量SHEIN商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        if (this.activeTab === 'shein') {
          pullProductList({}).then(response => {
            this.$message.success('批量拉取成功')
            this.getList()
          }).catch(error => {
            this.$message.error(error.message || '批量拉取失败')
          }).finally(() => {
            this.loading = false
          })
        }
      }).catch(() => {
        // 取消操作
      })
    },
    // 批量删除
    handleBatchDelete() {
      const selection = this.$refs.table.selection
      if (selection.length === 0) {
        this.$message.warning('请至少选择一条记录')
        return
      }
      this.$confirm('确认批量删除选中的商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }).then(() => {
        // TODO: 实现批量删除逻辑
        this.$message.success('批量删除成功')
      })
    },
    // 根据标签页获取不同的API
    getApiByTab(tab) {
      const apiMap = {
        'taobao': '/api/shop/taobao/goods/list',
        'jd': '/api/shop/jd/goods/list',
        'jdself': '/api/shop/jdself/goods/list',
        'pdd': '/api/shop/pdd/goods/list',
        'douyin': '/api/shop/douyin/goods/list',
        'video': '/api/shop/video/goods/list',
        'shein': '/api/open-api/shein/goods/list',
        'idosell': '/api/open-api/idosell/goods/list'
      }
      return apiMap[tab] || apiMap['taobao']
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
