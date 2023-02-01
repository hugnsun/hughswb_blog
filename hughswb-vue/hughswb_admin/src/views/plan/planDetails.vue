<template>
    <div class="execution">
        <basic-container>
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
            active2: false,
            tableOption: tableOption,
        };
    },

    methods: {
        getList(page, params) {
            console.log(page, params);
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