package com.stylefeng.guns.modular.system.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 系统设置表
 * </p>
 *
 * @author stylefeng
 * @since 2018-07-18
 */
@TableName("sys_cfg")
public class Cfg extends Model<Cfg> {

    private static final long serialVersionUID = 1L;

    /**
     * 设置id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 参数
     */
    @TableField("cfg_code")
    private String cfgCode;
    /**
     * 内容
     */
    @TableField("cfg_value")
    private String cfgValue;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCfgCode() {
        return cfgCode;
    }

    public void setCfgCode(String cfgCode) {
        this.cfgCode = cfgCode;
    }

    public String getCfgValue() {
        return cfgValue;
    }

    public void setCfgValue(String cfgValue) {
        this.cfgValue = cfgValue;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Cfg{" +
        "id=" + id +
        ", cfgCode=" + cfgCode +
        ", cfgValue=" + cfgValue +
        "}";
    }
}
