<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item :label="$t('list.id')" prop="id">
        <el-input
          v-model="queryParams.id"
          :placeholder="$t('list.id')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('list.name')" prop="name">
        <el-input
          v-model="queryParams.name"
          :placeholder="$t('list.name')"
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

<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-refresh"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handlePushOms"-->
<!--        >{{$t('order.pushOrderOms')}}</el-button>-->
<!--      </el-col>-->

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column :label="$t('list.id')" align="left" prop="id" ></el-table-column>
      <el-table-column :label="$t('list.name')" align="left" prop="name" ></el-table-column>
      <el-table-column :label="$t('list.shop')" align="left" prop="id" >
        <template slot-scope="scope">
          <el-tag type="info">{{ shopList.find(x=>x.id === scope.row.shopId) ? shopList.find(x=>x.id === scope.row.shopId).name : '' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('list.parent')" align="left" prop="parentId"></el-table-column>
      <el-table-column :label="$t('list.productCount')" align="left" prop="productCount" ></el-table-column>
      <el-table-column :label="$t('shop.omsCategoryId')" align="left" prop="omsCategoryId" ></el-table-column>

      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            :loading="pullLoading"
            size="mini"
            icon="el-icon-link"
            @click="handleBindCategory(scope.row)"
          >{{$t('list.operate.bindCategory')}}</el-button>
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
    <el-dialog title="绑定OMS分类" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('product.category')" prop="categoryId">
          <treeselect :options="categoryList" :placeholder="$t('product.category')" v-model="form.categoryId" style="width:220px" />
        </el-form-item>
        <el-form-item label="分类Id" prop="categoryId">
          <el-input v-model="form.categoryId" placeholder="请输入分类Id" />
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
import {listShopCategory, pullShopCategory,bindOmsCategory} from "@/api/idosell/shop";
import { listShop } from "@/api/shop/shop";

import {MessageBox} from "element-ui";
import {isRelogin} from "../../../utils/request";
import Clipboard from "clipboard";
import {listCategory} from "@/api/goods/category";
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  name: "CategoryIdosell",
  components: {Treeselect},
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
      categoryList:[{
        id: '0',
        label: '-',
        children: [],
      }],
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
        id:null,
        categoryId:null
      },
      rules: {
        categoryId: [{ required: true, message: "不能为空", trigger: "blur" }],
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
      this.form = {
        id: null,
        categoryId: null
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
    handlePull() {
      if(this.queryParams.shopId){
        this.pullLoading = true
        pullShopCategory({shopId:this.queryParams.shopId}).then(response => {
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
    handleBindCategory(row) {
      this.reset();
      // if(!this.categoryList||this.categoryList.length===0){
        this.getCategoryList()
      // }
      this.form.id = row.id
      this.form.categoryId = row.omsCategoryId
      this.open = true
    },
    getCategoryList() {
      // this.loading = true;
      listCategory(this.queryParams).then(response => {
        this.categoryList = this.buildTree(response.rows,0)
        this.loading = false;
      });
    },
    buildTree(list, parentId) {
      let tree = [];
      for (let i = 0; i < list.length; i++) {
        if (list[i].parentId === parentId) {
          let node = {
            id: list[i].id,
            parentId:list[i].parentId,
            label: list[i].name,
            children: this.buildTree(list, list[i].id)
          };
          tree.push(node);
        }
      }
      return tree;
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          bindOmsCategory(this.form).then(response => {
            this.$modal.msgSuccess("绑定成功");
            this.open = false;
            this.getList();
          });
        }
      });
    }
  }
};
</script>
