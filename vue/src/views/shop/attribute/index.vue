<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <attribute-shein v-if="item.id === 1500"></attribute-shein>
        <attribute-idosell v-if="item.id === 2000"></attribute-idosell>
      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>

import AttributeIdosell  from "@/views/idosell/attribute/index.vue";
import AttributeShein  from "@/views/shein/attribute/index.vue";
import {listPlatform} from "@/api/shop/shop";
export default {
  name: "ShopCategory",
  components:{AttributeShein,AttributeIdosell},
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
