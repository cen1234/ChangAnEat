<template>
  <div class="home" style="padding: 20px">

    <!--    搜索区域-->
    <div style="margin: 10px">
      <div class="demo-input-suffix">
        <el-row :gutter="20">
      <span class="ml-3 w-35 text-gray-600 inline-flex items-center " style="margin: 0 10px;padding: 10px"
      >
      </span
      >
          <!--       <el-input-->
          <!--           v-model="input1"-->
          <!--           class="w-50 m-2"-->
          <!--           placeholder="按日期搜索"-->
          <!--           :suffix-icon="Calendar"-->
          <!--           style="width: 20%;margin: 0 20px"-->
          <!--       />-->

          <span class="demonstration" style="width: 100%"></span>
<!--          <el-date-picker-->
<!--              v-model="value2"-->
<!--              type="date"-->
<!--              placeholder="Pick a day"-->
<!--              :disabled-date="disabledDate"-->
<!--              :shortcuts="shortcuts"-->
<!--              style="margin-left: 40px"-->
<!--          />-->
          <el-input v-model="search"  placeholder="请输入电话" clearable style="width: 15%;margin:0 5px" @blur="CheckUserPhone()"/>
<!--          <el-input-->
<!--              v-model="search"-->
<!--              class="w-50 m-2"-->
<!--              placeholder="按电话号搜索"-->
<!--              style="width: 20%;margin: 0 20px"-->
<!--          />-->
          <el-button type="info" plain @click="load()">搜索订单</el-button>
          <div style="margin: 0px 5px;float:left">
            <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="statusHandle(3)">批量派送</el-button>
          </div>
          <div style="margin: 0px 0">
            <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="statusHandle(4)">批量完成</el-button>
          </div>
        </el-row>
      </div>
    </div>

    <el-table
        ref="multipleTableRef"
        :data="tableOrderData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"/>
      <el-table-column property="number" label="订单号" width="120"/>
      <el-table-column property="consignee" label="收货人" width="100"/>
      <el-table-column prop="phone" label="联系电话" width="120"/>
      <el-table-column property="address" label="地址" width="250" show-overflow-tooltip/>
      <el-table-column prop="amount" label="付款金额" width="100"/>
      <el-table-column label="结账时间" prop="checkoutTime" width="200"/>
      <el-table-column label="支付方式" prop="payMethod" width="100"/>
      <el-table-column prop="status" label="状态" width="100"></el-table-column>
      <el-table-column label="查看" width="220">
        <template #default="scope">
          <el-button size="small" type="primary" color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="handleCheck(scope.row.number)"
          >查看</el-button>
        </template>
      </el-table-column>

    </el-table>
    <div style="margin: 10px 0">
      <el-pagination
          v-model:currentPage="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5,10]"
          :small="small"
          :disabled="disabled"
          :background="background"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>


    <el-dialog v-model="dialogVisible"
               title="订单详情"
               width="40%"
    >

      <el-table :data="tableOrderDetailData" stripe style="width: 100%">
        <el-table-column prop="name" label="名字"  />
        <el-table-column prop="dishFlavor" label="口味" />
        <el-table-column prop="number" label="数量" />
        <el-table-column prop="amount" label="金额" />
      </el-table>
      <div style="width: 80%;height: 100px;border-top: #496C66 5px ">

      </div>

    </el-dialog>

  </div>
</template>

<script>

import request from "../../utils/request";
import {ElMessage} from "element-plus";

export default {
  name: 'Order',
  components: {},
  data() {
    return {
      value2:'',
      search:'',
      currentPage:1,
      total:10,
      tableOrderData: [
        {
          number:'',
          consignee: '',
          phone: '',
          address: '',
          amount:0,
          checkoutTime: '',
          status: '',
          payMethod: 0,
          remark:''


        },

      ],
      tableOrderDetailData:[{
        name:'',
        amount:0,
        dishFlavor:'',
        number:0,
      }],
      pageSize:5,
      dialogVisible:false,
      checkList: [],
    }
  },
  created() {
    if(!localStorage.getItem('userInfo')){
      this.$router.push('/login');
    }
    // if(JSON.parse(localStorage.getItem('userInfo')).role=='店长'){
    //   this.isShow=true;
    // }
    this.load();
  },
  methods: {

    filterTag(value, row) {
      return row.tag === value
    },
    imp(value, row) {
      row.tag = value
    },
    disabledDate(time){
      return time.getTime() > Date.now()
    },
    handleSizeChange(pageSize){
      this.pageSize=pageSize;
      this.load();
    },
    handleCurrentChange(page){
      this.currentPage=page;
      this.load();
    },

    handleCheck(number){
      this.dialogVisible = true
      request.get('/changAn/order/detail', {
        params:{
          number:number
        }
    }).then(res => {
        console.log(res);
        this.tableOrderDetailData = res.data;
      })
    },

    load() {
      request.get('/changAn/order/page', {
        params: {
          page: this.currentPage,
          pageSize: this.pageSize,
          search: this.search,
        }
      }).then(res => {
        console.log(res);
        this.tableOrderData = res.data.records;
        for (let item of this.tableOrderData) {
          if (item.payMethod ==1) {
            item.payMethod = '微信';
          }
          if (item.payMethod ==2) {
            item.payMethod = '支付宝';
          }
          switch(item.status){
            case 2 : item.status='正在派送';
                   break;
            case 3 : item.status='已派送';
                   break;
            case 4 : item.status='已完成';
                   break;
          }
        }
        this.total = res.data.total;
      })
    },
    //状态更改
    statusHandle (row) {
      let params = {}
      if (typeof row === 'number' ) {
        if (this.checkList.length === 0) {
          this.$message.error('批量操作，请先勾选操作订单！')
          return false
        }
        params.id = this.checkList;
        params.status = row
      }
      this.$confirm('确认更改该订单状态?', '提示', {
        'confirmButtonText': '确定',
        'cancelButtonText': '取消',
        'type': 'warning'
      }).then(() => {
        request.put('/changAn/order/status/'+params.status,params.id).then(res => {
          if (res.code === 1) {
            this.$message.success('订单状态已经更改成功！')
            this.load();
          } else {
            this.$message.error(res.msg || '操作失败')
          }
        }).catch(err => {
          this.$message.error('请求出错了：' + err)
        })
      })
    },
    //多选框事件
    handleSelectionChange (val){
      let checkArr = []
      val.forEach((n) => {
        checkArr.push(n.id)
      })
      this.checkList = checkArr;
      console.log(this.checkList)
    },
    //正则验证电话
    CheckUserPhone(){
      if( this.search==undefined|| /^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$/.test(this.search)==false){
        ElMessage({
          message: '请输入正确的电话号码',
          type: 'warning',
        })
      }
    }
  }
}
</script>

<style scoped>
.demo-date-picker {
  display: flex;
  width: 100%;
  padding: 0;
  flex-wrap: wrap;
}
.demo-date-picker .block {
  padding: 30px 0;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
  flex: 1;
}
.demo-date-picker .block:last-child {
  border-right: none;
}
.demo-date-picker .demonstration {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
</style>