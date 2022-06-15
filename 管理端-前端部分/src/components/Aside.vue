<template>
    <div style="min-height: calc(100vh - 45px);">
        <el-radio-group  style="margin-bottom: 20px;margin-top: 10px">
            <el-radio-button size="small"  @click="toggleExpand">展开</el-radio-button>
            <el-radio-button size="small" @click="toggleCollapse">折叠</el-radio-button>
        </el-radio-group>
        <el-aside :width="isCollapse ? '55px' : '200px'" style="overflow: hidden;transition: width 1s">
            <el-menu
                    default-active="2"
                    :collapse="isCollapse"
                    :collapse-transition=true
                    router
                    @open="handleOpen"
                    @close="handleClose"
            >
                <el-menu-item index="order">
                   <span class="iconfont">&#xe897;</span>
                    <template #title >订单明细</template>
                </el-menu-item>
                <el-menu-item index="classification">
                    <span class="iconfont">&#xf00a6;</span>
                    <template #title>分类管理</template>
                </el-menu-item>
                <el-menu-item index="food">
                    <span class="iconfont">&#xe601;</span>
                    <template #title>菜品管理</template>
                </el-menu-item>
                <el-menu-item index="package">
                    <span class="iconfont">&#xf00e0;</span>
                    <template #title>套餐管理</template>
                </el-menu-item>
                <el-menu-item index="employee">
                    <span class="iconfont">&#xe72e;</span>
                    <template #title>员工管理</template>
                </el-menu-item>
                <el-menu-item index="6">
                    <span class="iconfont"></span>
                    <template #title></template>
                </el-menu-item>
                <el-menu-item index="7">

                </el-menu-item>
                <el-menu-item index="7">

                </el-menu-item>
                <el-menu-item index="7">

                </el-menu-item>
                <el-menu-item index="7">

                </el-menu-item>
            </el-menu>
        </el-aside>
    </div>
</template>

<script>
    export default {
        name: "Aside.vue",
        data: function() {
            return {
                // 是否折叠
                isCollapse: false,
            }
        },
        methods: {
            // 点击按钮切换菜单的折叠与展开
            toggleCollapse() {
                this.isCollapse = true;
            },
            toggleExpand(){
                this.isCollapse=false;
            }
        }
    }

</script>

<style scoped>


</style>