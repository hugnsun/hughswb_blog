<template>
    <el-container class="container-style">
        <el-row :gutter="100" style="width: 100%;padding-left 5%">
            <el-col
                :span="5"
                v-for="(item, index) of categoryList"
                :key="index"
            >
                <i :class="item.icon" />
                <el-tag effect="plain" style="margin-left: 22px">{{
                    item.categoryName
                }}</el-tag>
            </el-col>
        </el-row>
    </el-container>
</template>


<script>
export default {
    data: function () {
        return {
            categoryList: [],
        };
    },
    created() {
        this.getAllCategory();
    },
    mounted() {},
    methods: {
        getAllCategory() {
            this.axios
                .get("/api/oversimplification/allCategory")
                .then(({ data }) => {
                    this.categoryList = data.data;
                });
        },
    },
};
</script>
<style scoped>
.el-icon-alidiannao2 {
    font-size: 120px;
}
.el-icon-alijianshenqixie {
    font-size: 120px;
}
.el-icon-aliMoney {
    font-size: 120px;
}
.el-icon-alilvyouye {
    font-size: 120px;
}
.el-icon-alitubiaozhizuomoban- {
    font-size: 120px;
}
.container-style {
    padding: 8%;
}
.el-row /deep/ {
    padding-left: 10%;
    margin-top: 1%;
}
</style>