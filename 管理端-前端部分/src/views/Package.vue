<template>
    <div>
        <div style="width: 1095px;margin-left: 120px;padding: 10px">
            <!--        功能区-->
            <div style="margin: 10px 0;float:left">
                <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="add()">新增</el-button>
            </div>
            <div style="margin: 10px 5px;float:left">
                <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="deleteHandle('批量', null)">批量删除</el-button>
            </div>
            <div style="margin: 10px 5px;float:left">
                <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="statusHandle(0)">批量停售</el-button>
            </div>
            <div style="margin: 10px 0">
                <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="statusHandle(1)">批量起售</el-button>
            </div>
            <!--        搜索区-->
            <div style="margin: 10px 0">
                <el-input v-model="search"  placeholder="请输入内容" clearable style="width: 30%"/>
                <el-button  color="#71A4A3" bg="false"  style="color: #FFFFFF;margin-left: 5px" @click="load()">查询</el-button>
            </div>
            <!--        表格-->
            <el-table :data="tablePacketData"   border style="width: 100%" @selection-change="handleSelectionChange" >
                <el-table-column type="selection" width="55" />
                <el-table-column  prop="id" label="ID"  width="170" />
                <el-table-column prop="name" label="套餐名称" width="120"/>
                <el-table-column  label="图片" width="79" >
                    <template #default="scope">
                        <el-image
                                style="width: 60px; height: 60px"
                                :src="scope.row.image"
                                :preview-src-list="scope.row.image"
                        />
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName"  label="套餐分类" width="90" />
                <el-table-column prop="updateTime" label="更新时间" width="180" />
                <el-table-column prop="price" label="价格(元)" width="85" />
                <el-table-column prop="status" label="状态" width="70" />
                <el-table-column label="操作" width="150">
                    <template #default="scope">
                        <el-button size="small" v-if="scope.row.status=='停售'?false:true" type="primary" color="#71A4A3" bg="false"  style="color: #FFFFFF" @click="handleEdit(scope.row)"
                        >修改</el-button
                        >
                        <el-button type="danger" size="small" @click="deleteHandle('单删', scope.row.id)"  v-if="scope.row.status=='停售'?true:false">
                            删除
                        </el-button>
                        <el-button size="small" type="info"  style="color: #FFFFFF" v-if="scope.row.status=='停售'?false:true"    @click="statusHandle(scope.row)"
                        >停售</el-button
                        >
                        <el-button size="small" type="success" v-if="scope.row.status=='停售'?true:false"   @click="statusHandle(scope.row)"
                        >起售</el-button
                        >
                    </template>
                </el-table-column>
            </el-table>
            <div style="margin: 10px 0">
                <el-pagination
                        v-model:currentPage="currentPage"
                        v-model:page-size="pageSize"
                        :page-sizes="[3, 5]"
                        :small="small"
                        :disabled="disabled"
                        :background="background"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="total"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                />
                <el-dialog v-model="dialogFormVisible" :title="titleInfo"  width="500px" fullscreen="true">
                    <el-dialog
                            v-model="innerVisible"
                            width="30%"
                            title="表格不能为空"
                            append-to-body
                            center
                    />
                    <el-form
                            ref="form"
                            :model="form"
                            :rules="rules"
                            label-width="80px"
                    >
                        <el-form-item label="套餐名称:" style="width: 350px;float:left" prop="name" label-width="90px">
                            <el-input v-model="form.name" />
                        </el-form-item>
                        <el-form-item label="套餐分类:" style="width: 350px;float:left" prop="idType" label-width="90px">
                            <el-select v-model="form.categoryId" placeholder="请选择套餐分类" @change="$forceUpdate()">
                                <el-option v-for="(item, index) in setMealList" :key="index" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="价格:" style="width: 350px;float:left" prop="price" label-width="90px">
                            <el-input v-model="form.price" />
                        </el-form-item>
                        <el-form-item label="套餐菜品:" class="setmealFood" style="clear: both">
                            <el-form-item>
                                <div class="addDish">
                                    <span v-if="dishTable.length == 0" class="addBut" @click="openAddDish"> + 添加菜品</span>
                                    <div v-if="dishTable.length != 0" class="content">
                                        <div class="addBut" style="margin-bottom: 20px" @click="openAddDish">+ 添加菜品</div>
                                        <div class="table">
                                            <el-table :data="dishTable" style="width: 100%">
                                                <el-table-column prop="name" label="名称" width="180" align="center"></el-table-column>
                                                <el-table-column prop="price" label="原价" width="180">
                                                    <template #default="scope"> {{ Number(scope.row.price) / 100 }} </template>
                                                </el-table-column>
                                                <el-table-column prop="address" label="份数" align="center">
                                                    <template #default="scope">
                                                        <el-input-number
                                                                v-model="scope.row.copies"
                                                                size="small"
                                                                :min="1"
                                                                :max="99"
                                                                label="描述文字"
                                                        ></el-input-number>
                                                    </template>
                                                </el-table-column>
                                                <el-table-column prop="address" label="操作" width="180px;" align="center">
                                                    <template #default="scope">
                                                        <el-button type="text" size="small" @click="delDishHandle(scope.$index)"> 删除 </el-button>
                                                    </template>
                                                </el-table-column>
                                            </el-table>
                                        </div>
                                    </div>
                                </div>
                            </el-form-item>
                        </el-form-item>
                        <el-form-item label="描述信息:" style="width: 700px;float:left" prop="description" label-width="90px">
                            <el-input v-model="form.description"   type="textarea"  placeholder="套餐描述，最多600字" maxlength="600" :rows="5" resize="none"/>
                        </el-form-item>
                        <el-form-item label="套餐图片:"  style="width: 800px;">
                            <el-upload
                                    class="avatar-uploader"
                                    action="http://192.168.0.80:9090/changAn/file/upload"
                                    :show-file-list="false"
                                    :on-success="handleAvatarSuccess"
                                    :on-change="onChange"
                                    ref="upload"
                            >
                                <img v-if="imageUrl" :src="imageUrl" class="avatar" style="width: 100px;height:100px"/>
                                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                            </el-upload>
                            <span style="display:inline-block;margin-left: 15px;font-size: 14px;color: #496C66">图片支持JPEG，JPG，PNG格式，大小不超过2MB</span>
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

<!--                添加菜品-->
                <el-dialog
                        title="添加菜品"
                        class="addDishList"
                        v-model="dialogVisibleFood"
                        :visible.sync="dialogVisible"
                        width="60%"
                        :before-close="handleClose"
                >
                <el-input
                        v-model="value"
                        class="seachDish"
                        placeholder="请输入菜品名称进行搜索"
                        style="width: 250px;"
                        size="small"
                        clearable
                >
                <i slot="prefix" class="el-input__icon el-icon-search" style="cursor: pointer" @click="seachHandle">哈哈</i>
                </el-input>
                <el-button  color="#71A4A3" size="small" bg="false"  style="color: #FFFFFF;margin-left: 5px" @click="searchHandle()">搜索</el-button>
                    <!-- <AddDish ref="adddish" :check-list="checkList" :seach-key="seachKey" @checkList="getCheckList" /> -->

            <div class="addDishCon" >
            <div class="leftCont">
                <div v-show="seachkey.trim() == ''" class="tabBut">
                    <span v-for="(item, index) in dishType" :key="index" :class="{act:index == keyInd}" @click="checkTypeHandle(index, item.id)">{{ item.name }}</span>
                </div>
                        <div class="tabList">
                            <div class="table">
                                <div v-if="dishAddList.length == 0" style="padding-left:10px">暂无菜品!</div>
                                <el-checkbox-group v-if="dishAddList.length > 0" v-model="checkedList" @change="checkedListHandle">
                                    <div v-for="(item, index) in dishAddList" :key="index" class="items">
                                        <el-checkbox :key="index" :label="item">
                                            <div class="item">
                                                <span style="flex: 3;text-align: left">{{ item.dishName }}</span>
                                                <span>{{ item.status == 0 ? '停售' : '在售' }}</span>
                                                <span>{{ Number(item.price)/100 }}</span>
                                            </div>
                                        </el-checkbox>
                                    </div>
                                </el-checkbox-group>
                            </div>
                        </div>
                    </div>
                    <div class="ritCont">
                        <div class="tit">已选菜品({{ checkedList.length }})</div>
                        <div class="items">
                            <div v-for="(item, ind) in checkedList" :key="ind" class="item">
                                <span>{{ item.dishName }}</span>
                                <span class="price">￥ {{ Number(item.price)/100 }} </span>
                                <span class="del" @click="delCheck(ind)">
                                    删除
                                 </span>
                            </div>
                        </div>
                    </div>
                </div>
                    <span slot="footer" class="dialog-footer">
                    <el-button @click="handleClose">取 消</el-button>
                    <el-button type="primary" @click="addTableList">确 定</el-button>
                    </span>
                </el-dialog>
            </div>
        </div>
    </div>
</template>

<script>
    import request from "../../utils/request";
    import { ElMessage } from 'element-plus'

    export default {
        name: "Package.vue",
        data(){
            return{
                search:'',
                titleInfo:'新增套餐信息',
                currentPage:1,
                total:10,
                pageSize:5,
                imageUrl:'',
                dialogFormVisible:false,
                innerVisible:false,
                dialogVisibleFood:false,
                dishState:'',
                setMealList:[],
                dishTable:[],
                dishList: [],
                tablePacketData:[
                ],
                checkList: [],
                value: '',
                seachkey:'',
                form  : {
                    'name': '',
                    'categoryId': '',
                    'price': '',
                    'image': '',
                    'description': '',
                    'setMealDishList': [],
                    'status': true,
                    // 'idType': '',
                },
                dishType: [],
                dishAddList: [],
                dishListCache: [],
                keyInd : 0,
                searchValue: '',
                checkedList: []
            }
        },
        watch:{
            seachkey(value){
                if (value.trim()){
                    this.getDishForName(this.seachkey)
                }
            },
            checkList(value){
                this.checkedList = value
            }
        },
        created() {
            if(!localStorage.getItem('userInfo')){
                this.$router.push('/login');
            }
            this.load();
            this.getDishTypeList()
            this.getDishType()
        },
        computed: {
            rules () {
                return {
                    'name': [
                        {'required': true, 'message': '请填写菜品名称', 'trigger': 'blur'}
                    ],
                    'categoryId': [
                        {
                        required: true, message: '请选择套餐分类', trigger: 'change'}
                        ],
                    'description': [
                        { required: true, message: '请填写菜品描述', trigger: 'blur' },
                    ],
                    'price': [
                        {
                            'required': true,
                            // 'message': '请填写菜品价格',
                            validator: (rules, value, callback) => {
                                if (!value) {
                                    callback(new Error('请填写菜品价格'))
                                } else {
                                    const reg = /^\d+(\.\d{0,2})?$/
                                    if (reg.test(value)) {
                                        if(value < 10000){
                                            callback()
                                        }else{
                                            callback(new Error('菜品价格小于10000'))
                                        }
                                    } else {
                                        callback(new Error('菜品价格格式只能为数字,且最多保留两位小数'))
                                    }
                                }
                            },
                            'trigger': 'blur'
                        }
                    ],
                }
            }
        },
        methods: {
            load() {
                request.get('/changAn/setMeal/page', {
                    params: {
                        page: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search,
                    }
                }).then(res => {
                    console.log(res);
                    this.tablePacketData= res.data.records;
                    for (let item of this.tablePacketData) {
                        if (item.status === 0) {
                            item.status = '停售';
                        }
                        if (item.status === 1) {
                            item.status = '起售';
                        }
                         item.price=item.price;
                    }
                    this.total = res.data.total;
                })
            },
            //新增
            add() {
                this.titleInfo = '新增套餐信息';
                this.dialogFormVisible = true;
                //添加分类时要先清空表格
                this.form = {};
                //清空菜品
                this.dishTable=[],
                //清空图片上传列表
                this.imageUrl = ``;
                this.getDishTypeList();
                this.getDishType();
                this.$nextTick(()=>{
                    this.$refs['upload'].clearFiles();
                })
            },
            save() {
                if (this.form.name &&this.form.price&&this.form.description&&this.form.price&&this.form.categoryId) {
                    this.innerVisible = false;
                    this.form.price=parseInt(this.form.price);
                        this.form.status=1;
                    // this.form.categoryId=Number(this.form.categoryId);
                    // this.form.flavor = this.dishFlavors.map(obj => ({ ...obj, value: JSON.stringify(obj.value) }));
                    // delete this.form.dishFlavors;

                    // console.log(this.form);
                    //更新
                    if (this.form.id) {
                        console.log(this.form);

                        request.put('/changAn/setMeal', this.form).then(res => {
                            console.log(res.data);
                            if (res.code === 1) {
                                this.$message({
                                    type: "success",
                                    message: res.data,
                                })
                            } else {
                                this.$message({
                                    type: "error",
                                    message: res.msg,
                                })
                            }
                            this.dialogFormVisible = false;
                            this.load();
                        })
                    }
                    //新增
                    else {
                        request.post('/changAn/setMeal', this.form).then(res => {
                            console.log(res.data);
                            if (res.code === 1) {
                                this.$message({
                                    type: "success",
                                    message: "新增成功"
                                })
                            } else {
                                this.$message({
                                    type: "error",
                                    message: res.msg,
                                })
                            }
                            this.dialogFormVisible = false;
                            this.load();
                        });
                    }
                } else {
                    this.innerVisible = true;
                }
            },
            //修改
            handleEdit(row){
                console.log(typeof row.id)
                let id=row.id;
                request.get('/changAn/setMeal/'+id).then(res=>{
                    console.log(res);
                    this.form=res.data;
                    this.imageUrl = `/api/changAn/file/download?flag=${res.data.image}`;
                    // this.dishTable=res.data.setMealDishList;
                    // this.form.price =res.data.price/100;
                    // this.dishFlavors = res.data.flavor && res.data.flavor.map(obj => ({ ...obj, value: JSON.parse(obj.value),showOption: false }))
                })
                this.dialogFormVisible=true;
                this.titleInfo='编辑分类信息';
                //清空图片上传列表
                // this.$refs['upload'].clearFiles();
                // //清空
                // this.dishFlavors=[];
                this.getDishTypeList();
                this.getDishType();
            },
            //删除
            deleteHandle (type, id) {
                if (type === '批量' && id === null) {
                    if (this.checkList.length === 0) {
                        return this.$message.error('请选择删除对象')
                    }
                }
                this.$confirm('确认删除该菜品, 是否继续?', '确定删除', {
                    'confirmButtonText': '确定',
                    'cancelButtonText': '取消',
                }).then(() => {
                    let ids=type === '批量' ? this.checkList : id;
                    console.log(ids);
                    request.delete('/changAn/setMeal/'+ids).then(res => {
                        if (res.code === 1) {
                            this.$message.success('删除成功！')
                            this.load();
                        } else {
                            this.$message.error(res.msg || '操作失败')
                        }
                    }).catch(err => {
                        this.$message.error('请求出错了：' + err)
                    })
                })
            },
            //状态更改
            statusHandle (row) {
                let params = {}
                console.log(typeof row );
                if (typeof row === 'number' ) {
                    if (this.checkList.length === 0) {
                        this.$message.error('批量操作，请先勾选操作菜品！')
                        return false
                    }
                    params.id = this.checkList;
                    params.status = row
                } else {
                    params.id =String(row.id).split();
                    console.log(params.id)
                    params.status = row.status=='停售' ? 1 : 0;
                }
                this.dishState = params;
                this.$confirm('确认更改该菜品状态?', '提示', {
                    'confirmButtonText': '确定',
                    'cancelButtonText': '取消',
                    'type': 'warning'
                }).then(() => {
                    // 起售停售---批量起售停售接口
                    request.put('/changAn/setMeal/status/'+params.status,params.id).then(res => {
                        if (res.code === 1) {
                            this.$message.success('菜品状态已经更改成功！')
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
            //分页设置
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.load();
            },
            handleCurrentChange(page) {
                this.currentPage = page;
                this.load();
            },
            //文件上传成功
            handleAvatarSuccess (response, file, fileList) {
                // 拼接down接口预览
                if(response.code === 0 && response.msg === '未登录'){
                    window.top.location.href = 'login'
                }else{
                    this.imageUrl = `/api/changAn/file/download?flag=${response.data}`
                    this.form.image = response.data;
                }
            },

            onChange (file) {
                if(file){
                    const suffix = file.name.split('.')[1]
                    const size = file.size / 1024 / 1024 < 2
                    if(['png','jpeg','jpg'].indexOf(suffix) < 0){
                        this.$message.error('上传图片只支持 png、jpeg、jpg 格式！')
                        this.$refs.upload.clearFiles()
                        return false
                    }
                    if(!size){
                        this.$message.error('上传文件大小不能超过 2MB!')
                        return false
                    }
                    return file
                }
            },
            searchHandle() {
                this.seachkey = this.value;
            },
            // 获取套餐分类
            getDishTypeList() {
                request.get('/changAn/category/list',{
                    params:{
                        type:2,
                        // page:1,
                        // pageSize:1000
                    }
                }).then((res) => {
                    if (res.code === 1) {
                        this.setMealList = res.data.map((obj) => ({ ...obj, idType: obj.id }))
                    } else {
                        this.$message.error(res.msg || '操作失败')
                    }
                })
            },
            // 删除套餐菜品
            delDishHandle(index) {
                this.dishTable.splice(index, 1)
                this.checkList.splice(index, 1)
            },

            // 获取添加菜品数据
            getCheckList(value) {
                this.checkList = [...value]
            },

            // 添加菜品
            openAddDish() {
                this.seachkey = ''
                this.dialogVisibleFood = true;
                //搜索条件清空，菜品重新查询，菜品类别选第一个重新查询
                this.value = ''
                this.keyInd = 0
                this.getDishList(this.dishType[0].id)
            },
            // 取消添加菜品
            handleClose(done) {
                // this.$refs.adddish.close()
                this.dialogVisibleFood = false;
                // this.checkList = JSON.parse(JSON.stringify(this.dishTable))
                // this.dialogVisible = false
            },

            // 保存添加菜品列表
            addTableList() {
                this.dishTable = JSON.parse(JSON.stringify(this.checkList))
                this.form.setMealDishList=this.dishTable;
                // console.log("dishTable "+this.dishTable )
                this.dishTable.forEach((n) => {
                    n.copies = 1
                })
                this.dialogVisibleFood = false
                // 添加处理逻辑清空选中list
                // this.checkList = []
            },
            // 获取菜品分类
            getDishType () {
               request.get('/changAn/category/list',{
                    params:{
                        type:1
                    }
                }).then(res => {
                    if (res.code === 1) {
                        this.dishType = res.data;
                        this.getDishList(res.data[0].id);
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },

            // 通过套餐ID获取菜品列表分类
            getDishList (id) {
                request.get('/changAn/dish/list',{
                    params:{
                        categoryId: id
                    }
                }).then(res => {
                    if (res.code === 1) {
                        if (res.data.length == 0) {
                            this.dishAddList = [];
                            return
                        }
                        let newArr = res.data;
                        newArr.forEach((n) => {
                            n.dishId = n.id
                            n.copies = 1
                            // n.dishCopies = 1
                            n.dishName = n.name
                        })
                        this.dishAddList = newArr
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },

            // 关键词收搜菜品列表分类
            getDishForName (name) {

               request.get('/changAn/dish/list',{
                    params:{
                        id: id
                    }
                }).then(res => {
                    if (res.code === 1) {
                        let newArr = res.data
                        newArr.forEach((n) => {
                            n.dishId = n.id
                            n.dishName = n.name
                        })
                        this.dishAddList = newArr
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },

            checkTypeHandle (ind,id) {
                this.keyInd = ind
                this.getDishList(id)
            },

            checkedListHandle (value) {
                this.getCheckList(this.checkedList)
            },

            open (done) {
                this.dishListCache = JSON.parse(JSON.stringify(this.checkList))
            },

            close (done) {
                this.checkList = this.dishListCache
            },

            // 删除
            delCheck (ind){
                this.checkedList.splice(ind, 1)
            }
        }
    }
</script>

<style >
    .avatar-uploader .el-upload {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
    }

    .avatar-uploader .el-upload:hover {
        border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 128px;
        height: 128px;
        text-align: center;
    }
    .addDish .el-input {
        width: 130px;
    }
    .addDish .el-input-number__increase {
        border-left: solid 1px #FFE1CA;
        background: #fff3ea;
    }
    .addDish .el-input-number__decrease {
        border-right: solid 1px #FFE1CA;
        background: #fff3ea;
    }
    .addDish input {
        border: 1px solid #ffe1ca;
    }
    .addDish .table {
        border: solid 1px #EBEEF5;
        border-radius: 3px;
    }
    .addDish .table th {
        padding: 5px 0;
    }
    .addDish .table td {
        padding: 7px 0;
    }
    .addDishList .seachDish {
        position: absolute;
        top: 10px;
        right: 20px;
    }
    .addDishList .el-dialog__body {
        padding: 0;
        border-bottom: solid 1px #ccc;
    }
    .addDishList .el-dialog__footer {
        padding-top: 27px;
    }

    .addDish {
        width: 777px;
    }
    .addDish .addBut {
        background: #496C66;
        color: #FFFFFF;
        display: inline-block;
        padding: 0px 20px;
        border-radius: 3px;
        line-height: 40px;
        cursor: pointer;
        border-radius: 4px;
        font-weight: 500;
    }
    .addDish .content {
        background: #fafafb;
        padding: 20px;
        border: solid 1px #ccc;
        border-radius: 3px;
    }
    .addDishCon {
        padding: 0 20px;
        display: flex;
        line-height: 40px;
        border: 1px solid #496C66;
        margin: 20px 0;
    }
    .addDishCon .leftCont {
        display: flex;
        border-right: solid 2px #E4E7ED;
        width: 60%;
        padding: 15px;
    }
    .addDishCon .leftCont .tabBut {
        width: 110px;
    }
    .addDishCon .leftCont .tabBut span {
        display: block;
        text-align: center;
        border-right: solid 2px #f4f4f4;
        cursor: pointer;
    }
    .addDishCon .leftCont .act {
        border-color: #FFC200 !important;
        color: #FFC200 !important;
    }
    .addDishCon .leftCont .tabList {
        flex: 1;
        padding: 15px;
    }
    .addDishCon .leftCont .tabList .table {
        border: solid 1px #f4f4f4;
        border-bottom: solid 1px #f4f4f4;
    }
    .addDishCon .leftCont .tabList .table .items {
        border-bottom: solid 1px #f4f4f4;
        padding: 0 10px;
        display: flex;
    }
    .addDishCon .leftCont .tabList .table .items .el-checkbox, .addDishCon .leftCont .tabList .table .items .el-checkbox__label {
        width: 100%;
    }
    .addDishCon .leftCont .tabList .table .items .item {
        display: flex;
        padding-right: 20px;
    }
    .addDishCon .leftCont .tabList .table .items .item span {
        display: inline-block;
        text-align: center;
        flex: 1;
    }
    .addDishCon .ritCont {
        width: 40%;
        padding: 0 15px;
    }
    .addDishCon .ritCont .item {
        box-shadow: 0px 1px 4px 3px rgba(0, 0, 0, 0.03);
        display: flex;
        text-align: center;
        padding: 0 10px;
        margin-bottom: 20px;
        border-radius: 6px;
        color: #818693;
    }
    .addDishCon .ritCont .item span:first-child {
        text-align: left;
        color: #20232A;
    }
    .addDishCon .ritCont .item .price {
        display: inline-block;
        flex: 1;
    }
    .addDishCon .ritCont .item .del {
        cursor: pointer;
    }
    .addDishCon .ritCont .item .del img {
        position: relative;
        top: 5px;
        width: 20px;
    }

    .addDishCon .el-checkbox__label{
        width: 100%;
    }
    #combo-add-app .setmealFood .el-form-item__label::before{
        content: '*';
        color: #F56C6C;
        margin-right: 4px;
    }

    #combo-add-app .uploadImg .el-form-item__label::before{
        content: '*';
        color: #F56C6C;
        margin-right: 4px;
    }
</style>