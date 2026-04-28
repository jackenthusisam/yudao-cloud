package cn.iocoder.yudao.module.mcp.server.context;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * MCP 租户上下文工具类
 *
 * 基于 yudao-framework 的 TenantContextHolder 提供便捷的租户校验方法
 *
 * @author yudao
 */
@Slf4j
public class McpTenantUtils {

    /**
     * 请求头名称：租户 ID
     */
    public static final String TENANT_ID_HEADER = "X-Tenant-ID";

    /**
     * 默认租户 ID（未设置时使用）
     */
    public static final Long DEFAULT_TENANT_ID = 1L;

    /**
     * 获取当前请求的租户 ID
     *
     * @return 租户 ID，如果未设置返回 null
     */
    public static Long getTenantId() {
        return TenantContextHolder.getTenantId();
    }

    /**
     * 获取当前请求的租户 ID，如果未设置则返回默认值
     *
     * @return 租户 ID
     */
    public static Long getTenantIdOrDefault() {
        Long tenantId = TenantContextHolder.getTenantId();
        return tenantId != null ? tenantId : DEFAULT_TENANT_ID;
    }

    /**
     * 检查是否设置了租户上下文
     */
    public static boolean hasTenantContext() {
        return TenantContextHolder.getTenantId() != null;
    }

    /**
     * 校验数据归属
     * 如果数据的租户 ID 与当前请求的租户 ID 不匹配，抛出异常
     *
     * @param dataTenantId 数据的租户 ID
     * @throws ServiceException 403 如果租户不匹配
     */
    public static void verifyTenantOwnership(Long dataTenantId) {
        if (dataTenantId == null) {
            log.debug("[MCP] Data has no tenantId, skipping ownership verification");
            return;
        }

        Long requestTenantId = getTenantIdOrDefault();
        if (!dataTenantId.equals(requestTenantId)) {
            log.warn("[MCP] Cross-tenant access attempt: dataTenantId={}, requestTenantId={}",
                    dataTenantId, requestTenantId);
            throw new ServiceException(403, "Cannot access other tenant's data");
        }
    }

    /**
     * 清除租户上下文
     */
    public static void clear() {
        TenantContextHolder.clear();
    }
}
