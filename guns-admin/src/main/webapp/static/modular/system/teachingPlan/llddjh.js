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
        {title: '课程名称', field: 'course_name', visible: true, align: 'center', valign: 'middle'},
        {title: '理论教学类型', field: 'activity_name', visible: true, align: 'center', valign: 'middle'},
        {title: '活动时间', field: 'class_time', visible: true, align: 'center', valign: 'middle'},
        {title: '活动地点', field: 'class_place', visible: true, align: 'center', valign: 'middle'},
        {title: '督导人员', field: 'visitors', visible: true, align: 'center', valign: 'middle'},
        {title: '科室', field: 'dept_name', visible: true, align: 'center', valign: 'middle'}
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
 * 点击参与督导
 */
TeachingPlan.joinPlan = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/visitor/llddjh/joinPlan", function (data) {
            if(data.code==-1){
                Feng.error("参与失败!" + data.message + "!");
            }else{
                Feng.success("参与成功!");
            }
            TeachingPlan.table.refresh();
        }, function (data) {
            Feng.error("参与失败!" + data.responseJSON.message + "!");
        });
        ajax.set("teachingPlanId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 点击取消督导
 */
TeachingPlan.cancelPlan = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/visitor/llddjh/cancelPlan", function (data) {
            if(data.code==-1||data.code==0){
                Feng.error("取消失败!" + data.message + "!");
            }else{
                Feng.success("取消成功!");
            }
            TeachingPlan.table.refresh();
        }, function (data) {
            Feng.error("取消失败!" + data.responseJSON.message + "!");
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
    queryData['deptId'] = $("#deptId").val();
    return queryData;
};
TeachingPlan.search = function () {
    $("#TeachingPlanTable").bootstrapTable('destroy');
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/visitor/llddjh/list", defaultColunms);
    table.setPaginationType("server");
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
};

$(function () {
    var defaultColunms = TeachingPlan.initColumn();
    var table = new BSTable(TeachingPlan.id, "/visitor/llddjh/list", defaultColunms);
    table.setPaginationType("server");
    table.setQueryParams(TeachingPlan.formParams());
    TeachingPlan.table = table.init();
    TeachingPlan.loadDept();
    //var ztree = new $ZTree("treeDemo", "/dept/tree");
    //ztree.bindOnClick(TeachingPlan.onClickDept);
    //ztree.init();
    //instance = ztree;
});

/**
 * 加载科室数据
 */
TeachingPlan.loadDept = function () {
    var ajax = new $ax(Feng.ctxPath + "/dept/tree", function (data) {
        var len = data.length;
        var optionString = "<option value=\'\'></option>";
        for (i = 0; i < len; i++) {
            if(data[i].name!="顶级"){
                optionString += "<option value=\'"+ data[i].id +"\'>" + data[i].name + "</option>";
            }
        }
        var myobj = document.getElementById("deptId");
        if (myobj.options.length == 0)
        {
            $("#deptId").html(optionString);
        }
    }, function (data) {
    });
    ajax.start();
};

/**
 * 检查是否参与督导
 */
TeachingPlan.checkJoin = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/visitor/checkJoin", function (data) {
            if(data.code==-1){
                Feng.error(data.message + "!");
            }else{
                TeachingPlan.openEvaluationForm();
            }
        }, function (data) {
            Feng.error(data.responseJSON.message + "!");
        });
        ajax.set("teachingPlanId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 打开督导评价表格
 */
TeachingPlan.openEvaluationForm = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '评价表',
            area: ['800px', '520px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/visitor/evaluationform/' + TeachingPlan.seItem.id
        });
        this.layerIndex = index;
    }
};
///**
// * 点击部门input框
// */
//TeachingPlan.onClickDept = function (e, treeId, treeNode) {
//    $("#citySel").attr("value", instance.getSelectedVal());
//    $("#deptId").attr("value", treeNode.id);
//};
///**
// * 显示部门选择的树
// */
//TeachingPlan.showDeptSelectTree = function () {
//    var cityObj = $("#citySel");
//    var cityOffset = $("#citySel").offset();
//    $("#menuContent").css({
//        left: cityOffset.left + "px",
//        top: cityOffset.top + cityObj.outerHeight() + "px"
//    }).slideDown("fast");
//
//    $("body").bind("mousedown", onBodyDown);
//};
//function onBodyDown(event) {
//    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
//            event.target).parents("#menuContent").length > 0)) {
//        TeachingPlan.hideDeptSelectTree();
//    }
//}
///**
// * 隐藏部门选择的树
// */
//TeachingPlan.hideDeptSelectTree = function () {
//    $("#menuContent").fadeOut("fast");
//    $("body").unbind("mousedown", onBodyDown);// mousedown当鼠标按下就可以触发，不用弹起
//};

