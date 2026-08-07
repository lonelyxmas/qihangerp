<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">

      <el-form-item :label="$t('product.skuId')" prop="id">
        <el-input
          v-model="queryParams.id"
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
      <el-form-item :label="$t('product.productNumber')" prop="goodsNum">
        <el-input
          v-model="queryParams.goodsNum"
          :placeholder="$t('product.productNumber')"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item :label="$t('product.status')" prop="disable">
        <el-select v-model="queryParams.status" filterable  :placeholder="$t('product.status')">
          <el-option :label="$t('product.statusList.onSale')" value="1"></el-option>
          <el-option :label="$t('product.statusList.unSale')" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('list.search') }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('list.reset') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">

      <el-col :span="1.5">
<!--        <el-col :span="1.5">-->
<!--          <el-button-->
<!--            type="primary"-->
<!--            plain-->
<!--            icon="el-icon-plus"-->
<!--            size="mini"-->
<!--            @click="handleAdd"-->
<!--            v-hasPermi="['goods:goods:add']"-->
<!--          >添加商品SKU</el-button>-->
<!--        </el-col>-->
<!--        <el-col :span="1.5">-->
<!--          <el-button-->
<!--            type="success"-->
<!--            plain-->
<!--            icon="el-icon-edit"-->
<!--            size="mini"-->
<!--            @click="handleImport"-->
<!--            v-hasPermi="['goods:goods:edit']"-->
<!--          >导入商品SKU</el-button>-->
<!--        </el-col>-->
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsSpecList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
      <el-table-column :label="$t('product.skuId')" align="center" prop="id" />
      <el-table-column :label="$t('product.productName')" align="left" prop="goodsName" width="300" />
      <el-table-column :label="$t('product.productImage')" align="center" prop="image" width="80" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <image-preview :src="scope.row.colorImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.productNumber')" align="left" prop="goodsNum" />
      <el-table-column :label="$t('product.skuName')" align="center" prop="skuName" />
      <el-table-column :label="$t('product.skuCode')" align="center" prop="skuCode" />
      <el-table-column :label="$t('product.color')" align="center" prop="colorValue" />
      <el-table-column :label="$t('product.size')" align="center" prop="sizeValue" />
<!--      <el-table-column label="规格3" align="center" prop="styleValue" />-->
      <el-table-column :label="$t('product.retailPrice')" align="center" prop="retailPrice" c />
<!--      <el-table-column label="零售价" align="center" prop="retailPrice" :formatter="amountFormatter"/>-->
      <el-table-column :label="$t('product.wholePrice')" align="center" prop="wholePrice" :formatter="amountFormatter"/>
      <el-table-column :label="$t('product.status')" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.status === 1">{{$t('product.statusList.onSale')}}</el-tag>
          <el-tag size="small" v-if="scope.row.status === 2">{{$t('product.statusList.unSale')}}</el-tag>
        </template>
      </el-table-column>
<!--      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleUpdate(scope.row)"-->
<!--            v-hasPermi="['api:goodsSpec:edit']"-->
<!--          >{{$t('list.operate.edit')}}</el-button>-->

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

    <!-- 添加或修改商品规格库存管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
<!--        <el-form-item label="商品名" prop="goodsName">-->
<!--          <el-input v-model="form.goodsName" placeholder="请输入商品名" />-->
<!--        </el-form-item>-->
        <el-form-item label="SKU名" prop="skuName">
          <el-input v-model="form.skuName" placeholder="请输入SKU名" />
        </el-form-item>
        <el-form-item label="SKU编码" prop="skuCode">
          <el-input v-model="form.skuCode" placeholder="请输入SKU编码" />
        </el-form-item>

        <el-form-item label="图片URL" prop="colorImage">
<!--          <image-upload v-model="form.colorImage" :limit="1" />-->
          <el-input v-model="form.colorImage" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="售价" prop="retailPrice">
          <el-input type="number" v-model.number="form.retailPrice" placeholder="售价" />
        </el-form-item>

<!--        <el-form-item label="规格1" prop="colorValue">-->
<!--          <el-input v-model="form.colorValue" placeholder="请输入规格1" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="规格2" prop="sizeValue">-->
<!--          <el-input v-model="form.sizeValue" placeholder="请输入规格2" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="规格3" prop="styleValue">-->
<!--          <el-input v-model="form.styleValue" placeholder="请输入规格3" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="ERP商品ID" prop="outerErpGoodsId">-->
<!--          <el-input type="number" v-model.number="form.outerErpGoodsId" placeholder="请输入ERP商品ID" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="ERP商品SkuID" prop="outerErpSkuId">-->
<!--          <el-input type="number" v-model.number="form.outerErpSkuId" placeholder="请输入ERP商品SkuID" />-->
<!--        </el-form-item>-->
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" filterable  placeholder="状态">
            <el-option label="销售中" :value="1"></el-option>
            <el-option label="已下架" :value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{$t('list.submit')}}</el-button>
        <el-button @click="cancel">{{$t('list.cancel')}}</el-button>
      </div>
    </el-dialog>
    <!-- 导入ERP商品sku -->
    <el-dialog title="导入商品SKU" :visible.sync="importOpen" width="400px" append-to-body>
      <el-upload
        class="upload-demo"
        :headers="headers"
        drag
        action="/dev-api/api/oms-api/goods/goods_sku_import"
        accept="xlsx"
        multiple >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import {listGoodsSpec, getGoodsSpec, updateGoodsSpec, addGoodsSpec} from "@/api/goods/goodsSpec";
import {getToken} from "@/utils/auth";

export default {
  name: "GoodsSpec",
  data() {
    return {
      importOpen:false,
      headers: { 'Authorization': 'Bearer ' + getToken() },
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
      // 商品规格库存管理表格数据
      goodsSpecList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        erpGoodsId: null,
        erpSkuId: null,
        skuName: null,
        skuNum: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        goodsName: [{ required: true, message: "不能为空", trigger: "blur" }],
        skuName: [{ required: true, message: "不能为空", trigger: "blur" }],
        skuCode: [{ required: true, message: "SKU不能为空", trigger: "blur" }],
        retailPrice: [{ required: true, message: "不能为空", trigger: "blur" }],

      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    amountFormatter(row, column, cellValue, index) {
      return '￥' + parseFloat(cellValue).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    /** 查询商品规格库存管理列表 */
    getList() {
      this.loading = true;
      listGoodsSpec(this.queryParams).then(response => {
        this.goodsSpecList = response.rows;
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
        goodsId: null,
        skuName: null,
        skuNum: null,
        colorValue: null,
        colorImage: null,
        sizeValue: null,
        styleValue: null,
        erpGoodsId: null,
        erpSkuId: null
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
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },


    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getGoodsSpec(id).then(response => {
        this.form = response.data;
        this.form.disable = response.data.disable+''
        this.open = true;
        this.title = "修改商品规格库存管理";
      });
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateGoodsSpec(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          }else{
            addGoodsSpec(this.form).then(response => {
              this.$modal.msgSuccess("添加成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleAdd(){
      this.open = true
    },
    handleImport(){
      this.importOpen = true
    }
  }
};
</script>
