<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <category-shein v-if="item.id === 1500"></category-shein>
        <category-idosell v-if="item.id === 2000"></category-idosell>
      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>

import CategoryIdosell  from "@/views/idosell/category/index.vue";
import CategoryShein  from "@/views/shein/category/index.vue";
import {listPlatform} from "@/api/shop/shop";
export default {
  name: "ShopCategory",
  components:{CategoryShein,CategoryIdosell},
  data() {
    return {
      activeName: '',
      typeList: [],
    };
  },
  created() {

  },
  mounted() {
    listPlatform({status:0}).then(res => {
      this.typeList = res.rows;
      this.activeName = this.typeList[0].code
    })
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
