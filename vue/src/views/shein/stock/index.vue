<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item label="Spu Name" prop="spuName">
        <el-input
          v-model="queryParams.spuName"
          placeholder="Spu Name"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('product.productNumber')" prop="goodsNum">
        <el-input
          v-model="queryParams.goodsNum"
          :placeholder="$t('product.productNumber')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item :label="$t('product.skuCode')" prop="skuCode">-->
<!--        <el-input-->
<!--          v-model="queryParams.skuCode"-->
<!--          :placeholder="$t('product.skuCode')"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="Skc Name" prop="skcName">
        <el-input
          v-model="queryParams.skcName"
          placeholder="Skc Name"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('product.skuId')" prop="skuId">
        <el-input
          v-model="queryParams.skuId"
          :placeholder="$t('product.skuId')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

<!--      <el-form-item :label="$t('order.shop')" prop="shopId">-->
<!--        <el-select v-model="queryParams.shopId" :placeholder="$t('order.pleaseSelectShop')" clearable @change="handleQuery">-->
<!--          <el-option-->
<!--            v-for="item in shopList"-->
<!--            :key="item.id"-->
<!--            :label="item.name"-->
<!--            :value="item.id">-->
<!--          </el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('list.search') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('list.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          :loading="pullLoading"
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handlePullShopStock"
        >{{$t('stock.pullShopStockList')}}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsInventoryList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      <el-table-column label="主键ID" align="center" prop="id" />-->
      <el-table-column label="spu" align="center" prop="spuName" />
<!--      <el-table-column label="skc" align="center" prop="skcName" />-->
      <el-table-column label="Sku" align="center" prop="skuCode" />
      <el-table-column :label="$t('product.productName')" align="left" prop="productName" width="300"/>
      <el-table-column :label="$t('product.productNumber')" align="left" prop="supplierCode" />
      <el-table-column :label="$t('product.color')" align="center" prop="colorValue" />
      <el-table-column :label="$t('product.size')" align="center" prop="sizeValue" />
      <el-table-column :label="$t('stock.inventory')" align="center" prop="totalInventoryQuantity" />
      <el-table-column :label="$t('stock.lockedQty')" align="center" prop="totalLockedQuantity" />
      <el-table-column :label="$t('stock.outOfStockQty')" align="center" prop="totalOutOfStockQty" />
      <el-table-column :label="$t('stock.tempLockQuantity')" align="center" prop="totalTempLockQuantity" />
      <el-table-column :label="$t('stock.usableInventory')" align="center" prop="totalUsableInventory" />

      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            plain
            type="success"
            icon="el-icon-upload"
            @click="handlePushStockToOms(scope.row)"
          >{{ $t('stock.pushStockToOms') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

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
import {pullShopStock,syncStockToOms,listShopStock} from "@/api/shein/stock";
import { listShop } from "@/api/shop/shop";
export default {
  name: "StockShein",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedErpGoodsInventoryDetail: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      pullLoading: false,
      //
      showSearch: true,
      // 总条数
      total: 0,
      // 商品库存表格数据
      goodsInventoryList: [],
      shopList:[],
      // 商品库存明细表格数据
      erpGoodsInventoryDetailList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopId: null,
        goodsNumber: null,
        specId: null,
        specNumber: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
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
    /** 查询商品库存列表 */
    getList() {
      this.loading = true;
      listShopStock(this.queryParams).then(response => {
        this.goodsInventoryList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    //
    reset() {
      this.form = {};
      this.erpGoodsInventoryDetailList = [];
      this.resetForm("form");
    },
    /**  */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /**  */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 更新店铺库存 */
    handlePullShopStock(row) {
      if(this.queryParams.shopId) {
        this.pullLoading =true
        pullShopStock({shopId: this.queryParams.shopId}).then(response => {
          this.$modal.msgSuccess("后台更新中。。。")
          this.getList()
          this.pullLoading =false
        });
      }else{
        this.$modal.msgError("请选择店铺")
      }
    },
    handlePushStockToOms(row) {
      this.$modal.confirm('是否确认同步该商品sku库存到OMS？').then(() => {

      }).then(() => {
        syncStockToOms({id:row.id}).then(resp=>{
          this.$modal.msgSuccess("同步成功");
        })

      }).catch(() => {});
    }

  }
};
</script>
