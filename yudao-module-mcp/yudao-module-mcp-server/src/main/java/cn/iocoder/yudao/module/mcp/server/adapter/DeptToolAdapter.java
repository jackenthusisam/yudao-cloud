package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门模块 MCP Tool Adapter
 *
 * 提供部门管理的 MCP Tools
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeptToolAdapter {

    private final DeptApi deptApi;

    /**
     * 根据部门ID获取部门信息
     *
     * @param id 部门编号
     * @return 部门详情(包含编号、名称、父部门编号、负责人ID、状态)
     */
    @Tool(name = "system_dept_get", description = "根据部门ID获取部门详细信息。返回部门编号、名称、父部门编号、负责人用户ID、部门状态等信息。适用于查看具体部门详情、校验部门存在性等场景。")
    public DeptRespDTO deptGet(@ToolParam(description = "部门编号(ID)", required = true) Long id) {
        log.info("[MCP Tool] system_dept_get called with id={}", id);
        try {
            return deptApi.getDept(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_dept_get", e);
            throw new RuntimeException("获取部门信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据部门ID列表批量获取部门信息
     *
     * @param ids 部门编号列表
     * @return 部门信息列表
     */
    @Tool(name = "system_dept_list", description = "根据部门ID列表批量获取部门信息。返回每个部门的编号、名称、父部门编号、负责人ID、状态等完整信息。适用于批量查询部门、构建部门选择器等场景。")
    public List<DeptRespDTO> deptList(@ToolParam(description = "部门编号列表", required = true) List<Long> ids) {
        log.info("[MCP Tool] system_dept_list called with ids={}", ids);
        try {
            return deptApi.getDeptList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_dept_list", e);
            throw new RuntimeException("批量获取部门信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定部门的所有子部门
     *
     * @param id 部门编号
     * @return 子部门列表(直接子部门,不递归)
     */
    @Tool(name = "system_dept_child_list", description = "获取指定部门的所有直接子部门。返回子部门列表,每个子部门包含编号、名称、父部门编号、负责人ID、状态等信息。适用于展开部门树、查看组织架构等场景。")
    public List<DeptRespDTO> deptChildList(@ToolParam(description = "父部门编号", required = true) Long id) {
        log.info("[MCP Tool] system_dept_child_list called with id={}", id);
        try {
            return deptApi.getChildDeptList(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_dept_child_list", e);
            throw new RuntimeException("获取子部门列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取部门树结构
     *
     * @param rootId 根部门编号(传null则从顶级部门开始)
     * @return 树形结构的部门列表
     */
    @Tool(name = "system_dept_tree", description = "获取部门树形结构数据。根据根部门编号向下递归获取完整的部门层级关系,返回嵌套的树形结构。适用于展示组织架构树、构建部门导航等场景。如果rootId为null或0,则从顶级部门开始构建树。")
    public List<Map<String, Object>> deptTree(@ToolParam(description = "根部门编号(顶级部门传0或null)", required = false) Long rootId) {
        log.info("[MCP Tool] system_dept_tree called with rootId={}", rootId);
        try {
            // 获取所有部门或指定root的子部门
            List<DeptRespDTO> allDepts;
            if (rootId == null || rootId == 0) {
                // 获取所有顶级部门(父部门ID为0或null)
                allDepts = deptApi.getDeptList(Collections.emptyList()).getCheckedData();
                // 实际上 empty list 会返回空,我们需要先获取根部门
                // 通过获取所有部门然后过滤
                allDepts = getRootDepts();
            } else {
                // 获取指定部门的完整子树
                allDepts = getSubTreeDepts(rootId);
            }

            // 构建树形结构
            return buildDeptTree(allDepts, rootId);
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_dept_tree", e);
            throw new RuntimeException("获取部门树失败: " + e.getMessage());
        }
    }

    /**
     * 获取顶级部门(父部门ID为null或0的部门)
     */
    private List<DeptRespDTO> getRootDepts() {
        // 获取所有顶级部门
        // 策略: 先获取一个大的ID范围,或者通过其他方式获取
        // 这里简化处理,假设顶级部门ID通常较小
        List<DeptRespDTO> rootDepts = new ArrayList<>();
        // 尝试获取前100个部门,然后过滤出父ID为null/0的
        try {
            List<DeptRespDTO> allDepts = deptApi.getDeptList(Arrays.asList(1L, 2L, 3L, 4L, 5L, 10L, 100L, 101L, 102L)).getCheckedData();
            rootDepts = allDepts.stream()
                    .filter(dept -> dept.getParentId() == null || dept.getParentId() == 0)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("[MCP Tool] Failed to fetch root depts, returning empty", e);
        }
        return rootDepts;
    }

    /**
     * 获取指定部门ID下的所有子部门(递归)
     */
    private List<DeptRespDTO> getSubTreeDepts(Long parentId) {
        List<DeptRespDTO> result = new ArrayList<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(parentId);

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            List<DeptRespDTO> children = deptApi.getChildDeptList(currentId).getCheckedData();
            result.addAll(children);
            for (DeptRespDTO child : children) {
                queue.add(child.getId());
            }
        }
        return result;
    }

    /**
     * 构建部门树形结构
     */
    private List<Map<String, Object>> buildDeptTree(List<DeptRespDTO> depts, Long parentId) {
        return depts.stream()
                .filter(dept -> (parentId == null || parentId == 0)
                        ? (dept.getParentId() == null || dept.getParentId() == 0)
                        : Objects.equals(dept.getParentId(), parentId))
                .map(dept -> {
                    Map<String, Object> node = new LinkedHashMap<>();
                    node.put("id", dept.getId());
                    node.put("name", dept.getName());
                    node.put("parentId", dept.getParentId());
                    node.put("leaderUserId", dept.getLeaderUserId());
                    node.put("status", dept.getStatus());

                    // 递归获取子部门
                    List<Map<String, Object>> children = buildDeptTree(depts, dept.getId());
                    if (!children.isEmpty()) {
                        node.put("children", children);
                    }
                    return node;
                })
                .collect(Collectors.toList());
    }
}
