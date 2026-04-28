package cn.iocoder.yudao.module.mcp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * MCP Server 启动类
 *
 * 基于 Spring AI MCP Server WebMVC 实现
 * 提供 AI Model Context Protocol 服务能力
 *
 * @author yudao
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {
        "cn.iocoder.yudao.module.infra.api",
        "cn.iocoder.yudao.module.system.api",
        "cn.iocoder.yudao.module.member.api",
        "cn.iocoder.yudao.module.product.api"
})
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
}