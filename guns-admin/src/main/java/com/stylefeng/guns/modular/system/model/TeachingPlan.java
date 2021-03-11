package com.stylefeng.guns.modular.system.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 计划活动表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-13
 */
@TableName("sys_teaching_plan")
public class TeachingPlan extends Model<TeachingPlan> {

    private static final long serialVersionUID = 1L;

    /**
     * 计划活动编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 计划类型
     */
    @TableField("plan_type")
    private String planType;
    /**
     * 课程名称
     */
    @TableField("course_name")
    private String courseName;
    /**
     * 活动类型
     */
    @TableField("activity_type")
    private String activityType;
    /**
     * 活动名称
     */
    @TableField("activity_name")
    private String activityName;
    /**
     * 部门id
     */
    @TableField("dept_id")
    private Integer deptId;
    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;
    /**
     * 时间
     */
    @TableField("class_time")
    private String classTime;
    /**
     * 地点
     */
    @TableField("class_place")
    private String classPlace;
    /**
     * 任课老师
     */
    private String teachers;
    /**
     * 上课班级名称
     */
    @TableField("class_name")
    private String className;
    /**
     * 上课班号
     */
    @TableField("class_no")
    private String classNo;
    /**
     * 上课班级构成
     */
    @TableField("class_target")
    private String classTarget;
    /**
     * 上课人数
     */
    @TableField("class_number")
    private String classNumber;
    /**
     * 节次
     */
    @TableField("class_order")
    private String classOrder;
    /**
     * 内容详情
     */
    private String detail;
    /**
     * 备注
     */
    private String memo;
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
     * 教师职称
     */
    @TableField("teacher_title")
    private String teacherTitle;
    /**
     * 章节
     */
    private String chapter;
    /**
     * 开课单位
     */
    @TableField("course_unit")
    private String courseUnit;
    /**
     * 学时
     */
    @TableField("school_hours")
    private String schoolHours;
    /**
     * 查房病种
     */
    private String disease;
    /**
     * 床号
     */
    @TableField("bed_number")
    private String bedNumber;
    /**
     * 住院号
     */
    @TableField("inpatient_number")
    private String inpatientNumber;
    /**
     * 病例编号
     */
    @TableField("case_number")
    private String caseNumber;
    /**
     * 记录人
     */
    @TableField("note_taker")
    private String noteTaker;
    /**
     * 参与人员
     */
    private String participants;
    /**
     * 系统id
     */
    private String systemId;
    /**
     * 系统name
     */
    private String systemName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassPlace() {
        return classPlace;
    }

    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }

    public String getTeachers() {
        return teachers;
    }

    public void setTeachers(String teachers) {
        this.teachers = teachers;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }

    public String getClassTarget() {
        return classTarget;
    }

    public void setClassTarget(String classTarget) {
        this.classTarget = classTarget;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getClassOrder() {
        return classOrder;
    }

    public void setClassOrder(String classOrder) {
        this.classOrder = classOrder;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public String getTeacherTitle() {
        return teacherTitle;
    }

    public void setTeacherTitle(String teacherTitle) {
        this.teacherTitle = teacherTitle;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getCourseUnit() {
        return courseUnit;
    }

    public void setCourseUnit(String courseUnit) {
        this.courseUnit = courseUnit;
    }

    public String getSchoolHours() {
        return schoolHours;
    }

    public void setSchoolHours(String schoolHours) {
        this.schoolHours = schoolHours;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getInpatientNumber() {
        return inpatientNumber;
    }

    public void setInpatientNumber(String inpatientNumber) {
        this.inpatientNumber = inpatientNumber;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getNoteTaker() {
        return noteTaker;
    }

    public void setNoteTaker(String noteTaker) {
        this.noteTaker = noteTaker;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "TeachingPlan{" +
        "id=" + id +
        ", planType=" + planType +
        ", courseName=" + courseName +
        ", activityType=" + activityType +
        ", activityName=" + activityName +
        ", deptId=" + deptId +
        ", deptName=" + deptName +
        ", classTime=" + classTime +
        ", classPlace=" + classPlace +
        ", teachers=" + teachers +
        ", className=" + className +
        ", classNo=" + classNo +
        ", classTarget=" + classTarget +
        ", classNumber=" + classNumber +
        ", classOrder=" + classOrder +
        ", detail=" + detail +
        ", memo=" + memo +
        ", createTime=" + createTime +
        ", createUserId=" + createUserId +
        ", teacherTitle=" + teacherTitle +
        ", chapter=" + chapter +
        ", courseUnit=" + courseUnit +
        ", schoolHours=" + schoolHours +
        ", disease=" + disease +
        ", bedNumber=" + bedNumber +
        ", inpatientNumber=" + inpatientNumber +
        ", caseNumber=" + caseNumber +
        ", noteTaker=" + noteTaker +
        ", participants=" + participants +
        ", systemId=" + systemId +
        ", systemName=" + systemName +
        "}";
    }
}
