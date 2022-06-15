<template>
  <div>
    <el-container  style="width: 97%;height: 290px;margin: 10px auto;border-radius: 15px;background-color: #496C66">
      <div  style="width: 22%;margin: 20px ;">
        <img src="../assets/image/myimage/headPage.png" style="width: 80%;border-radius: 50%;margin: 5px 30px" alt="">
      </div>
      <div  style="width: 22%;margin: 20px 0px;">
        <div style="margin: 25px 0;font-size: 20px;color: #f0f0f0;font-weight: bold">
          <p style="margin: 15px 0">姓名:<span >{{name}}</span></p>
          <p style="margin: 15px 0">用户名:<span >{{username}}</span></p>
          <p style="margin: 15px 0">职能:<span >{{role}}</span></p>
          <p style="margin: 15px 0">性别:<span >{{sex}}</span></p>
          <p style="margin: 15px 0">电话:<span >{{phone}}</span></p>
          <el-button type="danger" @click="edit">修改资料</el-button>
        </div>

      </div>
    </el-container>

<!--    卡片-->
    <el-row :gutter="20" style="width: 98%;margin: 10px auto;border-radius: 20px;">
      <el-col :span="8">
        <el-card shadow="hover" class="el-card">
          <div style="font-size: 20px">
            <span class="iconfont">&#xe897;</span>
            订单数量
          </div>
          <div class="num">
            {{orderNum}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="el-card">
          <div style="font-size: 20px">
            <span class="iconfont">&#xe6a5;</span>
            员工数量
          </div>
          <div class="num">
            {{empNum}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="el-card" >
          <div style="font-size: 20px">
            <span class="iconfont">&#xe601;</span>
            菜品数量
          </div>
          <div class="num">
            {{foodNum}}
          </div>
        </el-card>
      </el-col>
    </el-row>


    <el-dialog v-model="dialogVisible"
               title="数据"
               width="30%"
    >
      <el-form :model="form" label-width="120px">

        <el-form-item style="text-align: center;margin: 10px auto" label-width="0">
            <img src="../assets/image/myimage/headPage.png" width="90" height="90" class="avatar " style="margin: 10px auto;border-radius: 10px">
        </el-form-item>


        <el-form-item label="ID">
          <el-input v-model="form[0].id" style="width:80%" disabled></el-input>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="form[0].name" style="width:80%"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form[0].username" style="width:80%"></el-input>
        </el-form-item>
        <el-form-item label="职能">
          <el-input v-model="form[0].role" style="width:80%"></el-input>
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="form[0].phone" style="width:80%"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="editConfirm"
            >确认</el-button
            >
          </span>
      </template>
    </el-dialog>


<!--排行-->
    <el-row :gutter="20" style="width: 98%;height: 290px;margin: 10px auto;border-radius: 20px;">
      <el-col :span="12">
        <el-card shadow="hover" class="el-card" >
          <p style="font-size: 20px;border-bottom: #496C66 5px solid">菜品排行</p>
          <el-table :data="tableData1" style="width: 100%">
            <el-table-column prop="name" label="菜品名称" width="230" />
            <el-table-column prop="price" label="菜品价格" width="180" />
            <el-table-column prop="sell" label="菜品销量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="el-card">
          <p style="font-size: 20px;border-bottom: #496C66 5px solid">套餐排行</p>
          <el-table :data="tableData2" style="width: 100%">
            <el-table-column prop="name" label="套餐名称" width="230" />
            <el-table-column prop="price" label="套餐价格" width="180" />
            <el-table-column prop="sell" label="套餐销量" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>



  </div>
</template>

<script>
import request from "../../utils/request";

export default {
  name: "ManagerPerson",
  data(){
    return{
      form:[
        //   {id:'',
        // name:'',
        // username:'',
        // role:'',
        // sex:'',
        // phone:'',
        // createTime:'',
        // createUser:'',
        // password:'',
        // status:'',
        // updateTime:'',
        // updateUser:''}
      ],
      name:'',
      username:'',
      role:'',
      sex:'',
      phone:'',

      orderNum:423,
      empNum:'',
      foodNum:'',

      dialogVisible:false,

      tableData1:[{
        name:'鱼香肉丝',
        price:'￥39.99',
        sell:195
      },{
        name:'麻婆豆腐',
        price:'￥19.99',
        sell:182
      },{
        name:'jio踩酸菜',
        price:'￥199.99',
        sell:169
      },
      ],

      tableData2:[{
        name:'鱼香肉丝',
        price:'￥39.99',
        sell:195
      },{
        name:'麻婆豆腐',
        price:'￥19.99',
        sell:182
      },{
        name:'jio踩酸菜',
        price:'￥199.99',
        sell:169
      },
      ]

    }
  },
  created() {
    this.loadPerson()
    this.loadOrderNum()
    this.loadFoodNum()
    this.loadEmpNum()
  },
  methods:{
    editConfirm(){
      request.put("/changAn/employee",this.form[0]).then(res => {
        if (res.code === 1) {
          this.$message.success("更新成功")
          this.loadPerson()
          this.dialogVisible = false
        } else {
          this.$message({
            type: "error",
            message: res.msg,
          })
        }
      })

    },
    edit(){
      this.dialogVisible = true
    },
    loadPerson(){
      let user = localStorage.getItem('userInfo') || '{}'
      if(user === '{}'){
        this.$message.error("还没有登录")
      }else {
        let name = JSON.parse(localStorage.getItem('userInfo')).name
        request.get('/changAn/employee/page',{
          params:{
            page:'1',
            pageSize:'10',
            search:name,
          }
        }).then(res=>{
          console.log(res);
          this.form = res.data.records
          this.name = res.data.records[0].name;
          this.username = res.data.records[0].username;
          this.role = res.data.records[0].role;
          this.sex = res.data.records[0].sex;
          this.phone = res.data.records[0].phone;

          // this.form.id = res.data.records[0].id;
          // this.form.name = res.data.records[0].name;
          // this.form.username = res.data.records[0].username;
          // this.form.role = res.data.records[0].role;
          // this.form.sex = res.data.records[0].sex;
          // this.form.phone = res.data.records[0].phone;
          // this.form.createTime = res.data.records[0].createTime;
          // this.form.createUser = res.data.records[0].createUser;
          // this.form.password = res.data.records[0].password;
          // this.form.status = res.data.records[0].status;
          // this.form.updateTime = res.data.records[0].updateTime;
          // this.form.updateUser = res.data.records[0].updateUser;

          // this.total=res.data.total;
        })
      }
    },
    loadOrderNum(){
      request.get('/changAn/order/page', {
        params: {
          page: "1",
          pageSize: "1000",
          search: "",
        }
      }).then(res => {
        console.log(res);
        this.orderNum = res.data.records.length;
      })
      },
    loadEmpNum(){
      request.get('/changAn/employee/page', {
        params: {
          page: "1",
          pageSize: "1000",
          search: "",
        }
      }).then(res => {
        console.log(res);
        this.empNum = res.data.records.length;
      })
    },
    loadFoodNum(){
      request.get('/changAn/dish/page', {
        params: {
          page: "1",
          pageSize: "1000",
          search: "",
        }
      }).then(res => {
        console.log(res);
        this.foodNum = res.data.records.length;
      })
    }
  }
}
</script>

<style scoped>
.el-card{
  border-radius: 10px;
  text-align: center;
}
.num{
  margin: 15px 0 0 0;
  font-size: 30px;
}
</style>