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
            <el-table :data="tableFoodData"   border style="width: 100%" @selection-change="handleSelectionChange" >
                <el-table-column type="selection" width="55" />
                <el-table-column v-show="false" prop="id" label="ID"  width="170" />
                <el-table-column prop="name" label="菜品名称" width="120"/>
                <el-table-column  label="图片" width="79" >
                    <template #default="scope">
                    <el-image
                            style="width: 60px; height: 60px"
                            :src="scope.row.image"
                            :preview-src-list="scope.row.image"
                    />
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName"  label="菜品分类" width="90" />
                <el-table-column prop="updateTime" label="更新时间" width="180" />
                <el-table-column prop="price" label="价格(元)" width="85" />
                <el-table-column prop="status" label="状态" width="70" />
                <el-table-column prop="sort" label="排序" width="70" />
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
                        <el-form-item label="菜品名称:" style="width: 350px;float:left" prop="name" label-width="90px">
                            <el-input v-model="form.name" />
                        </el-form-item>
                        <el-form-item label="菜品分类:" style="width: 350px;float:left" prop="categoryId" label-width="90px">
                            <el-select
                                    v-model="form.categoryId"
                                    placeholder="请选择菜品分类"
                            >
                                <el-option v-for="(item,index) in dishList" :key="index" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="价格:" style="width: 350px;float:left" prop="price" label-width="90px">
                            <el-input v-model="form.price" />
                        </el-form-item>
                        <el-form-item label="权重值:" prop="sort" label-width="90px">
                            <el-select v-model="form.sort" placeholder="请输入分类的权重值">
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
                            <span style="display:inline-block;margin-left: 15px;font-size: 14px;color: #496C66">权重值越小信息越靠前</span>
                        </el-form-item>
                        <el-form-item label="口味做法:" style="clear:both;">
                            <el-form-item>
                                <div class="flavorBox">
                                  <span
                                  v-if="dishFlavors.length == 0"
                                  class="addBut"
                                  @click="addFlavore"
                                  > + 添加口味</span>
                                <div v-if="dishFlavors.length != 0" class="flavor">
                                <div class="title">
                                    <span>口味名（3个字内）</span><span>口味标签（输入标签回车添加）</span>
                                </div>
                                <div class="cont">
                                <div v-for="(item, index) in dishFlavors" :key="index" class="items">
                                    <div class="itTit">
                                        <!-- <SelectInput
                                          :dish-flavors-data="dishFlavorsData"
                                          :index="index"
                                          :value="item.name"
                                          @select="selectHandle"
                                        /> -->
                                        <div class="selectInput">
                                        <div>
                                            <el-input
                                                    v-model="item.name"
                                                    type="text"
                                                    style="width: 100%"
                                                    placeholder="请输入口味"
                                                    @focus="selectFlavor(true,index)"
                                                    @blur="outSelect(false,index)"
                                                    @input="inputHandle(index)"
                                            />
                                        </div>
                                        <div v-show="item.showOption" class="flavorSelect">
                          <span
                                  v-for="(it, ind) in dishFlavorsData"
                                  :key="ind"
                                  class="items"
                                  @click="checkOption(it,ind,index)"
                          >{{ it.name }}</span>
                        <span
                                v-if="dishFlavorsData == []"
                                class="none"
                        >无数据</span>
                                </div>
                            </div>
                        </div>
                        <div class="labItems" style="display: flex">
                            <span v-for="(it, ind) in item.value" :key="ind">{{ it }} <i @click="delFlavorLabel(index, ind)">X</i></span>
                            <div
                                    class="inputBox"
                                    :style="inputStyle"
                                    contenteditable="true"
                                    @focus="flavorPosition(index)"
                                    @keydown.enter="(val)=>keyDownHandle(val,index)"
                            ></div>
                        </div>
                        <span class="delFlavor delBut non" @click="delFlavor(index)">删除</span>
                        </div>
                        </div>
                        <div class="addBut" @click="addFlavore">添加口味</div>
                        </div>
                    </div>
                    </el-form-item>
                </el-form-item>
                        <el-form-item label="描述信息:" style="width: 700px;float:left" prop="description" label-width="90px">
                            <el-input v-model="form.description"   type="textarea"  placeholder="菜品描述，最多600字" maxlength="600" :rows="5" resize="none"/>
                        </el-form-item>
                        <el-form-item label="菜品图片:"  style="width: 800px;">
                            <el-upload
                                    class="avatar-uploader"
                                    action="http://192.168.0.80:9090/changAn/file/upload"
                                    :show-file-list="false"
                                    :on-success="handleAvatarSuccess"
                                    :on-change="onChange"
                                    ref="upload"
                            >
                                <img v-if="imageUrl" :src="imageUrl" class="avatar"/>
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
            </div>
        </div>
    </div>
</template>

<script >
    import request from "../../utils/request";
    import { ElMessage } from 'element-plus'
    // import { getCurrentInstance } from 'vue'
    // import {ElTable} from 'element-plus'
    // import { toRefs } from '@vue/reactivity';

    export default {
        name: "Food.vue",
        data(){
            return{
                search:'',
                titleInfo:'新增分类信息',
                currentPage:1,
                total:10,
                pageSize:5,
                imageUrl:'',
                dialogFormVisible:false,
                innerVisible:false,
                dishState:'',
                dishList: [],
                dishFlavorsData: [],
                dishFlavors: [],
                // form:{},
                tableFoodData :[
                ],
                checkList: [],
                form  : {
                    'name': '',
                    'categoryId': '',
                    'price': '',
                    'sort': '',
                    'image': '',
                    'description': '',
                    'dishFlavors': [],
                    'status': true,
                },
            }
        },
        created() {
            if(!localStorage.getItem('userInfo')){
                this.$router.push('/login');
            }
            this.load();
        },
        computed: {
            rules () {
                return {
                    'name': [
                        {'required': true, 'message': '请填写菜品名称', 'trigger': 'blur'}
                    ],
                    'categoryId': [
                        {'required': true, 'message': '请选择菜品分类', 'trigger': 'change'}
                    ],
                    'sort': [
                        {
                            required: true,
                            message: '请选择权重值',
                            trigger: 'change',
                        }],
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
                request.get('/changAn/dish/page', {
                    params: {
                        page: this.currentPage,
                        pageSize: this.pageSize,
                        search: this.search,
                    }
                }).then(res => {
                    console.log(res);
                    this.tableFoodData = res.data.records;
                    for (let item of this.tableFoodData) {
                        if (item.status === 0) {
                            item.status = '停售';
                        }
                        if (item.status === 1) {
                            item.status = '起售';
                        }
                         item.price=item.price/100;
                    }
                    this.total = res.data.total;
                })
            },
            //新增
            add() {
                this.titleInfo = '新增菜品信息';
                this.dialogFormVisible = true;
                //添加分类时要先清空表格
                this.form = {};
                //清空图片上传列表
                // this.$refs['upload'].clearFiles();
                this.imageUrl = ``;
                this.dishFlavors = [];
                // //获取菜品分类
                this.getDishList();
                // 口味临时数据
                this.getFlavorListHand();
                this.$nextTick(()=>{
                    this.$refs['upload'].clearFiles();
                })
            },
            save() {
                if (this.form.name &&this.form.categoryId && this.form.sort&&this.form.price&&this.form.description&&this.dishFlavors&&this.form.image) {
                    this.innerVisible = false;
                    this.form.sort = parseInt(this.form.sort);
                    this.form.price=parseInt(this.form.price);
                    // this.form.categoryId=Number(this.form.categoryId);
                    this.form.flavor = this.dishFlavors.map(obj => ({ ...obj, value: JSON.stringify(obj.value) }));
                    delete this.form.dishFlavors;

                    console.log(this.form);
                    //更新
                    if (this.form.id) {
                        console.log(this.form);
                        request.put('/changAn/dish', this.form).then(res => {
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
                        request.post('/changAn/dish', this.form).then(res => {
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
                // this.form=JSON.parse(JSON.stringify(row));
                let id=row.id;
                console.log(id)
                request.get('/changAn/dish/'+id).then(res=>{
                    console.log(res);
                    this.form=res.data;
                    this.imageUrl = `/api/changAn/file/download?flag=${res.data.image}`;
                    // this.form.price =res.data.price/100;
                    this.dishFlavors = res.data.flavor && res.data.flavor.map(obj => ({ ...obj, value: JSON.parse(obj.value),showOption: false }))
                })
                this.dialogFormVisible=true;
                this.titleInfo='编辑分类信息';
                //清空图片上传列表
                // this.$refs['upload'].clearFiles();
                // //清空
                // this.dishFlavors=[];
                // //获取菜品分类
                this.getDishList();
                // 口味临时数据
                this.getFlavorListHand();
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
                    request.delete('/changAn/dish/'+ids).then(res => {
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
                    request.put('/changAn/dish/status/'+params.status,params.id).then(res => {
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
                    // this.form.image=`/api/changAn/file/download?flag=${response.data}`
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
            // handleAvatarSuccess(res){
            //     console.log(res.data);
            //     //回显到表单中
            //     this.imageUrl=res.data;
            //     this.form.image=res.data;
            // },
            // 按钮 - 添加口味
            addFlavore () {
                this.dishFlavors.push({'name': '', 'value': [], showOption: false}) // JSON.parse(JSON.stringify(this.dishFlavorsData))
            },

            // 按钮 - 删除口味
            delFlavor (ind) {
                this.dishFlavors.splice(ind, 1)
            },

            // 按钮 - 删除口味标签
            delFlavorLabel (index, ind) {
                this.dishFlavors[index].value.splice(ind, 1)
            },

            //口味位置记录
            flavorPosition (index) {
                this.index = index
            },

            // 添加口味标签
            keyDownHandle (val,index) {
                console.log('keyDownHandle----val',val)
                console.log('keyDownHandle----index',index)
                console.log('keyDownHandle----this.dishFlavors',this.dishFlavors)
                if (event) {
                    event.cancelBubble = true
                    event.preventDefault()
                    event.stopPropagation()
                }

                if (val.target.innerText.trim() != '') {
                    this.dishFlavors[index].value.push(val.target.innerText)
                    val.target.innerText = ''
                }
            },
            // 获取菜品分类
            getDishList () {
                request.get('/changAn/category/list',{
                    params:{
                        type:1
                    }
                }).then(res => {
                    console.log(res)
                    if (res.code === 1) {
                        this.dishList = res.data
                    } else {
                        this.$message.error(res.msg || '操作失败')
                    }
                })
            },

            // 获取口味列表
            getFlavorListHand () {
                // flavor flavorData
                this.dishFlavorsData = [
                    {'name':'甜味','value':['无糖','少糖','半糖','多糖','全糖']},
                    {'name':'温度','value':['热饮','常温','去冰','少冰','多冰']},
                    {'name':'忌口','value':['不要葱','不要蒜','不要香菜','不要辣']},
                    {'name':'辣度','value':['不辣','微辣','中辣','重辣']}
                ]
            },

            selectFlavor(st,index){
                // console.log('st',st)
                // console.log('index',index)
                // console.log('this.dishFlavors',this.dishFlavors)
                const obj = JSON.parse(JSON.stringify(this.dishFlavors[index]))
                obj.showOption = st
                this.dishFlavors.splice(index,1,obj)
                // this.dishFlavors[index].showOption = st
            },

            outSelect(st,index){
                const _this = this
                setTimeout(()=> {
                    const obj = JSON.parse(JSON.stringify(_this.dishFlavors[index]))
                    obj.showOption = st
                    _this.dishFlavors.splice(index,1,obj)
                }, 200)
            },

            inputHandle(val,index){
                // this.selectFlavor(false,index)
            },

            checkOption(val, ind, index){
                this.selectHandle(val.name, index, ind)
                this.dishFlavors[index].name = val.name
            },

            selectHandle(val, key, ind){
                const arrDate = [...this.dishFlavors]
                arrDate[key] = JSON.parse(JSON.stringify(this.dishFlavorsData[ind]))
                this.dishFlavors = arrDate
            },

        }
        }
</script>

<style scoped>
    .avatar-uploader .avatar {
        width: 98px;
        height:128px;
        display: block;
    }
</style>
<style>

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
    .flavorBox {
        width: 777px;
    }
    .flavorBox .addBut {
        background: #496C66;
        display: inline-block;
        padding: 0px 20px;
        border-radius: 3px;
        line-height: 40px;
        cursor: pointer;
        border-radius: 4px;
        color: #f4f4f4;
        font-weight: 500;
    }
    .flavorBox .flavor {
        border: solid 1px #dfe2e8;
        border-radius: 3px;
        padding: 15px;
        background: #fafafb;
    }
    .flavorBox .flavor .title {
        color: #606168;
    }
    .flavorBox .flavor .cont .items {
        display: flex;
        margin: 10px 0;
    }
    .flavorBox .flavor .cont .items .itTit {
        width: 150px;
        margin-right: 15px;
    }
    .flavorBox .flavor .cont .items .itTit input {
        width: 100%;
        line-height: 40px;
        border-radius: 3px;
        padding: 0 10px;
    }
    .flavorBox .flavor .cont .items .labItems {
        flex: 1;
        display: flex;
        flex-wrap: wrap;
        border-radius: 3px;
        min-height: 39px;
        border: solid 1px #d8dde3;
        background: #fff;
        padding: 0 5px;
    }
    .flavorBox .flavor .cont .items .labItems span {
        display: inline-block;
        margin: 5px;
        line-height: 26px;
        height: 26px;
        padding: 0 10px;
        background: darkseagreen;
        border-radius: 3px;
        border: solid 1px darkolivegreen;
        color: white;
    }
    .flavorBox .flavor .cont .items .labItems span i {
        cursor: pointer;
        font-style: normal;
    }
    .flavorBox .flavor .cont .items .labItems .inputBox {
        display: inline-block;
        width: 100%;
        height: 26px;
        line-height: 26px;
        overflow: hidden;
    }
    .inputBox:focus{
         border: 1px solid #496C66;
    }
    .flavorBox .flavor .cont .items .delFlavor {
        display: inline-block;
        padding: 0 10px;
        color: red;
        cursor: pointer;
    }
    .selectInput {
        position: relative;
        width: 100%;
        min-width: 100px;
    }
    .selectInput .flavorSelect {
        position: absolute;
        width: 100%;
        padding: 0 10px;
        border-radius: 3px;
        border: solid 1px #496C66;
        line-height: 30px;
        text-align: center;
        background: #fff;
        top: 50px;
        z-index: 99;
    }
    .selectInput .flavorSelect .items {
        cursor: pointer;
        display: inline-block;
        width: 100%;
        line-height: 35px;
        border-bottom: solid 1px #f4f4f4;
        color: #666;
    }
    .selectInput .flavorSelect .none {
        font-size: 14px;
    }

    #food-add-app .uploadImg .el-form-item__label::before{
        content: '*';
        color: #F56C6C;
        margin-right: 4px;
    }

</style>