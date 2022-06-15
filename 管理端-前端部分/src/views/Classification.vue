<template>
    <div>
        <div style="width: 990px;margin-left: 120px;padding: 10px">
            <!--        功能区-->
            <div style="margin: 10px 0">
                <el-button color="#71A4A3" bg="false" style="color: #ffffff" @click="add()">新增</el-button>
            </div>
            <!--        表格-->
            <el-table :data="tableTypeData"  :default-sort="{ prop: '', order: 'ascending' }"  border style="width: 100%">
                <el-table-column prop="id" v-show="false" label="ID"  width="170" />
                <el-table-column prop="name" label="菜品名称" width="150" />
                <el-table-column prop="type" label="分类类型" width="150" />
                <el-table-column prop="updateTime"  label="操作时间" width="180" />
                <el-table-column prop="sort" label="排序" width="70" />
                <el-table-column label="操作" width="220">
                    <template #default="scope">
                        <el-button size="small" type="primary" color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="handleEdit(scope.row)"
                        >编辑</el-button
                        >
                        <el-popconfirm  title="你确定要删除吗?" @confirm="handleDelete(scope.row.id)">
                            <template #reference>
                                <el-button type="danger" size="small" >删除</el-button>
                            </template>
                        </el-popconfirm>
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
                <el-dialog v-model="dialogFormVisible" :title="titleInfo"  width="500px" >
                    <el-dialog
                            v-model="innerVisible"
                            width="30%"
                            title="表格不能为空"
                            append-to-body
                            center
                    />
                    <el-form :model="form" label-width="80px">
                        <el-form-item label="分类名称" style="width: 350px">
                            <el-input v-model="form.name" @blur="CheckName()"/>
                        </el-form-item>
                        <el-form-item label="分类类型:">
                            <el-radio-group v-model="form.type">
                                <el-radio label="菜品分类" />
                                <el-radio label="套餐分类"/>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="权重值" style="margin-bottom: 195px">
                            <el-select v-model="form.sort" placeholder="请输入分类的权重值" >
                                <el-option label="1" value=1 />
                                <el-option label="2" value=2 />
                                <el-option label="3" value=3 />
                                <el-option label="4" value=4 />
                                <el-option label="5" value=5 />
                                <el-option label="6" value=6 />
                                <el-option label="7" value=7 />
                                <el-option label="8" value=8 />
                                <el-option label="9" value=9 />
                                <el-option label="10" value=10 />
                            </el-select>
                            <span style="display:inline-block;margin-left: 15px;font-size: 10px;color: #496C66">权重值越小信息越靠前</span>
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
            </div>
        </div>
    </div>
</template>

<script>
    import request from "../../utils/request";
    import { ElMessage } from 'element-plus'

    export default {
        name: "Classification.vue",
        data(){
            return{
                search:'',
                titleInfo:'新增分类信息',
                currentPage:1,
                total:10,
                pageSize:10,
                dialogFormVisible:false,
                innerVisible:false,
                form:{},
                tableTypeData :[
                ],
            }
        },
        created() {
            if(!localStorage.getItem('userInfo')){
                this.$router.push('/login');
            }
            this.load();
        },
        methods:{
            //渲染分类表格
            load(){
                request.get('/changAn/category/page',{
                    params:{
                        page:this.currentPage,
                        pageSize:this.pageSize,
                    }
                }).then(res=>{
                    console.log(res);
                    this.tableTypeData=res.data.records;
                    for(let item of this.tableTypeData){
                        if(item.type===1){
                            item.type='菜品分类';
                        }
                        if(item.type===2){
                            item.type='套餐分类';
                        }
                    }
                    this.total=res.data.total;
                })
            },
            //增加新分类
            add(){
                this.titleInfo='新增分类信息';
                this.dialogFormVisible=true;
                //添加分类时要先清空表格
                this.form={};
            },
            //点击对话框的确认按钮将数据传到后台
            save(){
                if(this.form.name&&this.form.type&&this.form.sort){
                    this.innerVisible=false;
                    this.form.sort=parseInt(this.form.sort);
                    if(this.form.type=='菜品分类'){
                        this.form.type=1;
                    }
                    if(this.form.type=='套餐分类'){
                        this.form.type=2;
                    }
                    //更新
                    if(this.form.id){
                        console.log(this.form);
                        request.put('/changAn/category',this.form).then(res=>{
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
                        console.log(this.form);
                        request.post('/changAn/category',this.form).then(res=>{
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
            //分类内容修改
            handleEdit(row){
                this.form=JSON.parse(JSON.stringify(row));
                this.dialogFormVisible=true;
                this.titleInfo='编辑分类信息';
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
                request.delete('/changAn/category/'+id).then(res=>{
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
            //正则检查表单填写是否规范
            CheckName(){
                if( this.form.name==undefined){
                    ElMessage({
                        message: '分类名称不能为空',
                        type: 'warning',
                    })
                }
            },
        }
    }
</script>

<style scoped>

</style>