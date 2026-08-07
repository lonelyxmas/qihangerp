<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">
      <el-form-item :label="$t('product.productName')" prop="name">
        <el-input
          v-model="queryParams.name"
          :placeholder="$t('product.productName')"
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

      <el-form-item :label="$t('product.category')" prop="categoryId">
        <!-- <el-input
          v-model="queryParams.categoryId"
          placeholder="请输入商品分类ID"
          clearable
          @keyup.enter.native="handleQuery"
        /> -->
        <treeselect :options="categoryTree" :placeholder="$t('product.category')" v-model="queryParams.categoryId" style="width: 230px;"/>
      </el-form-item>
      <el-form-item :label="$t('product.supplier')" prop="supplierId">
        <el-select v-model="queryParams.supplierId" filterable  :placeholder="$t('product.category')">
            <el-option v-for="item in supplierList" :key="item.id" :label="item.name" :value="item.id">
          </el-option>
        </el-select>
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
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['goods:goods:add']"
        >{{ $t('product.create') }}</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          @click="handleImport"-->
<!--          v-hasPermi="['goods:goods:edit']"-->
<!--        >推送到线下渠道店铺</el-button>-->
<!--      </el-col>-->
      <!--
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['goods:goods:remove']"
        >删除</el-button>
      </el-col> -->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleExport"-->
<!--          v-hasPermi="['goods:goods:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
       <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column :label="$t('product.productName')" align="left" prop="name" width="300" />
      <el-table-column :label="$t('product.productImage')" align="center" prop="image" width="80" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <image-preview :src="scope.row.image" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.productNumber')" align="center" prop="goodsNum" width="140" />
      <!-- <el-table-column label="单位名称" align="center" prop="unitName" /> -->
      <el-table-column :label="$t('product.category')" align="center" prop="categoryId" width="120">
        <template slot-scope="scope">
          <el-tag size="small">{{categoryList.find(x=>x.id === scope.row.categoryId)?categoryList.find(x=>x.id === scope.row.categoryId).name:''}}</el-tag>
        </template>
      </el-table-column>
      <!-- <el-table-column label="条码" align="center" prop="barCode" /> -->
      <el-table-column :label="$t('product.sku')" align="center" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleViewSkuList(scope.row)"
          >{{$t('product.sku')}}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.retailPrice')" align="center" prop="retailPrice" width="100" :formatter="amountFormatter"/>
      <el-table-column :label="$t('product.wholePrice')" align="center" prop="wholePrice" :formatter="amountFormatter"/>
      <!-- <el-table-column label="单位成本" align="center" prop="unitCost" /> -->
      <el-table-column :label="$t('product.supplier')" align="center" prop="supplierId" width="120">
        <template slot-scope="scope">
          <el-tag size="small">{{supplierList.find(x=>x.id == scope.row.supplierId)?supplierList.find(x=>x.id == scope.row.supplierId).name:''}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('product.publish')" align="center" prop="sheinCheckStatus" width="130">
        <template slot-scope="scope">
          <el-tag  v-for="item in scope.row.publishList" type="warning">{{item.shopPlatform}}</el-tag>

        </template>
      </el-table-column>
      <el-table-column :label="$t('product.status')" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.status === 1">{{$t('product.statusList.onSale')}}</el-tag>
          <el-tag size="small" v-if="scope.row.status === 2">{{$t('product.statusList.unSale')}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['goods:goods:edit']"
          >{{$t('list.operate.detail')}}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-upload2"
            @click="handleSync(scope.row)"
          >{{$t('product.operate.publish')}}</el-button>
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
    <!-- 同步到平台 -->
    <el-dialog :title="$t('product.operate.publish')" :visible.sync="syncOpen" width="400px" append-to-body>
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

    <el-dialog :title="title" :visible.sync="skuOpen" width="1000px" append-to-body>
      <el-table v-loading="loading" :data="skuList">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="Sku Id" align="center" prop="id" />
        <el-table-column label="商品名称" align="center" prop="goodsName" />
        <el-table-column label="SKU名称" align="center" prop="skuName" />
        <el-table-column label="SKU编码" align="center" prop="skuCode" />
        <el-table-column label="商品图片" align="center" prop="colorImage" width="100">
          <template slot-scope="scope">
            <image-preview :src="scope.row.colorImage" :width="50" :height="50"/>
          </template>
        </el-table-column>
        <el-table-column label="预计采购价" align="center" prop="purPrice" />
        <el-table-column label="状态" align="center" prop="status" >
          <template slot-scope="scope">
            <el-tag size="small" v-if="scope.row.status === 1">销售中</el-tag>
            <el-tag size="small" v-if="scope.row.status === 2">已下架</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <!-- 添加或修改商品管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="128px">
        <el-form-item :label="$t('product.productName')" prop="name" >
          <el-input v-model="form.name" disabled :placeholder="$t('product.productName')" />
        </el-form-item>
        <el-form-item :label="$t('product.productImage')" prop="image">
          <image-upload v-model="form.image" disabled="disabled"/>
        </el-form-item>
        <el-form-item :label="$t('product.productNumber')" prop="goodsNum">
          <el-input v-model="form.goodsNum" :placeholder="$t('product.productNumber')" disabled/>
        </el-form-item>
        <!--        <el-form-item label="单位名称" prop="unitName">-->
        <!--          <el-input v-model="form.unitName" placeholder="请输入单位名称" />-->
        <!--        </el-form-item>-->
        <el-form-item :label="$t('product.category')" prop="categoryId">
          <!--          <el-input v-model="form.categoryId" placeholder="请输入商品分类ID" />-->
          <treeselect :options="categoryTree"  disabled="disabled" :placeholder="$t('product.category')" v-model="form.categoryId" style="width: 230px;"/>
        </el-form-item>
        <!--        <el-form-item label="条码" prop="barCode">-->
        <!--          <el-input v-model="form.barCode" placeholder="请输入条码" />-->
        <!--        </el-form-item>-->
<!--        <el-form-item label="备注" prop="remark">-->
<!--          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />-->
<!--        </el-form-item>-->

<!--        <el-form-item label="预计采购价格" prop="purPrice">-->
<!--          <el-input v-model="form.purPrice" placeholder="请输入预计采购价格" />-->
<!--        </el-form-item>-->
        <el-form-item :label="$t('product.wholePrice')" prop="wholePrice">
          <el-input v-model="form.wholePrice" :placeholder="$t('product.wholePrice')" disabled/>
        </el-form-item>
        <el-form-item :label="$t('product.retailPrice')" prop="retailPrice">
          <el-input v-model="form.retailPrice" :placeholder="$t('product.retailPrice')" disabled/>
        </el-form-item>
        <!--        <el-form-item label="单位成本" prop="unitCost">-->
        <!--          <el-input v-model="form.unitCost" placeholder="请输入单位成本" />-->
        <!--        </el-form-item>-->
<!--        <el-form-item label="供应商" prop="supplierId">-->
<!--          &lt;!&ndash;          <el-input v-model="form.supplierId" placeholder="请输入供应商id" />&ndash;&gt;-->
<!--          <el-select v-model="form.supplierId" filterable  placeholder="请选择供应商名称">-->
<!--            <el-option v-for="item in supplierList" :key="item.id" :label="item.name" :value="item.id">-->
<!--            </el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
        <el-form-item :label="$t('product.status')" prop="status">
          <el-select v-model="form.status"  disabled :placeholder="$t('product.status')">
            <el-option :label="$t('product.statusList.onSale')" value="1"></el-option>
            <el-option :label="$t('product.statusList.unSale')" value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" v-if="false">
        <el-button type="primary" @click="submitForm">{{$t('list.submit')}}</el-button>
        <el-button @click="cancel">{{$t('list.cancel')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listGoods, getGoods, delGoods, addGoods, updateGoods } from "@/api/goods/goods";
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { listCategory } from "@/api/goods/category";
import {getToken} from "@/utils/auth";
import {listSupplier} from "@/api/goods/supplier";
import request from '@/utils/request'
import {listShop} from "@/api/shop/shop";

export default {
  name: "Goods",
  components: { Treeselect },
  data() {
    return {
      syncOpen:false,
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
      // 商品管理表格数据
      goodsList: [],
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      skuOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        image: null,
        goodsNum: null,
        unitName: null,
        categoryId: null,
        barCode: null,
        status: null,
        disable: null,
        supplierId: null,
        brandId: null,

      },
      // 表单参数
      form: {},
      categoryList: [],
      categoryTree: [],
      supplierList: [],
      skuList: [],
      // 表单校验
      rules: {
        shopId: [{ required: true, message: "不能为空", trigger: "blur" }],
        status: [{ required: true, message: "状态1销售中2已下架不能为空", trigger: "change" }],
        length: [{ required: true, message: "衣长/裙长/裤长不能为空", trigger: "blur" }],

        height: [
          { required: true, message: "高度/袖长不能为空", trigger: "blur" }
        ],
        width: [
          { required: true, message: "宽度/胸阔(围)不能为空", trigger: "blur" }
        ],
        width1: [
          { required: true, message: "肩阔不能为空", trigger: "blur" }
        ],
        width2: [
          { required: true, message: "腰阔不能为空", trigger: "blur" }
        ],
        width3: [
          { required: true, message: "臀阔不能为空", trigger: "blur" }
        ],
        weight: [
          { required: true, message: "重量不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    listCategory(this.queryParams).then(response => {
        this.categoryList = response.rows
        this.categoryTree = this.buildTree(response.rows,0)
        listSupplier({}).then(response => {
          this.supplierList = response.rows;
          // this.supplierLoading = false;
          listShop({}).then(response => {
            this.shopList = response.rows;
          });
          this.getList();
        });
        // this.getList();
      });


  },
  methods: {
    amountFormatter(row, column, cellValue, index) {
      return '￥' + parseFloat(cellValue).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.value
      };
    },
    buildTree(list, parentId) {
      let tree = [];
      for (let i = 0; i < list.length; i++) {
        if (list[i].parentId === parentId) {
          let node = {
            id: list[i].id,
            label: list[i].name,
            children: this.buildTree(list, list[i].id)
          };
          tree.push(node);
        }
      }
      return tree;
    },
    /** 查询商品管理列表 */
    getList() {
      this.loading = true;
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleAdd(){
      this.$router.push('/goods/create');
    },

    cancel() {
      this.open = false;
      this.skuOpen = false;
      this.syncOpen = false;
      this.skuList = []
      this.reset();
    },
    //
    reset() {
      this.form = {
        id: null,
        name: null,
        image: null,
        number: null,
        unitName: null,
        categoryId: null,
        barCode: null,
        remark: null,
        status: null,
        length: null,
        height: null,
        width: null,
        width1: null,
        width2: null,
        width3: null,
        weight: null,
        disable: null,
        period: null,
        purPrice: null,
        wholePrice: null,
        retailPrice: null,
        unitCost: null,
        supplierId: null,
        brandId: null,
        attr1: null,
        attr2: null,
        attr3: null,
        attr4: null,
        attr5: null,
        linkUrl: null,
        lowQty: null,
        highQty: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
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
    /** 查看SKU List*/
    handleViewSkuList(row){
      this.skuList = row.skuList
      this.skuOpen = true;

    },

    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getGoods(id).then(response => {
        this.form = response.data;
        this.form.disable = response.data.disable+''
        this.open = true;
        this.title = "Detail";
      });
    },
    syncForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$modal.confirm('是否确认同步该商品到平台？').then(() => {
            return request({
              url: '/api/oms-api/goods/sync',
              method: 'post',
              data: {
                goodsId: this.form.goodsId,
                shopId:this.form.shopId
              }
            });
          }).then(() => {
            this.$modal.msgSuccess("同步成功");
          }).catch(() => {});
        }
      });
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateGoods(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 同步到平台 */
    handleSync(row) {
      this.form.goodsId=row.id
      this.syncOpen = true
      // this.$modal.confirm('是否确认同步该商品到平台？').then(() => {
      //   return request({
      //     url: '/api/oms-api/goods/sync',
      //     method: 'post',
      //     data: {
      //       goodsId: row.id
      //     }
      //   });
      // }).then(() => {
      //   this.$modal.msgSuccess("同步成功");
      // }).catch(() => {});
    }
  }
};
</script>
