package cn.iocoder.yudao.module.mcp.server.config;

import cn.iocoder.yudao.framework.security.config.SecurityProperties;
import cn.iocoder.yudao.framework.web.config.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;

/**
 * MCP Server Web 配置
 * <p>
 * 由于排除了 YudaoWebAutoConfiguration，需要手动提供 WebProperties bean
 */
@Configuration
public class WebPropertiesConfiguration {

    @Bean
    @Primary
    public WebProperties webProperties() {
        WebProperties properties = new WebProperties();

        // API 配置
        WebProperties.Api appApi = new WebProperties.Api("/app-api", "**.controller.app.**");
        WebProperties.Api adminApi = new WebProperties.Api("/admin-api", "**.controller.admin.**");
        properties.setAppApi(appApi);
        properties.setAdminApi(adminApi);

        // UI 配置
        WebProperties.Ui adminUi = new WebProperties.Ui();
        adminUi.setUrl("http://localhost:8080");
        properties.setAdminUi(adminUi);

        return properties;
    }

    @Bean
    @Primary
    public SecurityProperties securityProperties() {
        SecurityProperties properties = new SecurityProperties();
        properties.setTokenHeader("Authorization");
        properties.setTokenParameter("token");
        properties.setMockEnable(false);
        properties.setMockSecret("test");
        properties.setPermitAllUrls(Collections.emptyList());
        properties.setPasswordEncoderLength(4);
        return properties;
    }
}
