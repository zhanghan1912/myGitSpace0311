/**
 * 初始化详情对话框
 */
var TeachingPlanInfoDlg = {
    teachingPlanInfoData : {}
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
        .set('evaluateType')
        .set('scoreItem1')
        .set('scoreItem2')
        .set('scoreItem3')
        .set('scoreItem4')
        .set('scoreItem5')
        .set('scoreItem6')
        .set('scoreItem7')
        .set('scoreItem8')
        .set('scoreItem9')
        .set('scoreItem10')
        .set('scoreItem11')
        .set('scoreItem12')
        .set('scoreItem13')
        .set('scoreItem14')
        .set('scoreItem15')
        .set('grade')
        .set('comment')
        .set('proposal')
        .set('better')
        .set('pjbsjyj')
        .set('ddsj')
        .set('imgUrl1')
        .set('imgUrl2')
        .set('imgUrl3')
        .set('imgUrl4')
        .set('imgUrl5')
        .set('imgUrl6')
        .set('imgUrl7')
        .set('imgUrl8')
        .set('imgUrl9');
}
/**
 * 验证数据是否为空
 */
TeachingPlanInfoDlg.validate = function () {
    //$('#queryInfoForm').data("bootstrapValidator").resetForm();
    $('#queryInfoForm').bootstrapValidator('validate');
    return $("#queryInfoForm").data('bootstrapValidator').isValid();
};
/**
 * 提交添加
 */
TeachingPlanInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/visitor/saveFormScore", function(data){
        if(data.code==-1){
            Feng.error("保存失败!" + data.message + "!");
        }else{
            Feng.success("保存成功!");
            window.parent.TeachingPlan.table.refresh();
            TeachingPlanInfoDlg.close();
        }
    },function(data){
        Feng.error("保存失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.teachingPlanInfoData);
    ajax.start();
}

TeachingPlanInfoDlg.checkInputScore = function () {
    $("input[name^='scoreItem']").each(function(){
        var score = $(this).val();
        var maxNum = $(this).attr("maxNum");
        var reg=/^(0|([1-9]\d*))(\.\d+)?$/;
        if($.trim(score)!=""){
            if(!reg.test(score)){
                Feng.error("请输入正确的数字!");
                $(this).val("");
                $(this).focus();
            }else if(Number(score)>Number(maxNum)){
                Feng.error("输入分值不能大于"+maxNum+"!");
                $(this).val("");
                $(this).focus();
            }
        }
    });
    TeachingPlanInfoDlg.loadToalScore();
}

TeachingPlanInfoDlg.loadToalScore = function () {
    var count = 0;
    $("input[name^='scoreItem']").each(function(){
        var score = $(this).val();
        if($.trim(score)!="" && !isNaN(score)){
            count+=parseFloat(score);
        }
    });
    var score=count.toFixed(1);
    if(score>=90){
        var text="<font color='green'>"+score+"</font>";
    }else if(score>=80){
        var text="<font color='blue'>"+score+"</font>";
    }else if(score>=70){
        var text="<font color='#ff8c00'>"+score+"</font>";
    }else{
        var text="<font color='red'>"+score+"</font>";
    }
    $("#totalScoreSpan").html(text);
    //等级自动绑定
    var select=document.getElementById("grade");
    if(select!=null||select!=undefined){

        var num=select.options.length;
        if(num<4){
            if(score<90){
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='不通过']").prop("selected",true);
            }else{
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='通过']").prop("selected",true);
            }
        }else{
            if(90<=score && score<=100){
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='优']").prop("selected",true);
            }else if(80<=score && score<90){
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='良']").prop("selected",true);
            }else if(70<=score && score<80){
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='中']").prop("selected",true);
            }else if(60<=score && score<70){
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='及格']").prop("selected",true);
            }else{
                $("#grade").find("option:selected").prop("selected",false);
                $("#grade").find("option[value='不及格']").prop("selected",true);
            }
        }
    }
}

/*
$(function () {
    //Feng.initValidator("queryInfoForm", TeachingPlanInfoDlg.validateFields);
    TeachingPlanInfoDlg.loadToalScore();
});*/



TeachingPlanInfoDlg.printDdpj = function () {
    Feng.info("导出中······");
    Feng.startLoading();
    window.location.href=Feng.ctxPath + "/admin/print?planId="+$("#planId").val()+"&id="+$("#visitorId").val();
    Feng.endLoading();
};