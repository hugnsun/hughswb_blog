<template>
    <div class="execution">
        <basic-container id="style-container">
            <avue-crud
                ref="crud"
                :page.sync="page"
                :data="tableData"
                :table-loading="tableLoading"
                :option="tableOption"
                @on-load="getList"
                @search-change="searchChange"
                @refresh-change="refreshChange"
                @size-change="sizeChange"
                @current-change="currentChange"
                @row-update="handleUpdate"
                @row-save="handleSave"
                @row-del="rowDel"
            >
                <template slot="menuLeft">
                    <vs-button
                        upload
                        animation-type="vertical"
                        style="width: 100px"
                        @click="$refs.crud.rowAdd()"
                    >
                        <i class="el-icon-alitianjia1" /> 新增计划
                        <template #animate
                            ><i class="el-icon-alichukong" /> 点击新增
                        </template>
                    </vs-button>
                </template>
                <template slot="delayOrNot" slot-scope="{ row }">
                    <vs-switch danger v-model="row.delayOrNot" disabled>
                        <template #off> 未延期 </template>
                        <template #on> 延期 </template>
                    </vs-switch>
                </template>
                <template slot="whetherToUploadNotes" slot-scope="{ row }">
                    <vs-switch v-model="row.whetherToUploadNotes" disabled>
                        <template #off> 未上传 </template>
                        <template #on> 已上传 </template>
                    </vs-switch>
                </template>
                <template slot="menu">
                    <div style="display: flex">
                        <vs-button upload color="dark" transparent icon>
                            <i class="el-icon-alixiugai" />修改
                        </vs-button>
                        <vs-button upload color="danger" border icon>
                            <i class="el-icon-alishanchu" />删除
                        </vs-button>
                    </div>
                </template>
            </avue-crud>
        </basic-container>
    </div>
</template>

<script>
import { tableOption } from "./coust/planDetailOption";

export default {
    name: "plandetails",
    data() {
        return {
            searchForm: {},
            tableData: [],
            page: {
                total: 0, // 总页数
                currentPage: 1, // 当前页数
                pageSize: 20, // 每页显示多少条
            },
            tableLoading: false,

            tableOption: tableOption,
        };
    },

    methods: {
        getList(page, params) {
            this.axios
                .get("/api/plan-details/page", { page, params })
                .then(({ data }) => {
                    this.tableData = data.data.records;
                    this.$notify({
                        title: "成功",
                        message: "数据查询成功",
                        type: "success",
                    });
                })
                .catch(() => {
                    this.$notify.error({
                        title: "错误",
                        message: "查询出现问题 请查询后端代码",
                    });
                });
        },
        rowDel: function (row, index) {
            console.log(row, index);
        },
        handleUpdate: function (row, index, done, loading) {
            console.log(row, index, done, loading);
        },
        handleSave: function (row, done, loading) {
            console.log(row, done, loading);
        },
        sizeChange(pageSize) {
            this.page.pageSize = pageSize;
        },
        currentChange(current) {
            this.page.currentPage = current;
        },
        searchChange(form, done) {
            this.searchForm = form;
            this.page.currentPage = 1;
            this.getList(this.page, form);
            done();
        },
        refreshChange() {
            this.getList(this.page);
        },
    },
};
</script>

 <style lang="scss" scoped>
.el-icon-alitianjia1 {
    padding-right: 5px;
}
.el-icon-alichukong {
    padding-right: 5px;
}
.button {
    display: inline-block;
}
</style>
