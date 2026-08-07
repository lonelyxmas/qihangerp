<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane v-for="item in typeList" :label="item.name"  :name="item.code" lazy>
      <goods-tao v-if="item.id === 100"></goods-tao>
      <goods-jd v-if="item.id === 200"></goods-jd>
      <goods-jdvc v-if="item.id === 280"></goods-jdvc>
      <goods-pdd v-if="item.id === 300"></goods-pdd>
      <goods-dou v-if="item.id === 400"></goods-dou>
      <goods-wei v-if="item.id === 500"></goods-wei>
      <goods-shein v-if="item.id === 1500"></goods-shein>
      <goods-idosell v-if="item.id === 2000"></goods-idosell>
      <goods-offline v-if="item.id === 999"></goods-offline>
    </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import GoodsTao from "@/views/tao/goods/index.vue";
import GoodsJd from "@/views/jd/goods/index.vue";
import GoodsJdvc from "@/views/jd/goods/index-vc.vue";
import GoodsPdd from "@/views/pdd/goods/index.vue";
import GoodsDou from "@/views/dou/goods/index.vue";
import GoodsWei from "@/views/wei/goods/index.vue";
import GoodsShein from "@/views/shein/goods/index.vue";
import GoodsIdosell from "@/views/idosell/goods/index.vue";
import GoodsOffline from "@/views/offline/goods/index.vue";
import {listPlatform} from "@/api/shop/shop";
export default {
  name: "ShopGoods",
  components: {
    GoodsTao,
    GoodsJd,
    GoodsJdvc,
    GoodsPdd,
    GoodsDou,
    GoodsWei,
    GoodsShein,
    GoodsOffline,
    GoodsIdosell
  },
  data() {
    return {
      activeName: "tao",
      typeList: []
    };
  },
  created() {
    listPlatform({status:0}).then(res => {
      this.typeList = res.rows;
      this.activeName = this.typeList[0].code
    })
    // this.typeList = [
    //   { id: 100, name: "淘宝天猫", code: "tao" },
    //   { id: 200, name: "京东POP", code: "jd" },
    //   { id: 280, name: "京东自营", code: "jdvc" },
    //   { id: 300, name: "拼多多", code: "pdd" },
    //   { id: 400, name: "抖店", code: "dou" },
    //   { id: 500, name: "视频号小店", code: "wei" },
    //   { id: 1500, name: "SHEIN", code: "shein" },
    //   { id: 999, name: "手动下单", code: "offline" }
    // ];
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    }
  }
};
</script>
