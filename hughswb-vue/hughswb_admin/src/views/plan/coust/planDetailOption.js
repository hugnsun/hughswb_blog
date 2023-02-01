export const tableOption = {
  "border": true,
  "index": true,
  "indexLabel": "序号",
  "stripe": true,
  "menuAlign": "center",
  "align": "center",
  "searchMenuSpan": 6,
  addBtn:false,
  "column": [
	 {
      "type": "select",
      "label": "计划分类",
      "prop": "categoryId",
      "span": 12,
      rules: [{
        required: true,
        message: "必须选择计划分类",
        trigger: "blur"
      }]
    },	  {
      "type": "input",
      "label": "计划名称",
      "prop": "planName",
      "span": 12,
      rules: [{
        required: true,
        message: "必须输入计划名称",
        trigger: "blur"
      }]
    },	  {
      "type": "datetime",
      "label": "开始时间",
      "prop": "startTime",
      "span": 12,
      rules: [{
        required: true,
        message: "必须选择开始时间",
        trigger: "blur"
      }]
    },	  {
      "type": "datetime",
      "label": "结束时间",
      "prop": "plannedEndTime",
      "span": 12,
      rules: [{
        required: true,
        message: "必须选择结束时间",
        trigger: "blur"
      }]
    },	  {
      "type": "input",
      "label": "是否延期",
      "prop": "delayOrNot",
      "span": 12,
      display:false,
      slot:true,
    },	  {
      "type": "input",
      "label": "上传笔记",
      "prop": "whetherToUploadNotes",
      "span": 12,
      display:false,
      slot:true,
    },{
      "type": "input",
      "label": "学习路径",
      "prop": "learningPath",
      "span": 12
    },	 {
      "type": "input",
      "label": "当前进度",
      "prop": "currentProgress",
      "span": 12,
      display:false
    },	  {
      "type": "input",
      "label": "当前状态",
      "prop": "status",
      "span": 12,
      display:false
    }  ,  {
      "type": "input",
      "label": "备注",
      "prop": "note",
      "span": 12
    }]
}
