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
        {title: '序号', align: 'center',width:45 ,formatter: function (value, row, index) {return index+1;}},
        {title: '课程名称', field: 'course_name', visible: true, align: 'center', valign: 'middle'},
        {title: '课程类型', field: 'activity_name', visible: true, align: 'center', valign: 'middle'},
        {title: '任课老师', field: 'teachers', visible: true, align: 'center', valign: 'middle'},
        {title: '督导老师', field: 'visitors', visible: true, align: 'center', valign: 'middle'},
        {title: '老师评语', field: 'comments', visible: true, align: 'center', valign: 'middle',formatter:paramsMatter},
        {title: '时间', field: 'class_time', visible: true, align: 'center', valign: 'middle'},
        {title: '地点', field: 'class_place', visible: true, align: 'center', valign: 'middle'},
        {title: '操作', valign: 'middle',align: 'center',formatter:operateFormatter}
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
        var index = layer.open({
            type: 2,
            title: '查看',
            area: ['800px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/admin/ddpjcx_info/' + TeachingPlan.seItem.id + '/' + TeachingPlan.seItem.plan_type
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
    queryData['visitorName'] = $("#visitorName").val();
    queryData['classPlace'] = $("#classPlace").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    return queryData;
};

TeachingPlan.search = function () {
    $("#TeachingPlanTable").bootstrapTable('destroy');
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/admin/ddpjcx/list", defaultColunms);
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
};

$(function () {
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/admin/ddpjcx/list", defaultColunms);
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
});

/**
 * 鼠标悬停展示信息
 */
//function paramsMatter(value, row, index) {
//    var comments = row.comments;
//    var visitors = row.visitors;
//    var titleText=document.createElement("table");
//    var text=document.createElement("tr");
//    text.innerHTML = "<th style='width: 80px;'>督导老师</th><th style='width: 220px;'>评语</th>";
//    titleText.appendChild(text);
//    if(comments!=undefined&&comments!='undefined'&&visitors!=undefined&&visitors!='undefined'){
//        var arr1=comments.split(",");
//        var arr2=visitors.split(",");
//        if(arr2!=null&&arr2.length>0){
//            for(var i=0;i<arr2.length;i++){
//                text.innerHTML = "<td>"+arr2[i]+"</td><td>"+arr1[i]+"</td>";
//                titleText.appendChild(text);
//            }
//        }
//    }
//    //titleText="<table>"+titleText+"</table>";
//    return "<span title="+titleText+">"+visitors+"</span>";
//}
function paramsMatter(value, row, index) {
    var comments = row.comments;
    var titleText="评语内容：\n";
    if(comments!=undefined&&comments!='undefined'&&comments!=''&&comments!=null){
        return "<span title="+comments+">"+comments+"</span>";
    }
}

function operateFormatter(value, row, index) {
    if(row.comments!=undefined&&row.comments!='undefined'){
        return [ '<a style="color: #337dcb;cursor: pointer;" onClick="batchPrint(\'' + row.id + '\')" title="导出评价表">导出</a>',].join('');
    }else{
        return [ '<a style="color: black;cursor: pointer;" onClick="noFormPrint()" title="导出评价表">导出</a>',].join('');
    }
}
TeachingPlan.exportDdpj = function () {
    Feng.info("导出中······");
    Feng.startLoading();
    window.location.href=Feng.ctxPath + "/admin/exportDdpjList?planType="+$("#planType").val()
        +"&courseName="+$("#courseName").val()+"&teachers="+$("#teachers").val()
        +"&visitorName="+$("#visitorName").val()+"&classPlace="+$("#classPlace").val()
        +"&beginTime="+$("#beginTime").val()+"&endTime="+$("#endTime").val()+"&roleFlag=admin";
    Feng.endLoading();
};

function batchPrint(id) {
    Feng.info("导出中······");
    Feng.startLoading();
    window.location.href=Feng.ctxPath + "/admin/batchPrint?planId="+id;
    Feng.endLoading();
};
function noFormPrint() {
    Feng.info("暂无评价表！");
};