<template>
    <div style="width: 1020px;margin-left: 120px;padding: 10px">
<!--        功能区-->
        <div style="margin: 10px 0">
            <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="add()">新增</el-button>
        </div>
<!--        搜索区-->
        <div style="margin: 10px 0">
            <el-input v-model="search"  placeholder="请输入内容" clearable style="width: 30%"/>
            <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF;margin-left: 5px" @click="load()">查询</el-button>
        </div>
<!--        表格-->
        <el-table :data="tableData"   border style="width: 100%">
            <el-table-column prop="id" label="ID"  width="140" />
            <el-table-column prop="username" label="账号名" width="150" />
<!--            <el-table-column prop="idNumber" label="身份证" width="150" />-->
            <el-table-column prop="name" label="姓名" width="150" />
            <el-table-column prop="role" label="职能" width="90" />
            <el-table-column prop="sex" label="性别" width="120" />
            <el-table-column prop="phone" label="电话号码" width="150" />
            <el-table-column label="操作" width="220">
                <template #default="scope">
                    <el-button size="small" type="primary" color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="handleEdit(scope.row)"
                    >编辑</el-button
                    >
                    <el-popconfirm v-if="isShow" title="你确定要删除吗?" @confirm="handleDelete(scope.row.id)">
                        <template #reference>
                            <el-button type="danger" size="small" >删除</el-button>
                        </template>
                    </el-popconfirm>
                    <el-button v-if="false" size="small" type="info"  @click="handleUse(scope.row.id)"
                    >禁用</el-button
                    >
                </template>
            </el-table-column>
        </el-table>
        <div style="margin: 10px 0">
            <el-pagination
                    v-model:currentPage="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[5, 10]"
                    :small="small"
                    :disabled="disabled"
                    :background="background"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
            />
                <el-dialog v-model="dialogFormVisible" :title="titleInfo" width="500px">
                    <el-dialog
                            v-model="innerVisible"
                            width="30%"
                            title="表格不能为空"
                            append-to-body
                            center
                    />
                    <el-form :model="form" label-width="80px">
                        <el-form-item label="账号名" style="width: 350px">
                            <el-input v-model="form.username" @blur="CheckUserName()"/>
                        </el-form-item>
                        <el-form-item label="姓名" style="width: 350px">
                            <el-input v-model="form.name" @blur="CheckName()"/>
                        </el-form-item>
                        <el-form-item label="职能">
                            <el-radio-group v-model="form.role">
                                <el-radio label="店长" />
                                <el-radio label="前台" />
                                <el-radio label="厨师" />
                                <el-radio label="普通员工" />
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="性别">
                            <el-radio-group v-model="form.sex">
                                <el-radio label="男" />
                                <el-radio label="女" />
                                <el-radio label="未知" />
                            </el-radio-group>
                        </el-form-item>
<!--                        <el-form-item label="账号" style="width: 350px">-->
<!--                            <el-input v-model="form. idNumber"/>-->
<!--                        </el-form-item>-->
                        <el-form-item label="电话号码" style="width: 350px">
                            <el-input v-model="form.phone" @blur="CheckUserPhone()"/>
                        </el-form-item>

                    </el-form>
                    <template #footer>
                      <span class="dialog-footer">
                        <el-button @click="dialogFormVisible = false">取消</el-button>
                        <el-button type="primary" @click="save()"
                         >确认</el-button
                        >
                      </span>
                    </template>
                </el-dialog>
            <el-dialog v-model="dialogUseFormVisible" title="员工权限管理" width="250px">
                <el-form :model="Useform" label-width="50px">
                    <el-form-item label="权限">
                        <el-radio-group v-model="Useform.use">
                            <el-radio label="禁用" />
                            <el-radio label="使用" />
                        </el-radio-group>
                    </el-form-item>
                </el-form>
                <template #footer>
                      <span class="dialog-footer">
                        <el-button @click="dialogUseFormVisible = false">取消</el-button>
                        <el-button type="primary" @click="saveUse()"
                        >确认</el-button
                        >
                      </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script>
   import request from "../../utils/request";
   import { ElMessage } from 'element-plus'

   export default {
        name: "Employee.vue",
        data(){
            return{
                search:'',
                titleInfo:'新增员工信息',
                currentPage:1,
                total:10,
                pageSize:10,
                isShow:false,
                dialogFormVisible:false,
                dialogUseFormVisible :false,
                innerVisible:false,
                form:{},
                Useform:{},
                tableData :[
                   ],
            }
        },
        created() {
            if(!localStorage.getItem('userInfo')){
                this.$router.push('/login');
            }
            if(JSON.parse(localStorage.getItem('userInfo')).role=='店长'){
                this.isShow=true;
            }
            this.load();
        },
        methods:{
            //渲染员工表格
            load(){
                request.get('/changAn/employee/page',{
                    params:{
                        page:this.currentPage,
                        pageSize:this.pageSize,
                        search:this.search,
                    }
                }).then(res=>{
                    console.log(res);
                    this.tableData=res.data.records;
                    this.total=res.data.total;
                })
            },
            //增加新员工
            add(){
                this.titleInfo='新增员工信息';
                this.dialogFormVisible=true;
                //添加员工时要先清空表格
                this.form={};
            },
            //点击对话框的确认按钮将数据传到后台
            save(){
                 if(this.form.username&&this.form.name&&this.form.role&&this.form.sex&&this.form.phone){
                     this.innerVisible=false;
                     //更新
                     if(this.form.id){
                         request.put('/changAn/employee',this.form).then(res=>{
                             console.log(res.data);
                             if(res.code===1){
                                 this.$message({
                                     type:"success",
                                     message:res.data,
                                 })
                             }
                             else{
                                 this.$message({
                                     type:"error",
                                     message:res.msg,
                                 })
                             }
                             this.dialogFormVisible=false;
                             this.load();
                         })
                     }
                     //新增
                     else{
                         request.post('/changAn/employee',this.form).then(res=>{
                             console.log(res.data);
                             if(res.code===1){
                                 this.$message({
                                     type:"success",
                                     message:"新增成功"
                                 })
                             }
                             else{
                                 this.$message({
                                     type:"error",
                                     message:res.msg,
                                 })
                             }
                             this.dialogFormVisible=false;
                             this.load();
                         });
                     }
                 }else{
                     this.innerVisible=true;
                 }
            },
            //员工内容修改
            handleEdit(row){
                this.form=JSON.parse(JSON.stringify(row));
                this.dialogFormVisible=true;
                this.titleInfo='编辑员工信息';
            },
            handleSizeChange(pageSize){
                this.pageSize=pageSize;
                this.load();
            },
            handleCurrentChange(page){
                this.currentPage=page;
                this.load();
            },
            //删除
            handleDelete(id){
               console.log(id);
               request.delete('/changAn/employee/'+id).then(res=>{
                   if(res.code===1){
                       this.$message({
                           type:"success",
                           message:res.data,
                       })
                   }
                   else{
                       this.$message({
                           type:"error",
                           message:res.msg,
                       })
                   }
                   this.load();
               })
            },
            //设置禁用状态
            handleUse(id){
                this. dialogUseFormVisible =true;
            },
            //点击禁用对话框的确认按钮触发的事件
            saveUse(){
                request.put('/changAn/employee',this.Useform).then(res=>{
                    if(res.code===1){
                        this.$message({
                            type:"success",
                            message:"设置成功"
                        })
                    }
                    else{
                        this.$message({
                            type:"error",
                            message:res.msg,
                        })
                    }
                })
            },
            //正则检查表单填写是否规范
            CheckUserName(){
                if( this.form.username==undefined|| /^[a-zA-Z0-9]{2,8}$/.test(this.form.username)==false){
                    ElMessage({
                        message: '账号名为2-8位数字和字母',
                        type: 'warning',
                    })
                }
            },
            CheckName(){
                if( this.form.name==undefined|| /^[\u4e00-\u9fa5]{2,15}$/.test(this.form.name)==false){
                    ElMessage({
                        message: '姓名为汉字，不超过15位',
                        type: 'warning',
                    })
                }
            },
            CheckUserPhone(){
                if( this.form.phone==undefined|| /^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$/.test(this.form.phone)==false){
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

</style>