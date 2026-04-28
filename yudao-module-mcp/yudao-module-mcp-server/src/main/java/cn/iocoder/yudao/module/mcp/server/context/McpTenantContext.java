package cn.iocoder.yudao.module.mcp.server.context;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

/**
 * MCP 租户上下文
 *
 * 基于 yudao-framework 的 TenantContextHolder
 * 此类保留用于 MCP 模块内部，方便直接操作 ThreadLocal
 *
 * @author yudao
 */
public class McpTenantContext {

    /**
     * 设置当前租户 ID
     */
    public static void setTenantId(Long tenantId) {
        TenantContextHolder.setTenantId(tenantId);
    }

    /**
     * 获取当前租户 ID
     */
    public static Long getTenantId() {
        return TenantContextHolder.getTenantId();
    }

    /**
     * 清除租户上下文
     * 重要：请求结束后必须调用
     */
    public static void clear() {
        TenantContextHolder.clear();
    }

    /**
     * 检查是否设置了租户 ID
     */
    public static boolean hasTenantId() {
        return TenantContextHolder.getTenantId() != null;
    }
}
