<template>
  <div class="app-container">


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          :loading="pullLoading"
          type="primary"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handlePullSizeAttrAll"
        >{{$t('shop.pullShopSizeAttr')}}</el-button>
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
          >{{$t('list.attributeValue')}}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-link"
            @click="handleBindAttribute(scope.row)"
            v-hasPermi="['goods:categoryAttribute:edit']"
          >{{$t('list.operate.bindAttribute')}}</el-button>
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-delete"-->
<!--            @click="handleDelete(scope.row)"-->
<!--            v-hasPermi="['goods:categoryAttribute:remove']"-->
<!--          >删除</el-button>-->
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
    <el-dialog title="属性值" :visible.sync="open" width="1100px" append-to-body>

          <el-table :data="form.valueList"  style="margin-bottom: 10px;">
            <!-- <el-table-column type="selection" width="50" align="center" /> -->
            <el-table-column label="序号" align="center" type="index" width="50"/>

            <el-table-column label="属性值ID" prop="attributeValueId" ></el-table-column>
            <el-table-column label="属性值" prop="attributeValue" ></el-table-column>
            <el-table-column label="属性ID" prop="attributeId" ></el-table-column>
            <el-table-column label="Oms属性值ID" prop="omsAttributeValueId" ></el-table-column>

            <el-table-column :label="$t('menu.operate')" align="center" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="success"
                  icon="el-icon-link"
                  @click="handleBindAttributeValue(scope.row)"
                >{{$t('list.operate.bindAttributeValue')}}</el-button>
              </template>
            </el-table-column>

          </el-table>
    </el-dialog>

    <el-dialog title="绑定OMS属性" :visible.sync="bindOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">

        <el-form-item label="OMS属性Id" prop="omsAttributeId" v-if="form.attributeId">
          <el-input v-model="form.omsAttributeId" placeholder="请输入OMS属性Id" />
        </el-form-item>
        <el-form-item label="OMS属性Id" prop="omsAttributeId" v-if="form.attributeId">
          <el-select v-model="form.omsAttributeId" filterable >
            <el-option
              v-for="item in omsAttributeList"
              :key="item.attributeId"
              :label="item.attributeName"
              :value="item.attributeId">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="OMS属性值Id" prop="omsAttributeValueId" v-if="form.attributeValueId">
          <el-input v-model="form.omsAttributeValueId" placeholder="请输入OMS属性值Id" />
        </el-form-item>
        <el-form-item label="OMS属性Id" prop="omsAttributeValueId" v-if="form.attributeValueId">
          <el-select v-model="form.omsAttributeValueId" filterable >
            <el-option
              v-for="item in omsAttributeValueList"
              :key="item.attributeValueId"
              :label="item.attributeValue"
              :value="item.attributeValueId">
            </el-option>
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
import {
  listIdosellAttribute,
  listIdosellAttributeValue,
  bindOmsAttribute,
  bindOmsAttributeValue
} from "@/api/idosell/attribute";
import {pullSizeAttrAll} from "@/api/idosell/shop";
import { listAttribute,listAttributeValue } from "@/api/goods/attribute";

export default {
  name: "AttributeIdosell",
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
      omsAttributeList: [],
      omsAttributeValueList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      pullLoading: false,
      bindOpen: false,
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
      form: {
        attributeId:null,
        omsAttributeId:null,
        attributeValueId:null,
        omsAttributeValueId:null,
      },
      // 表单校验
      rules: {
        omsAttributeId: [{ required: true, message: "不能为空", trigger: "blur" }],
        omsAttributeValueId: [{ required: true, message: "不能为空", trigger: "blur" }],

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
      listIdosellAttribute(this.queryParams).then(response => {
        this.categoryAttributeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    cancel() {
      this.open = false;
      this.bindOpen = false;
      this.reset();
    },
    //
    reset() {
      this.form =  {
        attributeId:null,
          omsAttributeId:null,
          attributeValueId:null,
          omsAttributeValueId:null,
      }
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
      listIdosellAttributeValue(row.attributeId).then(resp=>{
        this.open=true
        this.form.valueList =resp.rows;
        this.form.omsAttributeId = row.omsAttributeId
        this.open=true
      })

    },
    handleBindAttribute(row) {
      this.reset();
      listAttribute({attributeType:'1'}).then(response => {
        this.omsAttributeList = response.rows;
      });
      this.form.attributeId = row.attributeId
      this.form.omsAttributeId = row.omsAttributeId
      this.bindOpen = true
    },
    handleBindAttributeValue(row) {
      if(!this.form.omsAttributeId){
        this.$modal.msgError("请先绑定属性")
      }else {
        const omsAttributeId = this.form.omsAttributeId;
        listAttributeValue({attributeId:omsAttributeId,pageNum:1,pageSize:10000}).then(response => {
          this.omsAttributeValueList = response.rows;
          this.reset();
          this.form.attributeValueId = row.attributeValueId
          this.form.omsAttributeValueId = row.omsAttributeValueId
          this.bindOpen = true
        });

      }

    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if(this.form.attributeId){
            bindOmsAttribute(this.form).then(response => {
              this.$modal.msgSuccess("绑定成功");
              this.bindOpen = false;
              this.getList();
            });
          }else{
            bindOmsAttributeValue(this.form).then(response => {
              this.$modal.msgSuccess("绑定成功");
              this.bindOpen = false;
              this.open = false;
              this.getList();
            });
          }

        }
      });
    },
    handlePullSizeAttrAll() {
      this.$modal.confirm('确定拉取所有Size属性吗？').then(function() {

      }).then(() => {
        this.pullLoading = true
        return pullSizeAttrAll();
        // this.getList();
        this.$modal.msgSuccess("拉取成功");
      }).catch(() => {});
    },
  }
};
</script>
