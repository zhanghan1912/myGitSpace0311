package com.stylefeng.guns.core.common.constant.state;

import com.stylefeng.guns.core.support.GeneralEnum;
import com.stylefeng.guns.core.util.EnumUtil;

/**
 * 活动计划类型
 */
public enum PlanType implements GeneralEnum{

    THEORY("theory", "理论教学"),
    CLINIC("clinic", "临床教学"),
    INVIGILATE("invigilate", "考核督导"),
    ACTIVITY("activity", "其他活动"),
    CIRCLE("circle", "中医全科轮训");

    String id;
    String name;

    PlanType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public static String getNameById(String id) {
        if (EnumUtil.getById(id, PlanType.class) != null) {
            return EnumUtil.getById(id, PlanType.class).getName();
        } else {
            return "";
        }
    }
}
