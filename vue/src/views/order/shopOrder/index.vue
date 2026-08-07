<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-for="item in typeList" :label="item.name" :name="item.code" lazy>
        <order-tao v-if="item.id === 100"></order-tao>
        <order-jd v-if="item.id === 200"></order-jd>
        <order-jd-vc v-if="item.id === 280"></order-jd-vc>
        <order-pdd v-if="item.id === 300"></order-pdd>
        <order-dou v-if="item.id === 400"></order-dou>
        <order-wei v-if="item.id === 500"></order-wei>
        <order-offline v-if="item.id === 999"></order-offline>
        <order-shein v-if="item.id === 1500"></order-shein>
        <order-idosell v-if="item.id === 2000"></order-idosell>

      </el-tab-pane>

    </el-tabs>

  </div>
</template>

<script>
import OrderTao  from "@/views/tao/order/index";
import OrderJd  from "@/views/jd/order/index";
import OrderJdVc  from "@/views/jd/order/index-vc.vue";
import OrderDou  from "@/views/dou/order/index";
import OrderPdd  from "@/views/pdd/order/index";
import OrderWei  from "@/views/wei/order/index";
import OrderOffline  from "@/views/offline/order/index";
import OrderIdosell  from "@/views/idosell/order/index.vue";
import OrderShein  from "@/views/shein/order/index.vue";
import {listPlatform} from "@/api/shop/shop";
export default {
  name: "Order",
  components:{OrderTao,OrderJd,OrderDou,OrderPdd,OrderJdVc,OrderWei,OrderOffline,OrderIdosell,OrderShein},
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
