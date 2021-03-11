/**
 * 初始化详情对话框
 */
var CfgInfoDlg = {
    CfgInfoData : {}
};
/**
 * 清除数据
 */
CfgInfoDlg.clearData = function() {
    this.CfgInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CfgInfoDlg.set = function(key, val) {
    this.CfgInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
CfgInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
CfgInfoDlg.close = function() {
    parent.layer.close(window.parent.TeachingPlan.layerIndex);
}

/**
 * 收集数据
 */
CfgInfoDlg.collectData = function() {
    this
        .set('theoryCfgId')
        .set('theoryCfgValue')
        .set('clinicCfgId')
        .set('clinicCfgValue')
        .set('invigilateCfgId')
        .set('invigilateCfgValue')
        .set('circleCfgId')
        .set('circleCfgValue')
        .set('ddpjsjCfgId')
        .set('ddpjsjCfgValue')
        .set('uploadBaseDirId')
        .set('uploadBaseDirValue')
        .set('uploadBaseUrlId')
        .set('uploadBaseUrlValue')
        .set('uploadBaseUrlXcxId')
        .set('uploadBaseUrlXcxValue');
}
/**
 * 验证数据是否为空
 */
CfgInfoDlg.validate = function () {
    //$('#queryInfoForm').data("bootstrapValidator").resetForm();
    $('#queryInfoForm').bootstrapValidator('validate');
    return $("#queryInfoForm").data('bootstrapValidator').isValid();
};
/**
 * 提交添加
 */
CfgInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    $("#queryInfoForm").bootstrapValidator('validate');//提交验证
    if ($("#queryInfoForm").data('bootstrapValidator').isValid()) {//获取验证结果，如果成功，执行下面代码
        //提交信息
        var ajax = new $ax(Feng.ctxPath + "/syscfg/save", function(data){
            Feng.success("保存成功!");
            window.location.reload();
        },function(data){
            Feng.error("保存失败!" + data.responseJSON.message + "!");
        });
        ajax.set(this.CfgInfoData);
        ajax.start();
    }
}
$(function () {
    $("#queryInfoForm").bootstrapValidator({
        live: 'disabled',
        excluded: [':disabled', ':hidden', ':not(:visible)'],
        message: '数据类型不符合',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            theoryCfgValue: {
                validators: {
                    regexp: { //正则验证
                        regexp: /^[1-9]\d*$/,
                        message: '人数只能为整数'
                    }
                }
            },
            clinicCfgValue: {
                validators: {
                    regexp: { //正则验证
                        regexp: /^[1-9]\d*$/,
                        message: '人数只能为整数'
                    }
                }
            },
            invigilateCfgValue: {
                validators: {
                    regexp: { //正则验证
                        regexp: /^[1-9]\d*$/,
                        message: '人数只能为整数'
                    }
                }
            },
            circleCfgValue: {
                validators: {
                    regexp: { //正则验证
                        regexp: /^[1-9]\d*$/,
                        message: '人数只能为整数'
                    }
                }
            },
            ddpjsjCfgValue: {
                validators: {
                    regexp: { //正则验证
                        regexp: /^[1-9]\d*$/,
                        message: '天数只能为整数'
                    }
                }
            }
        }
    });
});
