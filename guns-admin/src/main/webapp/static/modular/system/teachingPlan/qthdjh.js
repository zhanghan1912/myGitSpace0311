/**
 * 管理初始化
 */
var TeachingPlan = {
    id: "TeachingPlanTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
TeachingPlan.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: '主题名称', field: 'courseName', visible: true, align: 'center', valign: 'middle'},
        {title: '其他活动类型', field: 'activityName', visible: true, align: 'center', valign: 'middle'},
        {title: '时间', field: 'classTime', visible: true, align: 'center', valign: 'middle'},
        {title: '地点', field: 'classPlace', visible: true, align: 'center', valign: 'middle'},
    ];
};

/**
 * 检查是否选中
 */
TeachingPlan.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        TeachingPlan.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加
 */
TeachingPlan.openAddTeachingPlan = function () {
    var index = layer.open({
        type: 2,
        title: '添加',
        area: ['800px', '520px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/admin/qthdjh_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看详情
 */
TeachingPlan.openTeachingPlanDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '编辑',
            area: ['800px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/admin/qthdjh_update/' + TeachingPlan.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除
 */
TeachingPlan.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/admin/lljxjh/delete", function (data) {
            Feng.success("删除成功!");
            TeachingPlan.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("teachingPlanId",this.seItem.id);
        ajax.start();
    }
};
/**
 * 查询列表
 */
TeachingPlan.formParams = function() {
    var queryData = {};
    queryData['activityType'] = $("#activityType").val();
    queryData['courseName'] = $("#courseName").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    return queryData;
};
TeachingPlan.search = function () {
    $("#TeachingPlanTable").bootstrapTable('destroy');
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/admin/qthdjh/list", defaultColunms);
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
};
TeachingPlan.openImportTeachingPlan = function () {
    var index = layer.open({
        type: 2,
        title: '导入',
        area: ['600px', '300px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/admin/qthdjh_import'
    });
    this.layerIndex = index;
};
$(function () {
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/admin/qthdjh/list", defaultColunms);
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
});
