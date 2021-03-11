/**
 * 初始化详情对话框
 */
var TeachingPlanInfoDlg = {
    teachingPlanInfoData : {},
    validateFields: {
        citySel: {
            validators: {
                notEmpty: {
                    message: '部门不能为空'
                }
            }
        },
        courseName: {
            validators: {
                notEmpty: {
                    message: '主题名称不能为空'
                }
            }
        },
        activityType: {
            validators: {
                notEmpty: {
                    message: '轮转类型不能为空'
                }
            }
        }
    }
};
/**
 * 清除数据
 */
TeachingPlanInfoDlg.clearData = function() {
    this.teachingPlanInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TeachingPlanInfoDlg.set = function(key, val) {
    this.teachingPlanInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
TeachingPlanInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
TeachingPlanInfoDlg.close = function() {
    parent.layer.close(window.parent.TeachingPlan.layerIndex);
}

/**
 * 收集数据
 */
TeachingPlanInfoDlg.collectData = function() {
    this
    .set('id')
    .set('planType')
    .set('courseName')
    .set('activityType')
    .set('activityName')
    .set('deptId')
    .set('deptName')
    .set('classTime')
    .set('classPlace')
    .set('teachers')
    .set('className')
    .set('classNo')
    .set('classTarget')
    .set('classNumber')
    .set('classOrder')
    .set('detail')
    .set('memo')
    .set('createTime')
    .set('createUserFlow')
    .set('visiteFlag')
    .set('visiteTime')
    .set('visitorFlow')
    .set('visitorName');
}
/**
 * 验证数据是否为空
 */
TeachingPlanInfoDlg.validate = function () {
    $('#queryInfoForm').data("bootstrapValidator").resetForm();
    $('#queryInfoForm').bootstrapValidator('validate');
    return $("#queryInfoForm").data('bootstrapValidator').isValid();
};
/**
 * 提交添加
 */
TeachingPlanInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/admin/lzjh/add", function(data){
        Feng.success("添加成功!");
        window.parent.TeachingPlan.table.refresh();
        TeachingPlanInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.teachingPlanInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
TeachingPlanInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    if (!this.validate()) {
        return;
    }

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/admin/lzjh/update", function(data){
        Feng.success("修改成功!");
        window.parent.TeachingPlan.table.refresh();
        TeachingPlanInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.teachingPlanInfoData);
    ajax.start();
};
/**
 * 点击部门input框
 */
TeachingPlanInfoDlg.onClickDept = function (e, treeId, treeNode) {
    $("#citySel").attr("value", instance.getSelectedVal());
    $("#deptId").attr("value", treeNode.id);
};
/**
 * 显示部门选择的树
 */
TeachingPlanInfoDlg.showDeptSelectTree = function () {
    var cityObj = $("#citySel");
    var cityOffset = $("#citySel").offset();
    $("#menuContent").css({
        left: cityOffset.left + "px",
        top: cityOffset.top + cityObj.outerHeight() + "px"
    }).slideDown("fast");

    $("body").bind("mousedown", onBodyDown);
};
function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
            event.target).parents("#menuContent").length > 0)) {
        TeachingPlanInfoDlg.hideDeptSelectTree();
    }
}
/**
 * 隐藏部门选择的树
 */
TeachingPlanInfoDlg.hideDeptSelectTree = function () {
    $("#menuContent").fadeOut("fast");
    $("body").unbind("mousedown", onBodyDown);// mousedown当鼠标按下就可以触发，不用弹起
};
/**
 * 显示用户详情部门选择的树
 */
TeachingPlanInfoDlg.showInfoDeptSelectTree = function () {
    var cityObj = $("#citySel");
    var cityPosition = $("#citySel").position();
    $("#menuContent").css({
        left: cityPosition.left + "px",
        top: cityPosition.top + cityObj.outerHeight() + "px"
    }).slideDown("fast");

    $("body").bind("mousedown", onBodyDown);
};
$(function () {
    Feng.initValidator("queryInfoForm", TeachingPlanInfoDlg.validateFields);
    var ztree = new $ZTree("treeDemo", "/dept/tree");
    ztree.bindOnClick(TeachingPlanInfoDlg.onClickDept);
    ztree.init();
    instance = ztree;
});
function checkFile(file){
    var filePath = file.value;
    var suffix = filePath.substring(filePath.lastIndexOf(".")+1);
    if("xlsx" == suffix || "xls" == suffix){
        $("#checkFileFlag").val("Y");
    }else{
        $("#checkFileFlag").val("N");
        $(file).val(null);
        Feng.info("请上传Excel文件");
    }
}
function importExcel(planType){
    var checkFileFlag = $("#checkFileFlag").val();
    if('Y'!=checkFileFlag){
        Feng.info("请上传Excel文件");
        return false;
    }
    Feng.startLoading();
    var options = {
        url : Feng.ctxPath + "/admin/importPlanType?planType="+planType,
        type : "post",
        cache : false,
        success : function(data) {
            Feng.endLoading();
            //截掉<pre></pre>标签
            var start = data.indexOf(">");
            if (start != -1) {
                var end = data.indexOf("<", start + 1);
                if (end != -1) {
                    data = data.substring(start + 1, end);
                }
            }
            var obj = eval("("+data+")");
            var tip = "成功导入"+obj.succCount+"条记录";
            if(obj.loseCount > 0){
                tip += ",失败"+obj.loseCount+"条记录<br/>";
                for(var i=0;i<obj.loseList.length;i++){
                    var row = obj.loseList[i];
                    tip += "第"+row+"行"+obj.problemsMap[row];
                    if(i<obj.loseList.length-1){
                        tip += "<br/>"
                    }
                }
            }
            Feng.info(tip);
            window.parent.TeachingPlan.table.refresh();
        },
        error : function(data) {
            Feng.error("请求异常");
        },
        iframe : true
    };
    $('#excelForm').ajaxSubmit(options);
}