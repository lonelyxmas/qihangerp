<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item :label="$t('order.orderNum')" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          :placeholder="$t('order.orderNum')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item label="子订单号" prop="subOrderNum">-->
<!--        <el-input-->
<!--          v-model="queryParams.subOrderNum"-->
<!--          placeholder="请输入子订单号"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item :label="$t('order.orderTime')" prop="orderTime">
        <el-date-picker clearable
                        v-model="orderTime" value-format="yyyy-MM-dd"
                        type="daterange"
                        range-separator="-"
                        start-placeholder="Begin Time"
                        end-placeholder="End Time">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('order.shop')" prop="shopId">
        <el-select v-model="queryParams.shopId" :placeholder="$t('order.shop')" clearable @change="handleQuery">
         <el-option
            v-for="item in shopList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
            <span style="float: left">{{ item.name }}</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 500">视频号小店</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 200">京东POP</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 280">京东自营</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 100">淘宝天猫</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 300">拼多多</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 400">抖店</span>
           <span style="float: right; color: #8492a6; font-size: 13px"  v-if="item.type === 999">线下渠道</span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item :label="$t('order.refundStatus')" prop="refundStatus">
        <el-select v-model="queryParams.refundStatus" :placeholder="$t('order.refundStatus')" clearable @change="handleQuery">
          <el-option :label="$t('order.refund.none')" value="1" ></el-option>
          <el-option :label="$t('order.refund.processing')" value="2"></el-option>
<!--          <el-option label="退款中" value="3"> </el-option>-->
          <el-option :label="$t('order.refund.completed')"  value="4"></el-option>
        </el-select>
      </el-form-item>
<!--      <el-form-item label="是否关联" prop="hasLink">-->
<!--        <el-select v-model="queryParams.hasLink" placeholder="是否关联" clearable @change="handleQuery">-->
<!--          <el-option label="未关联" value="0"></el-option>-->
<!--          <el-option label="已关联" value="1"></el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('list.search') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('list.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      <el-table-column label="子订单编号" align="center" prop="subOrderNum" />-->
      <el-table-column :label="$t('order.orderNum')" align="center" prop="orderNum" />
      <el-table-column :label="$t('order.shop')" align="center" prop="shopId" >
        <template slot-scope="scope">
          <el-tag type="info">{{ shopList.find(x=>x.id === scope.row.shopId) ? shopList.find(x=>x.id === scope.row.shopId).name : '' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('order.list.picture')"  prop="goodsImg" width="100px">
        <template slot-scope="scope">
<!--              <el-image style="width: 70px; height: 70px;" :src="scope.row.goodsImg"></el-image>-->
          <el-image  style="width: 40px; height: 40px;" :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
        </template>
      </el-table-column>
      <el-table-column :label="$t('order.list.goodsTitle')" align="left" prop="goodsTitle" width="250px"/>
      <el-table-column :label="$t('order.list.skuName')" align="center" prop="goodsSpec" />
      <el-table-column :label="$t('order.list.skuCode')" align="center" prop="skuNum" />
      <el-table-column :label="$t('order.list.quantity')" align="center" prop="quantity" />
<!--      <el-table-column label="平台SkuId" align="center" prop="skuId" />-->
<!--      <el-table-column label="系统 SkuId" align="center" prop="goodsSkuId" />-->
<!--      <el-table-column label="外部ERP SkuId" align="center" prop="outerErpSkuId" />-->
<!--      <el-table-column label="子订单金额" align="center" prop="itemAmount" :formatter="amountFormatter"/>-->
<!--      <el-table-column label="备注" align="center" prop="remark" />-->
      <el-table-column :label="$t('order.refundStatus')" align="center" prop="refundStatus" >
        <template slot-scope="scope">
           <!-- 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 -->
           <el-tag v-if="scope.row.refundStatus === 1">{{ $t('order.refund.none') }}</el-tag>
           <el-tag v-if="scope.row.refundStatus === 2">{{ $t('order.refund.processing') }}</el-tag>
           <el-tag v-if="scope.row.refundStatus === 3">{{ $t('order.refund.processing') }}</el-tag>
           <el-tag v-if="scope.row.refundStatus === 4">{{ $t('order.refund.completed') }}</el-tag>
        </template>
      </el-table-column>
<!--      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleEditErpSku(scope.row)"-->
<!--          >补充ERP SKU</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <!-- 修改Erp Sku 对话框 -->
    <el-dialog title="修改ERP SKU ID" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="ERP商品skuId" prop="erpSkuId">
          <el-input type="number" v-model="form.erpSkuId" placeholder="请输入ERP商品skuId" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{$t('list.submit')}}</el-button>
        <el-button @click="cancel">{{$t('list.cancel')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listOrderItem, updateErpSkuId} from "@/api/order/order";
import { listShop } from "@/api/shop/shop";

export default {
  name: "Order",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedSShopOrderItem: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      //
      showSearch: true,
      // 总条数
      total: 0,
      // 店铺订单表格数据
      orderList: [],
      // ${subTable.functionName}表格数据
      sShopOrderItemList: [],
      shopList:[],
      // 弹出层标题
      open:false,
      orderTime: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNum: null,
        shopId: null,
        subOrderNum: null,
        startTime: null,
        endTime: null,
        refundStatus: null
      },
      // 表单参数
      form: {
        id:null,
        erpSkuId:null
      },
      // 表单校验
      rules: {
        erpSkuId: [{ required: true, message: "请输入ERP系统商品SkuId", trigger: "change" }],
      }
    };
  },
  created() {
     listShop({}).then(response => {
        this.shopList = response.rows;
      });
    this.getList();
  },
  methods: {
    amountFormatter(row, column, cellValue, index) {
      return '￥' + cellValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    /** 查询店铺订单列表 */
    getList() {
      if(this.orderTime){
        this.queryParams.startTime = this.orderTime[0]
        this.queryParams.endTime = this.orderTime[1]
      }else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
      this.loading = true;
      listOrderItem(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /**  */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /**  */
    resetQuery() {
      this.resetForm("queryForm");
      this.orderTime=null
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    reset(){

    },
    cancel(){
      this.form.id = null
      this.form.erpSkuId = null
      this.open = false
    },
    // handleEditErpSku(row){
    //   this.form.id = row.id
    //   if(this.form.erpSkuId && this.form.erpSkuId > 0) {
    //     this.form.erpSkuId = row.erpSkuId
    //   }
    //   this.open = true
    // },
    // submitForm() {
    //   this.$refs["form"].validate(valid => {
    //     if (valid) {
    //       console.log('====修改参数====',this.form)
    //       updateErpSkuId(this.form).then(response => {
    //         this.$modal.msgSuccess("修改成功");
    //         this.open = false;
    //         this.getList();
    //       });
    //
    //     }
    //   });
    // }
  }
};
</script>
