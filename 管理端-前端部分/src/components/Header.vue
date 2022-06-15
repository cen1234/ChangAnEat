<template>
    <div style="height: 45px;line-height: 45px; border-bottom: 1px solid #ccc; display: flex;">
        <div style="width: 200px;padding-left: 30px;font-weight: bold;background-color:#496C66;color: #FFFFFF">后台管理</div>
        <div style="flex:1;background-color:#496C66;"></div>
        <div style="width: 140px;background-color:#496C66;" >
            <el-dropdown style="padding-top: 17px;padding-right: 40px">
            <span class="el-dropdown-link" style="color: #FFFFFF">
                {{userName}}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item style="--el-dropdown-menuItem-hover-color: #496C66;" @click="$router.push('/managerperson')">个人信息</el-dropdown-item>
                        <el-dropdown-item style="--el-dropdown-menuItem-hover-color:#496C66 ">更换主题</el-dropdown-item>
                        <el-dropdown-item style="--el-dropdown-menuItem-hover-color:#496C66 " @click="exit()" >退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script>

    import request from "../../utils/request";


    export default {
        name: "Header.vue",
        data(){
            return{
                userName:"未登录"
            }
        },
        created() {
            //未登录就跳转到登录页，否则就执行加载
            if(!localStorage.getItem('userInfo')){
                // this.$router.push('/login');
            }else{
                this.load();
            }
        },
        methods:{
            exit(){
                //退出登录时清空本地缓存中的用户信息
                localStorage.clear();
                this.userName="未登录";
                // axios.post("/changAn/employee/exit").then(res => {
                //     if (res.code === 1){
                //         this.$message({
                //             type:'success',
                //             message:"successful exit"
                //         })
                //         this.$router.push({path: '/login'})  //页面跳转
                //     }else{
                //         this.$message({
                //             type:'error',
                //             message:res.msg
                //         })
                //     }
                // })
                this.$router.push('/');
            },
           load(){
                //成功登录后在头部显示用户名
                this.userName=JSON.parse(localStorage.getItem('userInfo')).username;
            }
    }

    }
</script>

<style scoped>
    .el-dropdown-item-c {
        cursor: pointer;
        color: red;
        background-color: #496C66;
        display: flex;
        align-items: center;
    }

</style>