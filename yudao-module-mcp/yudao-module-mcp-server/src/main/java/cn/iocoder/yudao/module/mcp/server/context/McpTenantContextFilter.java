package cn.iocoder.yudao.module.mcp.server.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * MCP 租户上下文过滤器（备用）
 *
 * 当 API Key 未配置时，此过滤器从请求头 X-Tenant-ID 解析租户 ID
 * 如果 API Key 已配置并设置了租户上下文，此过滤器不会覆盖
 *
 * @author yudao
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10) // 在认证过滤器之后执行
@Slf4j
public class McpTenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 如果已有租户上下文（由认证过滤器设置），跳过
            if (McpTenantContext.hasTenantId()) {
                filterChain.doFilter(request, response);
                return;
            }

            // 从请求头解析租户 ID
            String tenantIdHeader = request.getHeader(McpTenantUtils.TENANT_ID_HEADER);

            if (tenantIdHeader != null && !tenantIdHeader.isBlank()) {
                try {
                    Long tenantId = Long.parseLong(tenantIdHeader.trim());
                    McpTenantContext.setTenantId(tenantId);
                    log.debug("[MCP] Tenant context set from header: tenantId={}", tenantId);
                } catch (NumberFormatException e) {
                    log.warn("[MCP] Invalid tenant ID header: {}", tenantIdHeader);
                    McpTenantContext.setTenantId(McpTenantUtils.DEFAULT_TENANT_ID);
                }
            } else {
                // 未设置租户 ID header 时，使用默认值（单租户场景）
                McpTenantContext.setTenantId(McpTenantUtils.DEFAULT_TENANT_ID);
                log.debug("[MCP] No tenant ID header, using default: {}",
                        McpTenantUtils.DEFAULT_TENANT_ID);
            }

            filterChain.doFilter(request, response);

        } finally {
            // 重要：请求结束后清除租户上下文
            // 注意：如果认证过滤器设置了租户上下文，也会在其 finally 中清除
            McpTenantContext.clear();
        }
    }
}
