package cn.iocoder.yudao.module.mcp.server.security;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mcp.server.config.YudaoMcpServerProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * MCP Server API Key 认证过滤器
 *
 * 验证请求 header 中的 X-MCP-API-Key 是否与配置的一致
 * 认证成功后设置租户上下文，确保 MCP Tool 调用使用正确的租户过滤
 *
 * @author yudao
 */
@RequiredArgsConstructor
@Slf4j
public class McpApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-MCP-API-Key";

    private final YudaoMcpServerProperties mcpServerProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 如果未配置 API Keys，则跳过认证（允许访问，但无租户上下文）
        Map<String, Long> apiKeys = mcpServerProperties.getApiKeys();
        if (apiKeys == null || apiKeys.isEmpty()) {
            log.warn("[MCP] API Keys not configured, skipping authentication");
            chain.doFilter(request, response);
            return;
        }

        // 检查请求路径是否为 MCP 端点
        String requestPath = request.getRequestURI();
        if (!isMcpEndpoint(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        // 提取请求中的 API Key
        String requestApiKey = request.getHeader(API_KEY_HEADER);

        // 验证 API Key 并获取对应的租户编号
        Long tenantId = validateAndGetTenantId(requestApiKey);
        if (tenantId == null) {
            log.warn("[MCP] API Key authentication failed for path: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing API Key\"}");
            return;
        }

        // 设置租户上下文，确保后续 MCP Tool 调用使用正确的租户过滤
        TenantContextHolder.setTenantId(tenantId);
        log.info("[MCP] API Key authentication succeeded for path: {}, tenantId: {}", requestPath, tenantId);

        try {
            chain.doFilter(request, response);
        } finally {
            // 清理租户上下文
            TenantContextHolder.clear();
        }
    }

    /**
     * 验证 API Key 并返回对应的租户编号
     *
     * @param requestApiKey 请求中的 API Key
     * @return 租户编号，如果验证失败则返回 null
     */
    private Long validateAndGetTenantId(String requestApiKey) {
        if (requestApiKey == null || requestApiKey.isBlank()) {
            return null;
        }
        Map<String, Long> apiKeys = mcpServerProperties.getApiKeys();
        return apiKeys.get(requestApiKey);
    }

    /**
     * 判断请求路径是否为 MCP 端点
     */
    private boolean isMcpEndpoint(String requestPath) {
        // SSE 端点
        String sseEndpoint = mcpServerProperties.getSseEndpoint();
        if (sseEndpoint != null && matchesEndpoint(requestPath, sseEndpoint)) {
            return true;
        }

        String sseMessageEndpoint = mcpServerProperties.getSseMessageEndpoint();
        if (sseMessageEndpoint != null && matchesEndpoint(requestPath, sseMessageEndpoint)) {
            return true;
        }

        return false;
    }

    /**
     * 判断请求路径是否匹配端点
     */
    private boolean matchesEndpoint(String requestPath, String endpoint) {
        if (endpoint == null) {
            return false;
        }
        // 支持通配符匹配
        if (endpoint.endsWith("/**")) {
            String prefix = endpoint.substring(0, endpoint.length() - 3);
            return requestPath.startsWith(prefix);
        }
        return requestPath.equals(endpoint);
    }
}
