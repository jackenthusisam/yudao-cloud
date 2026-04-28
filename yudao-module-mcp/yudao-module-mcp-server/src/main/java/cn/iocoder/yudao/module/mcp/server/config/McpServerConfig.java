package cn.iocoder.yudao.module.mcp.server.config;

import cn.iocoder.yudao.module.mcp.server.adapter.DeptToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.DictDataToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.InfraToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.MemberToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.PostToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.ProductSkuToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.ProductSpuToolAdapter;
import cn.iocoder.yudao.module.mcp.server.adapter.UserToolAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP Server 配置类
 *
 * 使用 Spring AI MCP Server (spring.ai.mcp.server.enabled = true)
 * 通过 MethodToolCallbackProvider 注册所有 @Tool 注解的 adapter
 *
 * @author yudao
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class McpServerConfig {

    private final DeptToolAdapter deptToolAdapter;
    private final UserToolAdapter userToolAdapter;
    private final PostToolAdapter postToolAdapter;
    private final DictDataToolAdapter dictDataToolAdapter;
    private final InfraToolAdapter infraToolAdapter;
    private final MemberToolAdapter memberToolAdapter;
    private final ProductSpuToolAdapter productSpuToolAdapter;
    private final ProductSkuToolAdapter productSkuToolAdapter;

    /**
     * 注册 Dept 模块工具
     * 提供: 部门查询、部门树等工具
     */
    @Bean
    public MethodToolCallbackProvider deptTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(deptToolAdapter)
                .build();
    }

    /**
     * 注册 User 模块工具
     * 提供: 用户查询等工具
     */
    @Bean
    public MethodToolCallbackProvider userTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(userToolAdapter)
                .build();
    }

    /**
     * 注册 Post 模块工具
     * 提供: 岗位查询等工具
     */
    @Bean
    public MethodToolCallbackProvider postTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(postToolAdapter)
                .build();
    }

    /**
     * 注册 DictData 模块工具
     * 提供: 字典数据查询等工具
     */
    @Bean
    public MethodToolCallbackProvider dictDataTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dictDataToolAdapter)
                .build();
    }

    /**
     * 注册 Infra 模块工具
     * 提供: 配置项、文件预签名 URL 等工具
     */
    @Bean
    public MethodToolCallbackProvider infraTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(infraToolAdapter)
                .build();
    }

    /**
     * 注册 Member 模块工具
     * 提供: 会员用户查询工具
     */
    @Bean
    public MethodToolCallbackProvider memberTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(memberToolAdapter)
                .build();
    }

    /**
     * 注册 Product SPU 模块工具
     * 提供: 商品 SPU 查询工具
     */
    @Bean
    public MethodToolCallbackProvider productSpuTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(productSpuToolAdapter)
                .build();
    }

    /**
     * 注册 Product SKU 模块工具
     * 提供: 商品 SKU 查询工具
     */
    @Bean
    public MethodToolCallbackProvider productSkuTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(productSkuToolAdapter)
                .build();
    }
}
