package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户模块 MCP Tool Adapter
 *
 * 提供用户管理的 MCP Tools
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserToolAdapter {

    private final AdminUserApi adminUserApi;

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户编号
     * @return 用户详情(包含用户编号、昵称、部门ID、岗位ID列表、手机号、头像、状态)
     */
    @Tool(name = "system_admin_user_get", description = "根据用户ID获取管理员用户的详细信息。返回用户编号、昵称、所属部门ID、岗位编号列表、手机号、头像URL、账号状态等信息。适用于查看具体用户详情、校验用户存在性等场景。")
    public AdminUserRespDTO userGet(@ToolParam(description = "用户编号(ID)", required = true) Long id) {
        log.info("[MCP Tool] system_admin_user_get called with id={}", id);
        try {
            return adminUserApi.getUser(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_admin_user_get", e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID列表批量获取用户信息
     *
     * @param ids 用户编号列表
     * @return 用户信息列表
     */
    @Tool(name = "system_admin_user_list", description = "根据用户ID列表批量获取管理员用户信息。返回每个用户的编号、昵称、所属部门ID、岗位编号列表、手机号、头像URL、账号状态等完整信息。适用于批量查询用户、构建用户选择器等场景。")
    public List<AdminUserRespDTO> userList(@ToolParam(description = "用户编号列表", required = true) List<Long> ids) {
        log.info("[MCP Tool] system_admin_user_list called with ids={}", ids);
        try {
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }
            return adminUserApi.getUserList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_admin_user_list", e);
            throw new RuntimeException("批量获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据部门ID获取该部门下的所有用户
     *
     * @param deptId 部门编号
     * @return 部门下的用户列表
     */
    @Tool(name = "system_admin_user_list_by_dept", description = "根据部门ID获取该部门下的所有管理员用户。返回该部门及其子部门下所有用户的编号、昵称、所属部门ID、岗位编号列表、手机号、头像URL、账号状态等完整信息。适用于查看部门成员、构建部门用户列表等场景。")
    public List<AdminUserRespDTO> userListByDept(@ToolParam(description = "部门编号", required = true) Long deptId) {
        log.info("[MCP Tool] system_admin_user_list_by_dept called with deptId={}", deptId);
        try {
            return adminUserApi.getUserListByDeptIds(Collections.singletonList(deptId)).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_admin_user_list_by_dept", e);
            throw new RuntimeException("获取部门用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户简单列表(仅返回ID和昵称)
     *
     * @param ids 用户编号列表
     * @return 用户简单信息列表(仅包含编号和昵称)
     */
    @Tool(name = "system_admin_user_simple_list", description = "获取用户简单列表,仅返回用户编号和昵称。适用于下拉选择器、用户mention等只需要基本信息的轻量级场景。相比user_list返回更少的数据,性能更好。")
    public List<Map<String, Object>> userSimpleList(@ToolParam(description = "用户编号列表", required = true) List<Long> ids) {
        log.info("[MCP Tool] system_admin_user_simple_list called with ids={}", ids);
        try {
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }
            List<AdminUserRespDTO> users = adminUserApi.getUserList(ids).getCheckedData();
            return users.stream()
                    .map(user -> {
                        Map<String, Object> simpleUser = new java.util.LinkedHashMap<>();
                        simpleUser.put("id", user.getId());
                        simpleUser.put("nickname", user.getNickname());
                        return simpleUser;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_admin_user_simple_list", e);
            throw new RuntimeException("获取用户简单列表失败: " + e.getMessage());
        }
    }
}
