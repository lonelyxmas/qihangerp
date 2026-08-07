<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item :label="$t('product.id')" prop="goodsId">
        <el-input
          v-model="queryParams.goodsId"
          :placeholder="$t('product.id')"
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
      <el-form-item :label="$t('product.skuId')" prop="specId">
        <el-input
          v-model="queryParams.skuId"
          :placeholder="$t('product.skuId')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('product.skuCode')" prop="skuCode">
        <el-input
          v-model="queryParams.skuCode"
          :placeholder="$t('product.skuCode')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('list.search') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('list.reset') }}</el-button>
      </el-form-item>
    </el-form>

<!--    <el-row :gutter="10" class="mb8">-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleExport"-->
<!--          v-hasPermi="['api:goodsInventory:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
<!--      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
<!--    </el-row>-->

    <el-table v-loading="loading" :data="goodsInventoryList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      <el-table-column label="主键ID" align="center" prop="id" />-->
      <el-table-column :label="$t('product.id')" align="center" prop="goodsId" />
      <el-table-column :label="$t('product.productName')" align="left" prop="goodsName" width="300"/>
      <el-table-column :label="$t('product.productNumber')" align="center" prop="goodsNum" />
      <el-table-column :label="$t('product.color')" align="center" prop="colorValue" />
      <el-table-column :label="$t('product.size')" align="center" prop="sizeValue" />
      <el-table-column :label="$t('product.skuId')" align="center" prop="skuId" />
      <el-table-column :label="$t('product.skuCode')" align="center" prop="skuCode" />
      <el-table-column :label="$t('stock.inventory')" align="center" prop="quantity" />
<!--      <el-table-column :label="$t('stock.lockedQty')" align="center" prop="lockedQty" />-->
<!--      <el-table-column :label="$t('list.status')" align="center" prop="isDelete" >-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag size="small" v-if="scope.row.isDelete==0">生效中</el-tag>-->
<!--          <el-tag size="small" v-if="scope.row.isDelete==1">已删除</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column :label="$t('list.createTime')" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="$t('list.updateTime')" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >{{$t('list.operate.detail')}}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >{{$t('list.operate.edit')}}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleSync(scope.row)"
          >{{ $t('stock.pushStockToShop') }}</el-button>
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

    <!-- 详情 -->
    <el-dialog :title="$t('stock.inventoryDetail')" :visible.sync="open" width="930px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">

        <el-table :data="erpGoodsInventoryDetailList" :row-class-name="rowErpGoodsInventoryDetailIndex" ref="erpGoodsInventoryDetail">
          <el-table-column :label="$t('list.index')" align="center" prop="index" width="55"/>
          <el-table-column :label="$t('list.createTime')" prop="createTime" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('list.type')" prop="type" width="150">
            <template slot-scope="scope">
              <el-tag size="small" v-if="scope.row.type==2">减少库存</el-tag>
              <el-tag size="small" v-if="scope.row.type==1">增加库存</el-tag>
              <el-tag size="small" v-if="scope.row.type==3">锁定库存</el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('order.list.quantity')" prop="quantity" width="80">
          </el-table-column>
          <el-table-column :label="$t('stock.balanceInventory')" prop="balanceQuantity" width="100"></el-table-column>

          <el-table-column :label="$t('list.remark')" prop="remark" width="150">
          </el-table-column>
          <el-table-column :label="$t('list.createBy')" prop="createBy" width="150">
          </el-table-column>
        </el-table>
      </el-form>
    </el-dialog>
  <!-- 修改商品库存对话框 -->
    <el-dialog :title="title" :visible.sync="updateOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="128px">
        <el-form-item :label="$t('stock.operate.oldQuantity')" prop="oldQuantity">
          <el-input v-model="form.oldQuantity" disabled :placeholder="$t('stock.operate.oldQuantity')" />
        </el-form-item>
        <el-form-item label="库存操作类型" prop="type">
          <el-select v-model="form.type" >
            <el-option :label="$t('stock.operate.type.add')" value="1"></el-option>
            <el-option :label="$t('stock.operate.type.reduce')" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('stock.operate.quantity')" prop="quantity">
          <el-input type="number" v-model="form.quantity" :placeholder="$t('stock.operate.quantity')" />
        </el-form-item>
        <el-form-item :label="$t('list.remark')" prop="remark">
          <el-input type="textarea" v-model="form.remark"  :placeholder="$t('list.remark')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{$t('list.submit')}}</el-button>
        <el-button @click="cancel">{{$t('list.cancel')}}</el-button>
      </div>
    </el-dialog>
    <!--同步到店铺-->
    <el-dialog :title="$t('stock.pushStockToShop')" :visible.sync="syncOpen" width="400px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="$t('list.shop')" prop="shopId">
          <el-select v-model="form.shopId" filterable  :placeholder="$t('list.shop')">
            <el-option v-for="item in shopList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="syncForm">{{$t('list.submit')}}</el-button>
        <el-button @click="cancel">{{$t('list.cancel')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGoodsInventory, getGoodsInventory ,updateGoodsInventoryQuantity,syncGoodsInventoryToShop} from "@/api/goods/goodsInventory";
import {listShop} from "@/api/shop/shop";
import request from "@/utils/request";

export default {
  name: "GoodsInventory",
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
      //
      showSearch: true,
      // 总条数
      total: 0,
      // 商品库存表格数据
      goodsInventoryList: [],
      // 商品库存明细表格数据
      erpGoodsInventoryDetailList: [],
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      updateOpen: false,
      syncOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsId: null,
        goodsNumber: null,
        specId: null,
        specNumber: null
      },
      // 表单参数
      form: {
        id:null,
        shopId:null,
        oldQuantity:null,
        quantity:null,
        type:null,
        remark:null
      },
      // 表单校验
      rules: {
        type: [{ required: true, message: "不能为空", trigger: "blur" }],
        quantity: [{ required: true, message: "不能为空", trigger: "blur" }],
        shopId: [{ required: true, message: "不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询商品库存列表 */
    getList() {
      this.loading = true;
      listGoodsInventory(this.queryParams).then(response => {
        this.goodsInventoryList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    //
    reset() {
      this.form = {
        id: null,
        shopId: null,
        oldQuantity: null,
        quantity: null,
        type: null,
        remark: null,
      };
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
    handleDetail(row) {
      this.reset();
      const id = row.id || this.ids
      getGoodsInventory(id).then(response => {
        this.form = response.data;
        this.erpGoodsInventoryDetailList = response.data;
        this.open = true;
      });
    },
    handleUpdate(row) {
      this.reset();
      const id = row.id
      this.updateOpen=true
      this.form.id = row.id
      this.form.oldQuantity = row.quantity
      // getGoodsInventory(id).then(response => {
      //   this.form = response.data;
      //   this.erpGoodsInventoryDetailList = response.data;
      //   this.open = true;
      // });
    },
	/** 商品库存明细序号 */
    rowErpGoodsInventoryDetailIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    cancel() {
      this.open = false;
      this.updateOpen = false;
      this.syncOpen = false;
      this.reset();
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateGoodsInventoryQuantity(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.updateOpen = false;
            this.getList();
          });
        }
      })
    },
    /** 同步到平台 */
    handleSync(row) {
      listShop({}).then(response => {
        this.shopList = response.rows;
        this.form.id=row.id
        this.syncOpen = true
      });
    },
    syncForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$modal.confirm('是否确认同步该商品sku库存到平台？').then(() => {

          }).then(() => {
            syncGoodsInventoryToShop(this.form).then(resp=>{
              this.$modal.msgSuccess("同步成功");
            })

          }).catch(() => {});
        }
      });
    },
  }
};
</script>
