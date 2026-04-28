package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.system.api.dept.PostApi;
import cn.iocoder.yudao.module.system.api.dept.dto.PostRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 岗位模块 MCP Tool Adapter
 *
 * 提供岗位管理的 MCP Tools
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PostToolAdapter {

    private final PostApi postApi;

    /**
     * 根据岗位ID列表批量获取岗位信息
     *
     * @param ids 岗位编号列表
     * @return 岗位信息列表(包含岗位编号、名称、岗位状态等)
     */
    @Tool(name = "system_post_list", description = "根据岗位ID列表批量获取岗位信息。返回每个岗位的编号、名称、排序、状态等完整信息。适用于批量查询岗位、构建岗位选择器、查看岗位详情等场景。")
    public List<PostRespDTO> postList(@ToolParam(description = "岗位编号列表", required = true) List<Long> ids) {
        log.info("[MCP Tool] system_post_list called with ids={}", ids);
        try {
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }
            return postApi.getPostList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_post_list", e);
            throw new RuntimeException("获取岗位列表失败: " + e.getMessage());
        }
    }
}
