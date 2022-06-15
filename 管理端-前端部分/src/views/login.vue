<template>
    <div class="login-register">
        <div class="contain">
            <div class="big-box" :class="{active:isLogin}">
                <div class="big-contain" >
                    <div class="btitle">账户登录</div>
                    <div class="bform" :model="form">
                        <input type="username" placeholder="账号" v-model="form.username">
                        <span class="errTips" v-if="nameError">* 用户名填写错误 *</span>
                        <input type="password" placeholder="密码" v-model="form.password">
                        <span class="errTips" v-if="passwordError">* 密码填写错误 *</span>
                    </div>
                    <button class="bbutton" @click="login">登录</button>
                </div>
            </div>
            <div class="small-box" :class="{active:isLogin}">
                <div class="small-contain">
                    <div class="stitle">欢迎回来!</div>
                    <p class="scontent">与我们保持联系，请登录你的账户</p>
                    <button class="sbutton">登录</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import request from "../../utils/request";
    export default {
        name: 'login',
        data() {
            return {
                isLogin: false,
                nameError: false,
                passwordError: false,
                existed: false,
                form: {
                    username: '',
                    password: ''
                }
            }
        },
        methods: {
            login() {
                //用户名：4~8，字母，数字
                //密码：字母数字下划线，不少于6位
                function isusn(str) {
                    var reg = /^(?![0-9]+$(?![a-zA-Z]+$)[0-9a-zA-Z]{2,8}$)/
                    return reg.test(str)
                }
                function ispwd(str) {
                    var reg=/^[\w+$]{6,}/
                    // var reg = /=.*([a-zA-Z].*)(?=.*[0-9].*)[a-zA-Z0-9-*/+.~.!@#$%^&*()]{6,}$/
                    return reg.test(str)
                }
                if (this.form.username != "" && this.form.password != "") {
                    if (isusn(this.form.username)  && ispwd(this.form.password)) {
                        request.post("/changAn/employee/login", this.form).then(res => {
                            if (res.code === 1) {
                                // alert("hh")
                                this.$message({
                                    type: 'success',
                                    message: "successful login"
                                })
                                //在本地缓存用户信息
                                localStorage.setItem('userInfo',JSON.stringify(res.data));
                                //页面跳转
                                this.$router.push({path: '/Food'})
                            } else {
                                this.$message({
                                    type: 'error',
                                    message: res.msg
                                })
                            }
                        })
                        // this.logging = false
                        // let {msg,code,data} = data
                        // if (code != 200){
                        //     this.$message({
                        //         message : msg,
                        //         type:error
                        //     })
                        // }else{
                        //     if(self.data.code=1){
                        //         sessionStorage.setItem("user",JSON.stringify(user))
                        //         this.$router.push({ path:'/Employee'})
                        //     }else if (user.type ==='advert'){
                        //         sessionStorage.setItem("user",JSON.stringify(user))
                        //         this.$router.push({ path:'/Dish'})
                        //     }
                        // }
                        //             })
                        //             .catch( err => {
                        //                 console.log(err);
                        //                 // console.log("登录失败")
                        //             })
                    } else {
                        alert("请准确填写\n用户名：2~8位字母数字\n密码：6~12位，由字母数字下划线组成");
                    }
                }else{
                    alert("用户名和密码不能为空")
                }
            }
        }
    }
</script>

<style scoped="scoped">
    .login-register{
        width: 100vw;
        height: 100vh;
        box-sizing: border-box;
    }
    .contain{
        width: 60%;
        height: 60%;
        position: relative;
        top: 50%;
        left: 50%;
        transform: translate(-50%,-50%);
        background-color: #fff;
        border-radius: 20px;
        box-shadow: 0 0 3px #f0f0f0,
        0 0 6px #f0f0f0;
    }
    .big-box{
        width: 70%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 30%;
        transform: translateX(0%);
        transition: all 1s;
    }
    .big-contain{
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
    .btitle{
        font-size: 1.5em;
        font-weight: bold;
        color: rgb(57,167,176);
    }
    .bform{
        width: 100%;
        height: 40%;
        padding: 2em 0;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: center;
    }
    .bform .errTips{
        display: block;
        width: 50%;
        text-align: left;
        color: red;
        font-size: 0.7em;
        margin-left: 1em;
    }
    .bform input{
        width: 50%;
        height: 30px;
        border: none;
        outline: none;
        border-radius: 10px;
        padding-left: 2em;
        background-color: #f0f0f0;
    }
    .bbutton{
        width: 20%;
        height: 40px;
        border-radius: 24px;
        border: none;
        outline: none;
        background-color: rgb(57,167,176);
        color: #fff;
        font-size: 0.9em;
        cursor: pointer;
    }
    .small-box{
        width: 30%;
        height: 100%;
        background: linear-gradient(135deg,rgb(57,167,176),rgb(56,183,145));
        position: absolute;
        top: 0;
        left: 0;
        transform: translateX(0%);
        transition: all 1s;
        border-top-left-radius: inherit;
        border-bottom-left-radius: inherit;
    }
    .small-contain{
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
    .stitle{
        font-size: 1.5em;
        font-weight: bold;
        color: #fff;
    }
    .scontent{
        font-size: 0.8em;
        color: #fff;
        text-align: center;
        padding: 2em 4em;
        line-height: 1.7em;
    }
    .sbutton{
        width: 60%;
        height: 40px;
        border-radius: 24px;
        border: 1px solid #fff;
        outline: none;
        background-color: transparent;
        color: #fff;
        font-size: 0.9em;
        cursor: pointer;
    }
</style>