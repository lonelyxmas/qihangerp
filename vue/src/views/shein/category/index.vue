<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item :label="$t('list.id')" prop="categoryId">
        <el-input
          v-model="queryParams.categoryId"
          :placeholder="$t('list.id')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('list.productType')" prop="productTypeId">
        <el-input
            v-model="queryParams.productTypeId"
            :placeholder="$t('list.productType')"
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
        >{{$t('shop.pullShopCategory')}}</el-button>
      </el-col>
            <el-col :span="1.5">
              <el-button
                  :loading="pullLoading"
                type="primary"
                plain
                icon="el-icon-refresh"
                size="mini"
                @click="handlePullProductTypeAttrAll"
              >{{$t('shop.pullShopProductTypeAttr')}}</el-button>
            </el-col>
      <el-col :span="1.5">
        <el-button
            :loading="pullLoading"
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handlePushOms"
        >{{$t('shop.pushCategoryAndAttr')}}</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('list.id')" align="left" prop="categoryId" width="220px"></el-table-column>
      <el-table-column :label="$t('list.name')" align="left" prop="categoryName" width="220px"></el-table-column>
      <el-table-column :label="$t('list.shop')" align="left" prop="id" width="220px">
        <template slot-scope="scope">
          <el-tag type="info">{{ shopList.find(x=>x.id === scope.row.shopId) ? shopList.find(x=>x.id === scope.row.shopId).name : '' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('list.parent')" align="left" prop="parentCategoryId" width="220px"></el-table-column>
      <el-table-column :label="$t('list.productType')" align="left" prop="productTypeId" width="220px"></el-table-column>
      <el-table-column :label="$t('shop.omsCategoryId')" align="left" prop="omsCategoryId" ></el-table-column>
      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              type="text"
              size="mini"
              icon="el-icon-view"
              @click="handleViewAttr(scope.row)"
          >{{$t('shop.viewShopProductTypeAttr')}}</el-button>

          <el-button
            :loading="pullLoading"
            size="mini"
            icon="el-icon-refresh"
            @click="handlePullProductTypeAttr(scope.row)"
          >{{$t('shop.pullShopProductTypeAttr')}}</el-button>
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
    <el-dialog title="分类属性" :visible.sync="open" width="660px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="80px" inline>
            <el-descriptions title="属性信息">
              <el-descriptions-item label="ID">{{form.attributeId}}</el-descriptions-item>
              <el-descriptions-item label="名称">{{form.attributeName}}</el-descriptions-item>
              <el-descriptions-item label="类型">
                <!-- 1-销售属性（在skc和sku维度传参），2-尺寸属性（在size维度传参），3-成分属性（在商品属性维度传参），4-普通属性（在商品属性维度传参） -->
                <el-tag size="small" v-if="form.attributeType === 1">销售属性</el-tag>
                <el-tag size="small" v-if="form.attributeType === 2">尺寸属性</el-tag>
                <el-tag size="small" v-if="form.attributeType === 3">成分属性</el-tag>
                <el-tag size="small" v-if="form.attributeType === 4">普通属性</el-tag>
                <!-- 属性录入方式;0: 手工填写参数；1:下拉列表选择(可多选);2:销售属性专属(只针对销售属性，下拉列表选择);3:下拉列表选择(单选)4:下拉列表+手工参数 -->
              </el-descriptions-item>
              <el-descriptions-item label="录入方式">
                <el-tag size="small" v-if="form.attributeMode === 0">手工填写参数</el-tag>
                <el-tag size="small" v-if="form.attributeMode === 1">下拉列表选择(可多选)</el-tag>
                <el-tag size="small" v-if="form.attributeMode === 2">销售属性专属(只针对销售属性，下拉列表选择)</el-tag>
                <el-tag size="small" v-if="form.attributeMode === 3">下拉列表选择(单选)</el-tag>
                <el-tag size="small" v-if="form.attributeMode === 4">下拉列表+手工参数</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <el-table :data="form.vals"  style="margin-bottom: 10px;">
              <!-- <el-table-column type="selection" width="50" align="center" /> -->
              <el-table-column label="序号" align="center" type="index" width="50"/>
              <el-table-column label="属性值ID" prop="attributeValueId" ></el-table-column>
              <el-table-column label="属性值" prop="attributeValue" width="150"></el-table-column>

              <el-table-column label="状态" prop="isShow">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.isShow === 1">显示</el-tag>
                  <el-tag v-if="scope.row.isShow === 0">不显示</el-tag>

                </template>
              </el-table-column>
            </el-table>
          </el-form>
    </el-dialog>

  </div>
</template>

<script>
import {listShopCategory, pullShopCategory,pullProductTypeAttr,getProductTypeAttr,pullProductTypeAttrAll,pushProductTypeAttrOms} from "@/api/shein/shop";
import { listShop } from "@/api/shop/shop";

import {MessageBox} from "element-ui";
import {isRelogin} from "../../../utils/request";
import Clipboard from "clipboard";

export default {
  name: "CategoryShein",
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
      open: false,
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
        productTypeId: null,
        categoryId: null,
      },
      // 表单参数
      form: {
        attributeId:null
      },
      rules: {
      }
    };
  },
  created() {
    listShop({type: 2000}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });
    // this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      if(this.orderTime){
        this.queryParams.startTime = this.orderTime[0]
        this.queryParams.endTime = this.orderTime[1]
      }else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
      this.loading = true;
      listShopCategory(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    cancel() {
      this.open = false;
      this.reset();
    },
    //
    reset() {
      this.form = {};
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
    handlePull() {
      if(this.queryParams.shopId){
        this.pullLoading = true
        pullShopCategory({shopId:this.queryParams.shopId}).then(response => {
          console.log('拉取shein订单接口返回=====',response)
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
    handlePullProductTypeAttr(row) {
      // 接口拉取订单并更新
      this.pullLoading = true
      pullProductTypeAttr({shopId:row.shopId,productTypeId:row.productTypeId}).then(response => {
          console.log('拉取shein分类属性接口返回=====',response)
        this.$modal.msgSuccess(JSON.stringify(response));
        this.pullLoading = false
      })
    },
    handleViewAttr(row) {
      this.reset();
      getProductTypeAttr(row.productTypeId).then(response => {
        console.log("============",response)
        if(response.data){
          this.form = response.data;
        }

        this.open = true;

      });
    },
    handlePullProductTypeAttrAll() {
      this.$modal.confirm('确定拉取所有分类属性？').then(function() {

      }).then(() => {
        this.pullLoading = true
        return pullProductTypeAttrAll();
        // this.getList();
        this.$modal.msgSuccess("拉取成功");
      }).catch(() => {});
    },
    handlePushOms() {
      this.$modal.confirm('是否推送所有分类及属性到OMS？').then(function() {
        return pushProductTypeAttrOms();
      }).then(() => {
        // this.getList();
        this.$modal.msgSuccess("推送成功");
      }).catch(() => {});
    },
  }
};
</script>
