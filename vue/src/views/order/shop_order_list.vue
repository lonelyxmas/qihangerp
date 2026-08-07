<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item :label="$t('order.orderNum')" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          :placeholder="$t('order.pleaseEnterOrderNum')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('order.subOrderNum')" prop="subOrderNum">
        <el-input
          v-model="queryParams.subOrderNum"
          :placeholder="$t('order.pleaseEnterSubOrderNum')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('order.orderTime')" prop="orderTime">
        <el-date-picker clearable
                        v-model="orderTime" value-format="yyyy-MM-dd"
                        type="daterange"
                        range-separator="至"
                        :start-placeholder="$t('order.startDate')"
                        :end-placeholder="$t('order.endDate')">
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('order.shop')" prop="shopId">
        <el-select v-model="queryParams.shopId" :placeholder="$t('order.pleaseSelectShop')" clearable @change="handleQuery">
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('order.search') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('order.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="orderList">
      <el-table-column :label="$t('order.orderNum')" align="center" prop="orderNum" />
      <el-table-column :label="$t('order.subOrderNum')" align="center" prop="subOrderNum" />
      <el-table-column :label="$t('order.shop')" align="center" prop="shopName" />
      <el-table-column :label="$t('order.orderStatus')" align="center" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.orderStatus === 0">{{ $t('order.status.pending') }}</el-tag>
          <el-tag type="success" v-if="scope.row.orderStatus === 1">{{ $t('order.status.paid') }}</el-tag>
          <el-tag type="info" v-if="scope.row.orderStatus === 2">{{ $t('order.status.shipped') }}</el-tag>
          <el-tag type="warning" v-if="scope.row.orderStatus === 3">{{ $t('order.status.completed') }}</el-tag>
          <el-tag type="danger" v-if="scope.row.orderStatus === 4">{{ $t('order.status.closed') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('order.refundStatus')" align="center" prop="refundStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.refundStatus === 0">{{ $t('order.refund.none') }}</el-tag>
          <el-tag type="warning" v-if="scope.row.refundStatus === 1">{{ $t('order.refund.processing') }}</el-tag>
          <el-tag type="success" v-if="scope.row.refundStatus === 2">{{ $t('order.refund.completed') }}</el-tag>
          <el-tag type="danger" v-if="scope.row.refundStatus === 3">{{ $t('order.refund.failed') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('order.orderAmount')" align="center" prop="orderAmount" :formatter="amountFormatter" />
      <el-table-column :label="$t('order.paidAmount')" align="center" prop="paidAmount" :formatter="amountFormatter" />
      <el-table-column :label="$t('order.orderTime')" align="center" prop="orderTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderTime) }}</span>
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
import { listOrder } from "@/api/order/order";
import { listShop } from "@/api/shop/shop";
import { parseTime } from "@/utils/zhijian";

export default {
  name: "ShopOrderList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
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
      shopList:[],
      orderTime: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNum: null,
        subOrderNum: null,
        shopId: null,
        startTime: null,
        endTime: null,
        refundStatus: null,
        orderStatus: null
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
    parseTime,
    amountFormatter(row, column, cellValue, index) {
      return '￥' + parseFloat(cellValue).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
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
      listOrder(this.queryParams).then(response => {
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
    }
  }
};
</script>
