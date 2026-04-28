package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.file.dto.FileCreateReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Infra 模块 MCP Tool Adapter
 *
 * 提供配置管理和文件管理的 MCP Tools
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InfraToolAdapter {

    private final ConfigApi configApi;
    private final FileApi fileApi;

    // ==================== Config Tools ====================

    /**
     * 根据配置键获取配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    @Tool(name = "infra_config_get", description = "根据配置键(key)获取系统配置值。适用于查询系统参数、开关配置等场景。")
    public String configGet(@ToolParam(description = "配置键(配置项的 key)", required = true) String key) {
        log.info("[MCP Tool] infra_config_get called with key={}", key);
        try {
            return configApi.getConfigValueByKey(key).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling infra_config_get", e);
            throw new RuntimeException("获取配置失败: " + e.getMessage());
        }
    }

    // ==================== File Tools ====================

    /**
     * 获取文件预签名URL
     * 用于在不暴露真实URL的情况下临时访问文件
     *
     * @param url 文件的完整访问地址
     * @param expirationSeconds 预签名URL有效期，单位秒，默认3600
     * @return 文件预签名URL
     */
    @Tool(name = "infra_file_presigned_url", description = "获取文件的预签名访问URL。适用于临时授权访问私有文件、生成文件下载链接等场景。返回的预签名URL在指定时间后自动失效。")
    public String filePresignedUrl(
            @ToolParam(description = "文件的完整访问地址(OSS/S3 等存储路径)", required = true) String url,
            @ToolParam(description = "预签名URL有效期，单位秒，默认3600秒(1小时)", required = false) Integer expirationSeconds) {
        log.info("[MCP Tool] infra_file_presigned_url called with url={}, expirationSeconds={}", url, expirationSeconds);
        try {
            if (expirationSeconds == null) {
                expirationSeconds = 3600;
            }
            return fileApi.presignGetUrl(url, expirationSeconds).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling infra_file_presigned_url", e);
            throw new RuntimeException("获取预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件并返回访问路径
     *
     * @param content 文件内容(Base64编码)
     * @param name 文件名称(可选)
     * @param directory 存储目录(可选)
     * @param type 文件MIME类型(可选)
     * @return 文件访问路径
     */
    @Tool(name = "infra_file_upload", description = "上传文件到对象存储并返回访问路径。适用于上传用户头像、商品图片、附件等场景。支持指定文件名、存储目录和文件类型。")
    public String fileUpload(
            @ToolParam(description = "文件内容(Base64编码)", required = true) String content,
            @ToolParam(description = "文件名称(含扩展名，如 image.png)", required = false) String name,
            @ToolParam(description = "存储目录路径(如 /avatar/, /product/)", required = false) String directory,
            @ToolParam(description = "文件的MIME类型(如 image/jpeg, application/pdf)", required = false) String type) {
        log.info("[MCP Tool] infra_file_upload called with name={}, directory={}, type={}", name, directory, type);
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(content);
            FileCreateReqDTO reqDTO = new FileCreateReqDTO();
            reqDTO.setName(name);
            reqDTO.setDirectory(directory);
            reqDTO.setType(type);
            reqDTO.setContent(bytes);
            return fileApi.createFile(reqDTO).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling infra_file_upload", e);
            throw new RuntimeException("上传文件失败: " + e.getMessage());
        }
    }
}
