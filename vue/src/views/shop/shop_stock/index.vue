<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <stock-shein v-if="item.id === 1500"></stock-shein>
        <stock-idosell v-if="item.id === 2000"></stock-idosell>

      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>

import StockIdosell  from "@/views/idosell/stock/index.vue";
import StockShein  from "@/views/shein/stock/index.vue";
import {listPlatform} from "@/api/shop/shop";
export default {
  name: "ShopStock",
  components:{StockIdosell,StockShein},
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
