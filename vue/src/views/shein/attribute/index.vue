<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="128px">

      <el-form-item :label="$t('shop.attributeType')" prop="productTypeId">
        <el-select v-model="queryParams.attributeType" :placeholder="$t('shop.attributeType')" clearable @change="handleQuery">
          <el-option label="销售属性" value="1" ></el-option>
          <el-option label="尺寸属性" value="2"></el-option>
          <el-option label="成分属性" value="3"></el-option>
          <el-option label="普通属性" value="4"></el-option>
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
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handlePullProductTypeAttrAll"
        >{{$t('shop.pullShopProductTypeAttr')}}</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-plus"-->
<!--          size="mini"-->
<!--          @click="handleAdd"-->
<!--          v-hasPermi="['goods:categoryAttribute:add']"-->
<!--        >新增</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['goods:categoryAttribute:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-delete"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--          v-hasPermi="['goods:categoryAttribute:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryAttributeList" @selection-change="handleSelectionChange">
<!--      <el-table-column type="selection" width="55" align="center" />-->
<!--      <el-table-column label="ID" align="center" prop="id" />-->
      <el-table-column :label="$t('shop.attributeId')" align="left" prop="attributeId" />
      <el-table-column :label="$t('shop.attributeName')" align="left" prop="attributeName" />
<!--      <el-table-column label="分类ID" align="left" prop="categoryId" />-->
      <el-table-column :label="$t('shop.attributeType')" align="center" prop="attributeType" >
        <template slot-scope="scope">
          <!-- 属性的类型 1-销售属性（在skc和sku维度传参），2-尺寸属性（在size维度传参），3-成分属性（在商品属性维度传参），4-普通属性（在商品属性维度传参）-->
          <el-tag v-if="scope.row.attributeType === 1" style="margin-bottom: 6px;">销售属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 2" style="margin-bottom: 6px;">尺寸属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 3" style="margin-bottom: 6px;">成分属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 4" style="margin-bottom: 6px;">普通属性</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('list.attribute')" align="center" prop="attributeLabel" >
        <template slot-scope="scope">
          <!-- 属性的类型 1-销售属性（在skc和sku维度传参），2-尺寸属性（在size维度传参），3-成分属性（在商品属性维度传参），4-普通属性（在商品属性维度传参）-->
          <el-tag v-if="scope.row.attributeLabel === 1" style="margin-bottom: 6px;">主销售属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 1 && scope.row.attributeLabel === 0" style="margin-bottom: 6px;">次销售属性</el-tag>
          <el-tag v-if="scope.row.attributeType !== 1 && scope.row.attributeLabel === 0" style="margin-bottom: 6px;">一般属性</el-tag>
        </template>
      </el-table-column>
      <el-table-column  :label="$t('list.must')"  align="center" prop="attributeStatus" >
        <template slot-scope="scope">
          <!-- 1:属性不填; 2:属性选填; 3:属性必填 -->
          <el-tag v-if="scope.row.attributeStatus === 1" style="margin-bottom: 6px;">属性不填</el-tag>
          <el-tag v-if="scope.row.attributeStatus === 2" style="margin-bottom: 6px;">属性选填</el-tag>
          <el-tag v-if="scope.row.attributeStatus === 3" style="margin-bottom: 6px;">属性必填</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('list.inputType')" align="center" prop="attributeMode" >
        <template slot-scope="scope">
          <!-- 属性录入方式;0: 手工填写参数；1:下拉列表选择(可多选);2:销售属性专属(只针对销售属性，下拉列表选择);3:下拉列表选择(单选)4:下拉列表+手工参数 -->
          <el-tag v-if="scope.row.attributeMode === 0" style="margin-bottom: 6px;">手工填写参数</el-tag>
          <el-tag v-if="scope.row.attributeMode === 1" style="margin-bottom: 6px;">下拉列表选择(可多选)</el-tag>
          <el-tag v-if="scope.row.attributeMode === 2" style="margin-bottom: 6px;">销售属性专属(下拉列表选择)</el-tag>
          <el-tag v-if="scope.row.attributeMode === 3" style="margin-bottom: 6px;">下拉列表选择(单选)</el-tag>
          <el-tag v-if="scope.row.attributeMode === 4" style="margin-bottom: 6px;">下拉列表+手工参数</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('shop.omsAttributeId')" prop="omsAttributeId" ></el-table-column>
      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAttrValue(scope.row)"
            v-hasPermi="['goods:categoryAttribute:edit']"
          >{{$t('list.attributeValue')}}</el-button>

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

    <!-- 属性值对话框 -->
    <el-dialog title="属性值" :visible.sync="open" width="650px" append-to-body>

      <el-table :data="form.valueList"  style="margin-bottom: 10px;">
        <!-- <el-table-column type="selection" width="50" align="center" /> -->
        <el-table-column label="序号" align="center" type="index" width="50"/>

        <el-table-column label="属性ID" prop="attributeId" ></el-table-column>
        <el-table-column label="属性值ID" prop="attributeValueId" ></el-table-column>
        <el-table-column label="属性值" prop="attributeValue" ></el-table-column>
        <el-table-column label="Oms属性值ID" prop="omsAttributeValueId" ></el-table-column>
        <!--            <el-table-column label="退款状态" prop="refundStatus">-->
        <!--              <template slot-scope="scope">-->
        <!--                &lt;!&ndash; 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功 &ndash;&gt;-->
        <!--                <el-tag v-if="scope.row.refundStatus === 1">无售后或售后关闭</el-tag>-->
        <!--                <el-tag v-if="scope.row.refundStatus === 2">售后处理中</el-tag>-->
        <!--                <el-tag v-if="scope.row.refundStatus === 3">退款中</el-tag>-->
        <!--                <el-tag v-if="scope.row.refundStatus === 4">退款成功</el-tag>-->
        <!--              </template>-->
        <!--            </el-table-column>-->
      </el-table>
    </el-dialog>  </div>
</template>

<script>
import { listAttribute ,listAttributeValue} from "@/api/shein/attribute";
import {pullProductTypeAttrAll} from "@/api/shein/shop";

export default {
  name: "AttributeShein",
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
      pullLoading: false,
      // 总条数
      total: 0,
      // 商品分类属性表格数据
      categoryAttributeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        attributeType: '1',

      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        categoryId: [
          { required: true, message: "分类id不能为空", trigger: "blur" }
        ],
        title: [{ required: true, message: "不能为空", trigger: "change" }],
        type: [
          { required: true, message: "类型：0属性1规格不能为空", trigger: "change" }
        ],
        code: [{ required: true, message: "不能为空", trigger: "change" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询商品分类属性列表 */
    getList() {
      this.loading = true;
      listAttribute(this.queryParams).then(response => {
        this.categoryAttributeList = response.rows;
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
        categoryId: null,
        type: null,
        title: null,
        code: null
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
    handleAttrValue(row){
      listAttributeValue(row.attributeId).then(resp=>{
        this.open=true
        this.form.valueList =resp.rows;
        this.open=true
      })

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
  }
};
</script>
