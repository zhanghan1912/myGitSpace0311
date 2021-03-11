package com.stylefeng.guns.modular.system.service;

import com.baomidou.mybatisplus.service.IService;
import com.stylefeng.guns.core.node.ZTreeNode;
import com.stylefeng.guns.modular.system.model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色相关业务
 *
 * @author fengshuonan
 */
public interface IRoleService extends IService<Role> {

    /**
     * 设置某个角色的权限
     *
     * @param roleId 角色id
     * @param ids    权限的id
     */
    void setAuthority(Integer roleId, String ids);

    /**
     * 删除角色
     *
     * @author stylefeng
     */
    void delRoleById(Integer roleId);

    /**
     * 根据条件查询角色列表
     *
     * @return
     */
    List<Map<String, Object>> selectRoles(@Param("condition") String condition);
    /**
     * 根据条件查询角色列表
     *
     * @return
     */
    List<Map<String, Object>> selectRolesByName(@Param("roleName") String roleName);

    /**
     * 删除某个角色的所有权限
     *
     * @param roleId 角色id
     * @return
     */
    int deleteRolesById(@Param("roleId") Integer roleId);

    /**
     * 获取角色列表树
     *
     * @return
     */
    List<ZTreeNode> roleTreeList();

    /**
     * 获取角色列表树
     *
     * @return
     */
    List<ZTreeNode> roleTreeListByRoleId(String[] roleId);
}
