package cn.iocoder.yudao.module.mcp.server.security;

import cn.iocoder.yudao.module.mcp.server.config.YudaoMcpServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * MCP Server 安全配置
 *
 * @author yudao
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
public class McpSecurityConfiguration {

    private final YudaoMcpServerProperties mcpServerProperties;

    /**
     * Spring Security 配置
     * 禁用安全配置， permit 所有请求
     * MCP 端点通过 API Key 过滤器进行认证
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/mcp/**").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    /**
     * 注册 API Key 认证过滤器
     * 拦截所有 MCP 端点请求，验证 X-MCP-API-Key header
     */
    @Bean
    @ConditionalOnProperty(prefix = "yudao.mcp.server", name = "enabled")
    public FilterRegistrationBean<McpApiKeyAuthenticationFilter> apiKeyAuthenticationFilter() {
        FilterRegistrationBean<McpApiKeyAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        McpApiKeyAuthenticationFilter filter = new McpApiKeyAuthenticationFilter(mcpServerProperties);

        // 设置过滤器名称和顺序
        registrationBean.setName("mcpApiKeyAuthenticationFilter");
        registrationBean.setFilter(filter);
        registrationBean.setOrder(MCP_API_KEY_FILTER_ORDER);

        // MCP 端点路径
        String sseEndpoint = mcpServerProperties.getSseEndpoint();
        String sseMessageEndpoint = mcpServerProperties.getSseMessageEndpoint();

        if (sseEndpoint != null) {
            registrationBean.addUrlPatterns(sseEndpoint);
            log.info("[MCP] API Key filter registered for SSE endpoint: {}", sseEndpoint);
        }
        if (sseMessageEndpoint != null) {
            registrationBean.addUrlPatterns(sseMessageEndpoint);
            log.info("[MCP] API Key filter registered for SSE message endpoint: {}", sseMessageEndpoint);
        }

        return registrationBean;
    }

    /**
     * 过滤器顺序
     * 需要在 Spring Security 过滤器之前执行，以便早期拦截无效请求
     */
    private static final int MCP_API_KEY_FILTER_ORDER = -10;
}
