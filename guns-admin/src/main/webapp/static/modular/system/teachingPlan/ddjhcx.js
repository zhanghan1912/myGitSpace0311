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
        {title: '序号',width:'70px',align: 'center',valign: 'middle',formatter: function(value, row, index) {return index + 1;}},
        {title: '课程名称', field: 'courseName', visible: true, align: 'center', valign: 'middle'},
        {title: '课程类型', field: 'activityName', visible: true, align: 'center', valign: 'middle'},
        {title: '任课老师', field: 'teachers', visible: true, align: 'center', valign: 'middle'},
        {title: '时间', field: 'classTime', visible: true, align: 'center', valign: 'middle'},
        {title: '地点', field: 'classPlace', visible: true, align: 'center', valign: 'middle'}
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
 * 打开查看详情
 */
TeachingPlan.openTeachingPlanDetail = function () {
    if (this.check()) {
        console.log(TeachingPlan.seItem);
        var index = layer.open({
            type: 2,
            title: '查看',
            area: ['800px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/admin/ddjhcx_info/' + TeachingPlan.seItem.id + '/' + TeachingPlan.seItem.planType
        });
        this.layerIndex = index;
    }
};

/**
 * 查询列表
 */
TeachingPlan.formParams = function() {
    var queryData = {};
    queryData['planType'] = $("#planType").val();
    queryData['activityType'] = $("#activityType").val();
    queryData['courseName'] = $("#courseName").val();
    queryData['teachers'] = $("#teachers").val();
    queryData['classPlace'] = $("#classPlace").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    return queryData;
};

TeachingPlan.search = function () {
    $("#TeachingPlanTable").bootstrapTable('destroy');
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/admin/ddjhcx/list", defaultColunms);
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
};

$(function () {
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/admin/ddjhcx/list", defaultColunms);
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
});
