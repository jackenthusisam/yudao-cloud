package cn.iocoder.yudao.module.mcp.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * MCP Server 配置属性
 *
 * @author yudao
 */
@Data
@ConfigurationProperties(prefix = "yudao.mcp.server")
public class YudaoMcpServerProperties {

    /**
     * 是否启用 MCP Server
     */
    private boolean enabled = true;

    /**
     * 服务名称
     */
    private String name = "yudao-mcp-server";

    /**
     * 服务版本
     */
    private String version = "1.0.0";

    /**
     * 服务描述
     */
    private String instructions = "芋道云 MCP 服务";

    /**
     * SSE 端点路径
     */
    private String sseEndpoint = "/mcp/sse";

    /**
     * SSE 消息端点路径
     */
    private String sseMessageEndpoint = "/mcp/sse/message";

    /**
     * HTTP 端点路径
     */
    private String httpEndpoint = "/mcp/http";

    /**
     * API Key 配置
     * Key: API Key 值
     * Value:对应的租户编号
     */
    private Map<String, Long> apiKeys;
}
