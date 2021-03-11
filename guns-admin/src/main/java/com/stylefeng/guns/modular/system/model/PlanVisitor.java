package com.stylefeng.guns.modular.system.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import com.mysql.cj.jdbc.Blob;

/**
 * <p>
 * 计划活动参与督导表
 * </p>
 *
 * @author stylefeng
 * @since 2019-09-10
 */
@TableName("sys_plan_visitor")
public class PlanVisitor extends Model<PlanVisitor> {

    private static final long serialVersionUID = 1L;

    /**
     * 督导计划id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 计划活动编号
     */
    @TableField("plan_id")
    private Integer planId;
    /**
     * 参加督导标识
     */
    @TableField("visite_flag")
    private String visiteFlag;
    /**
     * 督导id
     */
    @TableField("visitor_id")
    private Integer visitorId;
    /**
     * 督导名称
     */
    @TableField("visitor_name")
    private String visitorName;
    /**
     * 评价详情
     */
    @TableField("evaluate_score")
    private String evaluateScore;
    /**
     * 创建日期
     */
    @TableField("create_time")
    private String createTime;
    /**
     * 创建人id
     */
    @TableField("create_user_id")
    private Integer createUserId;
    /**
     * 理论计划类型
     */

    @TableField("evaluate_type")
    private String evaluateType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getVisiteFlag() {
        return visiteFlag;
    }

    public void setVisiteFlag(String visiteFlag) {
        this.visiteFlag = visiteFlag;
    }

    public Integer getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(Integer visitorId) {
        this.visitorId = visitorId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getEvaluateType() {
        return evaluateType;
    }

    public void setEvaluateType(String evaluateType) {
        this.evaluateType = evaluateType;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PlanVisitor{" +
        "id=" + id +
        ", planId=" + planId +
        ", visiteFlag=" + visiteFlag +
        ", visitorId=" + visitorId +
        ", visitorName=" + visitorName +
        ", evaluateScore=" + evaluateScore +
        ", createTime=" + createTime +
        ", createUserId=" + createUserId +
        ", evaluateType=" + evaluateType +
        "}";
    }
}
