package cn.iocoder.yudao.module.mcp.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MCP Server 配置属性启用
 *
 * @author yudao
 */
@Configuration
@EnableConfigurationProperties(YudaoMcpServerProperties.class)
public class McpServerPropertiesConfiguration {
}
