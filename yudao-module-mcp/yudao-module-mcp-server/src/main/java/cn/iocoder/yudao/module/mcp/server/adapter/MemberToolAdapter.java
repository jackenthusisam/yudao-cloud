package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Member Module Tool Adapter
 *
 * Provides MCP tools for member user management.
 * These tools enable AI assistants to query member user information.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MemberToolAdapter {

    private final MemberUserApi memberUserApi;

    /**
     * Get member user by ID
     *
     * @param id Member user ID. Example: 1024
     * @return Member user details
     */
    @Tool(description = """
        Get member user information by user ID.

        Parameters:
        - id (Long, required): Member user ID. Example: 1024

        Returns:
        - User ID, nickname, avatar URL, mobile, email, gender, birthday, status, level ID, points, create time

        Use cases:
        - Look up specific member details when user mentions a member ID
        - Get member profile information
        - Check member level and points balance
        - Validate member existence
        """)
    public MemberUserRespDTO member_user_get(@ToolParam(description = "Member user ID. Example: 1024") Long id) {
        log.info("[MCP Tool] member_user_get called with id={}", id);
        return memberUserApi.getUser(id).getCheckedData();
    }

    /**
     * Batch get member users by IDs
     *
     * @param ids List of member user IDs. Example: [1, 2, 1024]
     * @return List of member user details
     */
    @Tool(description = """
        Batch get member user information by user IDs.

        Parameters:
        - ids (List<Long>, required): List of member user IDs. Example: [1, 2, 1024]

        Returns:
        - List of members with ID, nickname, avatar, mobile, gender, status, level, points

        Use cases:
        - Fetch multiple members at once for efficiency
        - Get member details for group operations
        - Batch validate member IDs
        """)
    public List<MemberUserRespDTO> member_user_list(@ToolParam(description = "List of member user IDs. Example: [1, 2, 1024]") List<Long> ids) {
        log.info("[MCP Tool] member_user_list called with ids={}", ids);
        return memberUserApi.getUserList(ids).getCheckedData();
    }
}
