<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item :label="$t('order.orderNum')" prop="orderNum">
        <el-input
          v-model="queryParams.orderNum"
          :placeholder="$t('order.pleaseEnterOrderNum')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('order.shop')" prop="shopId">
        <el-select v-model="queryParams.shopId" :placeholder="$t('order.pleaseSelectShop')" clearable @change="handleQuery">
         <el-option
            v-for="item in shopList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('order.orderTime')" prop="orderTime">
        <el-date-picker clearable
                        v-model="orderTime" value-format="yyyy-MM-dd"
                        type="daterange"
                        range-separator="-"
                        :start-placeholder="$t('order.startDate')"
                        :end-placeholder="$t('order.endDate')">
        </el-date-picker>
      </el-form-item>

      <el-form-item :label="$t('order.orderStatus')" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" :placeholder="$t('order.pleaseSelectOrderStatus')" clearable @change="handleQuery">

          <el-option label="待支付" value="1" ></el-option>
          <el-option label="已支付" value="105"></el-option>
          <el-option label="备货中" value="2"> </el-option>
          <el-option label="部分发货" value="101"> </el-option>
          <el-option label="已发货" value="3"> </el-option>
          <el-option label="已取消" value="4"> </el-option>
          <el-option label="已完成" value="5"> </el-option>
        </el-select>
      </el-form-item>


      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('order.search') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('order.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">

      <el-col :span="1.5">
        <el-button
          :loading="pullLoading"
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handlePull"
        >{{$t('order.pullOrderList')}}</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          :disabled="multiple"
          @click="handlePushOms"
        >{{$t('order.pushOrderOms')}}</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('order.orderNum')" align="left" prop="orderNo" width="220px">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          > {{scope.row.orderNo}}</el-button>
          <i class="el-icon-copy-document tag-copy" :data-clipboard-text="scope.row.orderId" @click="copyActiveCode($event,scope.row.orderId)" ></i>
          <!--          <el-button type="text" :data-clipboard-text="scope.row.orderSn" size="mini" style="float: left"  @click="copyActiveCode($event,scope.row.orderSn)"  class="tag-copy">复制</el-button>-->
          <br/>
          <el-tag type="info">{{ shopList.find(x=>x.id === scope.row.shopId) ? shopList.find(x=>x.id === scope.row.shopId).name : '' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="商品明细" align="center" width="900px" >
        <template slot="header">
          <table>
            <th>
              <td width="50px">{{ $t('order.list.picture')  }}</td>
              <td width="250px" align="left">{{ $t('order.list.goodsTitle')  }}</td>
              <td width="150" align="left">{{ $t('order.list.skuName')  }}</td>
              <td width="200" align="left">{{ $t('order.list.skuCode')  }}</td>
              <td width="150" align="left">{{ $t('order.list.platformSkuId')  }}</td>
              <td width="50" align="left">{{ $t('order.list.quantity')  }}</td>
            </th>
          </table>
        </template>
        <template slot-scope="scope" >
          <el-table :data="scope.row.items" :show-header="false" :cell-style="{border:0 + 'px' }"  :row-style="{border:0 + 'px' }" >
            <el-table-column label="商品图片" width="50px">
              <template slot-scope="scope">
                <!--                <el-image  style="width: 40px; height: 40px;" :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>-->
                <image-preview :src="scope.row.imgPath" :width="40" :height="40"/>
              </template>
            </el-table-column>
            <el-table-column label="商品名" align="left" width="250px" prop="productName" />
            <el-table-column label="SKU名" align="left" prop="skuCode" width="150"  :show-overflow-tooltip="true"/>
            <el-table-column label="Sku编码" align="left" prop="skuCode" width="200"/>
            <el-table-column label="平台SkuId" align="left" prop="skuId" width="150"/>
            <el-table-column label="商品数量" align="center" prop="itemNum" width="50px">
              <template slot-scope="scope">
                <el-tag size="small" type="danger">{{scope.row.productQuantity}}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column :label="$t('order.list.payAmount')" align="center" prop="payAmount"  >
        <template slot-scope="scope">
          <span>{{ amountFormatter(null,null,scope.row.payAmount/100,0) }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('order.list.orderTime')" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
<!--      <el-table-column label="买家留言" align="center" prop="buyerWords" />-->
<!--      <el-table-column label="卖家备注" align="center" prop="sellerWords" />-->
      <el-table-column :label="$t('order.orderStatus')" align="center" prop="orderStatus" >
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.orderStatus === 1"> 待支付</el-tag>
          <el-tag size="small" v-if="scope.row.orderStatus === 105"> 已支付</el-tag>
          <el-tag size="small" v-if="scope.row.orderStatus === 2"> 备货中</el-tag>
          <el-tag size="small" v-if="scope.row.orderStatus === 101"> 部分发货</el-tag>
          <el-tag size="small" v-if="scope.row.orderStatus === 3"> 已发货</el-tag>
          <el-tag size="small" v-if="scope.row.orderStatus === 4"> 已取消</el-tag>
          <el-tag size="small" v-if="scope.row.orderStatus === 5"> 已完成</el-tag>


        </template>
      </el-table-column>
      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            :loading="pullLoading"
            size="mini"
            icon="el-icon-refresh"
            @click="handlePullUpdate(scope.row)"
          >{{$t('order.updateOrder')}}</el-button>
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
import {listOrder, pullOrder, getOrder, pushOms, pullOrderDetail} from "@/api/shein/order";
import { listShop } from "@/api/shop/shop";

import {MessageBox} from "element-ui";
import {isRelogin} from "../../../utils/request";
import Clipboard from "clipboard";

export default {
  name: "OrderIdosell",
  data() {
    return {
      // 遮罩层
      loading: true,
      //
      showSearch: true,
      pullLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      detailOpen: false,
      multiple: true,
      // 总条数
      total: 0,
      // 淘宝订单表格数据
      orderList: [],
      shopList:[],
      orderTime:null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopId: null,
        tid: null,
        startTime: null,
        endTime: null,
        status: null
      },
      // 表单参数
      form: {
      },
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
    // this.getList();
  },
  methods: {
    copyActiveCode(event,queryParams) {
      console.log(queryParams)
      const clipboard = new Clipboard(".tag-copy")
      clipboard.on('success', e => {
        this.$message({ type: 'success', message: '复制成功' })
        // 释放内存
        clipboard.destroy()
      })
      clipboard.on('error', e => {
        // 不支持复制
        this.$message({ type: 'waning', message: '该浏览器不支持自动复制' })
        // 释放内存
        clipboard.destroy()
      })
    },
    amountFormatter(row, column, cellValue, index) {
      return '￥' + cellValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    /** 查询淘宝订单列表 */
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

    cancel() {
      this.open = false;
      this.detailOpen = false;
      this.saleAfterOpen = false
      this.reset();
    },
    //
    reset() {
      this.form = {
        id: null,
        shopId: null
      };
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
      this.orderTime=null
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handlePullDetailByTid(){
      if(this.queryParams.shopId && this.queryParams.orderId) {
        this.pullLoading = true
        pullOrderDetail({shopId:this.queryParams.shopId,orderId:this.queryParams.orderId}).then(response => {
          console.log('拉订单接口返回=====',response)
          if(response.code==200){
            this.$modal.msgSuccess(JSON.stringify(response));
          }else {
            this.$modal.msgError(JSON.stringify(response));
          }

          this.pullLoading = false
        })
      }else{
        this.$modal.msgSuccess("请先输入订单号并且选择店铺");
      }
    },
    handlePull() {
      if(this.queryParams.shopId){
        this.pullLoading = true
        pullOrder({shopId:this.queryParams.shopId,updType:0}).then(response => {
          console.log('拉取dou订单接口返回=====',response)
          if(response.code === 1401) {
              MessageBox.confirm('Token已过期，需要重新授权！请前往店铺列表重新获取授权！', '系统提示', { confirmButtonText: '前往授权', cancelButtonText: '取消', type: 'warning' }).then(() => {
                this.$router.push({path:"/shop/shop_list",query:{type:3}})
                // isRelogin.show = false;
                // store.dispatch('LogOut').then(() => {
                // location.href = response.data.tokenRequestUrl+'?shopId='+this.queryParams.shopId
                // })
              }).catch(() => {
                isRelogin.show = false;
              });

            // return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
          }else{
            this.$modal.msgSuccess(JSON.stringify(response));
            this.getList()
          }
          this.pullLoading = false
        })
      }else{
        this.$modal.msgSuccess("请先选择店铺");
      }

      // this.$modal.msgSuccess("请先配置API");
    },
    handlePullUpdate(row) {
      // 接口拉取订单并更新
      this.pullLoading = true
      pullOrderDetail({shopId:row.shopId,orderId:row.orderId}).then(response => {
          console.log('拉取dou订单接口返回=====',response)
        this.$modal.msgSuccess(JSON.stringify(response));
        this.pullLoading = false
      })
    },
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        this.goodsList = response.data.taoOrderItemList;
        this.detailOpen = true;
        this.detailTitle = "订单详情";
      });
      this.isAudit = false
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {

        }
      });
    },
    handlePushOms(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否手动推送到系统？').then(function() {
        return pushOms({ids:ids});
      }).then(() => {
        // this.getList();
        this.$modal.msgSuccess("推送成功");
      }).catch(() => {});
    },
  }
};
</script>
