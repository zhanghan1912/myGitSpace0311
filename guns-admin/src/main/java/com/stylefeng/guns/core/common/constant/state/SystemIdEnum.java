package com.stylefeng.guns.core.common.constant.state;

import com.stylefeng.guns.core.support.GeneralEnum;
import com.stylefeng.guns.core.util.EnumUtil;

/**
 * 活动计划类型
 */
public enum SystemIdEnum implements GeneralEnum{

    Train("Train", "住培督导"),
    Education("Education", "学历督导")
    ;

    String id;
    String name;

    SystemIdEnum(String id, String name) {
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
        if (EnumUtil.getById(id, SystemIdEnum.class) != null) {
            return EnumUtil.getById(id, SystemIdEnum.class).getName();
        } else {
            return "";
        }
    }
}
