<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd(null)"
          v-hasPermi="['goods:category:add']"
        >{{$t('list.create')}}</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryList" row-key="id" :tree-props="{children: 'children'}"  >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
       <el-table-column label="ID" align="center" prop="id" />
      <el-table-column :label="$t('list.name')"  prop="name" />
      <el-table-column :label="$t('list.code')" align="center" prop="number" />
      <el-table-column :label="$t('list.productType')" align="center" prop="productTypeId" />

      <el-table-column :label="$t('list.remark')" align="center" prop="remark" />
      <el-table-column :label="$t('list.sort')" align="center" prop="sort" />
<!--      <el-table-column label="图片" align="center" prop="image" width="100">-->
<!--        <template slot-scope="scope">-->
<!--          <image-preview :src="scope.row.image" :width="50" :height="50"/>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column :label="$t('list.isDelete')" align="center" prop="isDelete" >
        <template slot-scope="scope">
          <el-tag size="small" v-if="scope.row.isDelete === 0">N</el-tag>
          <el-tag size="small" v-if="scope.row.isDelete === 1">Y</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('list.platform')" align="center" prop="isDelete" >
        <template slot-scope="scope">
          <el-tag size="small" v-for="item in getRelation(scope.row.relationsJson)">
            <el-col v-if=" item.shop_platform_id ===1500">SHEIN</el-col>
            <el-col v-if=" item.shop_platform_id ===2000">IdoSell</el-col>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('menu.operate')" align="left" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            icon="el-icon-view"
            size="mini"
            v-if="scope.row.productTypeId>0"
            @click="handleCategory(scope.row)"
          >{{$t('list.operate.attribute')}}</el-button>
          <el-button
          type="text"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd(scope.row)"
          v-hasPermi="['goods:category:add']"
        >{{$t('list.create')}}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['goods:category:edit']"
          >{{$t('list.operate.edit')}}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['goods:category:remove']"
          >{{$t('list.operate.delete')}}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    /> -->

    <!-- 添加或修改商品分类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级分类" prop="parentId">
          <el-input v-model="form.parent" placeholder="" disabled />
          <!-- <el-select v-model="form.parentId" placeholder="上级分类">
            <el-option
              v-for="item in shopList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select> -->
        </el-form-item>
        <el-form-item label="分类编码" prop="number">
          <el-input v-model="form.number" placeholder="请输入分类编码" />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="排序值" prop="sort">
          <el-input v-model.number="form.sort" placeholder="请输入排序值" />
        </el-form-item>
<!--        <el-form-item label="图片" prop="image">-->
<!--          <image-upload v-model="form.image"/>-->
<!--        </el-form-item>-->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{$t('list.submit')}}</el-button>
        <el-button @click="cancel">{{$t('list.cancel')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCategory, getCategory, delCategory, addCategory, updateCategory } from "@/api/goods/category";

export default {
  name: "Category",
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
      // 商品分类表格数据
      categoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        number: null,
        name: null,
        parentId: null,
        path: null,
        sort: null,
        image: null,
        isDelete: null,
      },
      // 表单参数
      form: {
        parent:null
      },
      // 表单校验
      rules: {
        number: [{ required: true, message: "不能为空", trigger: "blur" }],
        name: [{ required: true, message: "不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getRelation(jsonString){
      if(jsonString) {

        let parsedData = JSON.parse(jsonString);
        if (parsedData) {
          return parsedData
          // let resultStrings = [];
          // 循环遍历 parsedData 数组
          // parsedData.forEach(item => {
            // if(item.shop_platform_id === 1500){
            //   resultStrings.push('SHEIN')
            // }else if(item.shop_platform_id === 2000){
            //   resultStrings.push('IDOSELL')
            // }
            // resultStrings.push(`shop_category_id: ${item.shop_category_id}, shop_platform_id: ${item.shop_platform_id}`);
          // });
          // return resultStrings.join('; ')
        }
      }
    },
    buildTree(list, parentId) {
      let tree = [];
      for (let i = 0; i < list.length; i++) {
        // console.log("=======111111==",list[i])
        if (list[i].parentId === parentId) {
          // console.log("=======22222==",parentId)
          let node = {
            id: list[i].id,
            name: list[i].name,
            image:list[i].image,
            number:list[i].number,
            sort:list[i].sort,
            remark:list[i].remark,
            parentId:list[i].parentId,
            isDelete:list[i].isDelete,
            relationsJson:list[i].relationsJson,
            productTypeId:list[i].productTypeId,
            children: this.buildTree(list, list[i].id)
          };
          tree.push(node);
        }
      }
      return tree;
    },

    /** 查询商品分类列表 */
    getList() {
      this.loading = true;
      listCategory().then(response => {
        this.categoryList = this.buildTree(response.rows,0)
        console.log("构建后的list",this.categoryList)
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
        number: null,
        name: null,
        remark: null,
        parentId: null,
        path: null,
        sort: null,
        image: null,
        isDelete: null,
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
    handleCategory(row){
      this.$router.push({path:'/goods/goods_category/attribute',query:{categoryId:row.id,categoryName:row.name}});
    },

    handleAdd(row) {
      this.reset();
      if(row){
        this.form.parent = row.name
        this.form.parentId = row.id
      }else{
        this.form.parent = '一级分类'
        this.form.parentId = 0
      }
      this.open = true;
      this.title = "添加商品分类";
    },

    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCategory(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商品分类";
      });
    },
    /** 提交*/
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCategory(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCategory(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除商品分类编号为"' + ids + '"的数据项？').then(function() {
        return delCategory(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出 */
    handleExport() {
      this.download('goods/category/export', {
        ...this.queryParams
      }, `category_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
