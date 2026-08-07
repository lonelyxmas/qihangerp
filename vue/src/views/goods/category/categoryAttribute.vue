<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分类" prop="categoryName">
        <el-input disabled
          v-model="queryParams.categoryName"
          placeholder=""
          readonly
        />
      </el-form-item>
<!--      <el-form-item label="'属性名'" prop="title">-->
<!--        <el-input-->
<!--          v-model="queryParams.title"-->
<!--          placeholder="请输入'属性名'"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="固定值color颜色size尺码style款式" prop="code">-->
<!--        <el-input-->
<!--          v-model="queryParams.code"-->
<!--          placeholder="请输入固定值color颜色size尺码style款式"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item>-->
<!--        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ $t('list.search') }}</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ $t('list.reset') }}</el-button>-->
<!--      </el-form-item>-->
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['goods:categoryAttribute:add']"
        >{{$t('list.create')}}</el-button>
      </el-col>
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
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="属性ID" align="center" prop="attributeId" />
      <el-table-column label="属性名" align="left" prop="attributeName" />
      <el-table-column label="分类ID" align="left" prop="categoryId" />
      <el-table-column label="类型" align="center" prop="attributeType" >
        <template slot-scope="scope">
          <!-- 属性的类型 1-销售属性（在skc和sku维度传参），2-尺寸属性（在size维度传参），3-成分属性（在商品属性维度传参），4-普通属性（在商品属性维度传参）-->
          <el-tag v-if="scope.row.attributeType === 1" style="margin-bottom: 6px;">销售属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 2" style="margin-bottom: 6px;">尺寸属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 3" style="margin-bottom: 6px;">成分属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 4" style="margin-bottom: 6px;">普通属性</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="属性" align="center" prop="attributeLabel" >
        <template slot-scope="scope">
          <!-- 属性的类型 1-销售属性（在skc和sku维度传参），2-尺寸属性（在size维度传参），3-成分属性（在商品属性维度传参），4-普通属性（在商品属性维度传参）-->
          <el-tag v-if="scope.row.attributeLabel === 1" style="margin-bottom: 6px;">主销售属性</el-tag>
          <el-tag v-if="scope.row.attributeType === 1 && scope.row.attributeLabel === 0" style="margin-bottom: 6px;">次销售属性</el-tag>
          <el-tag v-if="scope.row.attributeType !== 1 && scope.row.attributeLabel === 0" style="margin-bottom: 6px;">一般属性</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否必填" align="center" prop="attributeStatus" >
        <template slot-scope="scope">
          <!-- 1:属性不填; 2:属性选填; 3:属性必填 -->
          <el-tag v-if="scope.row.attributeStatus === 1" style="margin-bottom: 6px;">属性不填</el-tag>
          <el-tag v-if="scope.row.attributeStatus === 2" style="margin-bottom: 6px;">属性选填</el-tag>
          <el-tag v-if="scope.row.attributeStatus === 3" style="margin-bottom: 6px;">属性必填</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="输入方式" align="center" prop="attributeMode" >
        <template slot-scope="scope">
          <!-- 属性录入方式;0: 手工填写参数；1:下拉列表选择(可多选);2:销售属性专属(只针对销售属性，下拉列表选择);3:下拉列表选择(单选)4:下拉列表+手工参数 -->
          <el-tag v-if="scope.row.attributeMode === 0" style="margin-bottom: 6px;">手工填写参数</el-tag>
          <el-tag v-if="scope.row.attributeMode === 1" style="margin-bottom: 6px;">下拉列表选择(可多选)</el-tag>
          <el-tag v-if="scope.row.attributeMode === 2" style="margin-bottom: 6px;">销售属性专属(下拉列表选择)</el-tag>
          <el-tag v-if="scope.row.attributeMode === 3" style="margin-bottom: 6px;">下拉列表选择(单选)</el-tag>
          <el-tag v-if="scope.row.attributeMode === 4" style="margin-bottom: 6px;">下拉列表+手工参数</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryId" >
        <template slot-scope="scope">
          {{queryParams.categoryName}}
        </template>
      </el-table-column>
      <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAttrValue(scope.row)"
            v-hasPermi="['goods:categoryAttribute:edit']"
          >属性值管理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['goods:categoryAttribute:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['goods:categoryAttribute:remove']"
          >删除</el-button>
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

    <!-- 添加或修改商品分类属性对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
<!--        <el-form-item label="分类" prop="categoryId">-->
<!--          <el-input v-model="form.categoryId" placeholder="请输入分类id" />-->
<!--        </el-form-item>-->
        <el-form-item label="属性名" prop="title">
          <el-input v-model="form.title" placeholder="请输入属性名" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option value="0" label="属性"></el-option>
            <el-option value="1" label="规格"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="属性值类型" prop="code">
          <el-select v-model="form.code" placeholder="属性值类型">
            <el-option value="color" label="颜色"></el-option>
            <el-option value="size" label="尺码"></el-option>
            <el-option value="style" label="款式"></el-option>
          </el-select>
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
import { listCategoryAttribute, getCategoryAttribute, delCategoryAttribute, addCategoryAttribute, updateCategoryAttribute } from "@/api/goods/categoryAttribute";

export default {
  name: "CategoryAttribute",
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
        categoryId: null,
        categoryName: null,
        type: null,
        title: null,
        code: null
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
    if(this.$route.query.categoryId){
      this.queryParams.categoryId = this.$route.query.categoryId
      this.queryParams.categoryName = this.$route.query.categoryName
    }
    this.getList();
  },
  methods: {
    /** 查询商品分类属性列表 */
    getList() {
      this.loading = true;
      listCategoryAttribute(this.queryParams).then(response => {
        this.categoryAttributeList = response.rows;
        // this.total = response.total;
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
      this.$router.push({path:'/goods/goods_category/attribute_value',query:{categoryAttributeId:row.attributeId,categoryAttributeTitle:row.title}});
    },

    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商品分类属性";
    },

    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCategoryAttribute(id).then(response => {
        this.form = response.data;
        this.form.type = response.data.type+''
        this.open = true;
        this.title = "修改商品分类属性";
      });
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCategoryAttribute(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            if(this.$route.query.categoryId) {
              this.form.categoryId = this.$route.query.categoryId
              addCategoryAttribute(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        }
      });
    },

    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除商品分类属性编号为"' + ids + '"的数据项？').then(function() {
        return delCategoryAttribute(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
