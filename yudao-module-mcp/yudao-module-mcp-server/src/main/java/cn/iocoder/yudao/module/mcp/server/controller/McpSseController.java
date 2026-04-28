package cn.iocoder.yudao.module.mcp.server.controller;

import cn.iocoder.yudao.framework.common.biz.system.dict.DictDataCommonApi;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.infra.api.file.dto.FileCreateReqDTO;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.product.api.sku.ProductSkuApi;
import cn.iocoder.yudao.module.product.api.spu.ProductSpuApi;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.PostApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP SSE Controller
 *
 * Provides MCP protocol endpoints for AI clients to call business APIs
 * MCP (Model Context Protocol) 基于 JSON-RPC 2.0
 */
@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
@Slf4j
public class McpSseController {

    private final DictDataCommonApi dictDataCommonApi;
    private final DeptApi deptApi;
    private final AdminUserApi adminUserApi;
    private final PostApi postApi;
    private final ConfigApi configApi;
    private final FileApi fileApi;
    private final MemberUserApi memberUserApi;
    private final ProductSpuApi productSpuApi;
    private final ProductSkuApi productSkuApi;

    /**
     * MCP SSE endpoint - main entry point for MCP protocol
     */
    @GetMapping(value = "/sse", produces = "text/event-stream")
    public String sseEndpoint() {
        log.info("[MCP] SSE endpoint called");
        return "MCP Server is running. Use /mcp/tools to list available tools.";
    }

    /**
     * MCP JSON-RPC message endpoint
     * 接收并处理 MCP 协议消息
     */
    @PostMapping("/sse/message")
    @ResponseBody
    public Map<String, Object> handleMessage(@RequestBody Map<String, Object> request) {
        log.info("[MCP] Received message: {}", request);

        Map<String, Object> response = new HashMap<>();
        String jsonrpc = (String) request.get("jsonrpc");
        Object id = request.get("id");
        String method = (String) request.get("method");

        response.put("jsonrpc", jsonrpc != null ? jsonrpc : "2.0");
        response.put("id", id);

        try {
            if ("tools/call".equals(method)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = (Map<String, Object>) request.get("params");
                String toolName = (String) params.get("name");
                @SuppressWarnings("unchecked")
                Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

                log.info("[MCP] Calling tool: {} with args: {}", toolName, arguments);
                Object result = callTool(toolName, arguments);
                response.put("result", result);
            } else if ("tools/list".equals(method)) {
                response.put("result", getToolList());
            } else {
                response.put("error", Map.of(
                        "code", -32601,
                        "message", "Method not found: " + method
                ));
            }
        } catch (Exception e) {
            log.error("[MCP] Error handling message", e);
            response.put("error", Map.of(
                    "code", -32603,
                    "message", "Internal error: " + e.getMessage()
            ));
        }

        return response;
    }

    /**
     * 调用指定的工具
     */
    private Object callTool(String toolName, Map<String, Object> arguments) {
        switch (toolName) {
            // System tools
            case "ping":
                return callPing(arguments);
            case "system_dict_data_list":
                return callDictDataList(arguments);
            case "system_dept_list":
                return callDeptList(arguments);
            case "system_dept_get":
                return callDeptGet(arguments);
            case "system_admin_user_list":
                return callAdminUserList(arguments);
            case "system_admin_user_get":
                return callAdminUserGet(arguments);
            case "system_post_list":
                return callPostList(arguments);

            // Infra tools
            case "infra_config_get":
                return callConfigGet(arguments);
            case "infra_file_presigned_url":
                return callFilePresignedUrl(arguments);
            case "infra_file_upload":
                return callFileUpload(arguments);

            // Member tools
            case "member_user_list":
                return callMemberUserList(arguments);
            case "member_user_get":
                return callMemberUserGet(arguments);

            // Product tools
            case "product_spu_list":
                return callProductSpuList(arguments);
            case "product_spu_get":
                return callProductSpuGet(arguments);
            case "product_sku_list":
                return callProductSkuList(arguments);
            case "product_sku_get":
                return callProductSkuGet(arguments);

            default:
                return Map.of("error", "Unknown tool: " + toolName);
        }
    }

    // ==================== System Tools ====================

    private Object callPing(Map<String, Object> input) {
        log.info("[MCP] ping called");
        return Map.of(
                "status", "ok",
                "message", "pong",
                "timestamp", System.currentTimeMillis()
        );
    }

    private Object callDictDataList(Map<String, Object> input) {
        String dictType = (String) input.get("dictType");
        log.info("[MCP] system_dict_data_list called with dictType={}", dictType);
        try {
            return dictDataCommonApi.getDictDataList(dictType).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling dict_data_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callDeptList(Map<String, Object> input) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> idsList = (List<Number>) input.get("ids");
            List<Long> ids = idsList.stream().map(Number::longValue).toList();
            log.info("[MCP] system_dept_list called with ids={}", ids);
            return deptApi.getDeptList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling dept_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callDeptGet(Map<String, Object> input) {
        try {
            Long id = ((Number) input.get("id")).longValue();
            log.info("[MCP] system_dept_get called with id={}", id);
            return deptApi.getDept(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling dept_get", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callAdminUserList(Map<String, Object> input) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> idsList = (List<Number>) input.get("ids");
            List<Long> ids = idsList.stream().map(Number::longValue).toList();
            log.info("[MCP] system_admin_user_list called with ids={}", ids);
            return adminUserApi.getUserList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling admin_user_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callAdminUserGet(Map<String, Object> input) {
        try {
            Long id = ((Number) input.get("id")).longValue();
            log.info("[MCP] system_admin_user_get called with id={}", id);
            return adminUserApi.getUser(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling admin_user_get", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callPostList(Map<String, Object> input) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> idsList = (List<Number>) input.get("ids");
            List<Long> ids = idsList.stream().map(Number::longValue).toList();
            log.info("[MCP] system_post_list called with ids={}", ids);
            return postApi.getPostList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling post_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    // ==================== Infra Tools ====================

    private Object callConfigGet(Map<String, Object> input) {
        try {
            String key = (String) input.get("key");
            log.info("[MCP] infra_config_get called with key={}", key);
            return configApi.getConfigValueByKey(key).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling config_get", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callFilePresignedUrl(Map<String, Object> input) {
        try {
            String url = (String) input.get("url");
            Integer expirationSeconds = (Integer) input.get("expirationSeconds");
            log.info("[MCP] infra_file_presigned_url called with url={}, expirationSeconds={}", url, expirationSeconds);
            return fileApi.presignGetUrl(url, expirationSeconds).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling file_presigned_url", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callFileUpload(Map<String, Object> input) {
        try {
            String content = (String) input.get("content");
            String name = (String) input.get("name");
            String directory = (String) input.get("directory");
            String type = (String) input.get("type");
            log.info("[MCP] infra_file_upload called with name={}, directory={}, type={}", name, directory, type);

            FileCreateReqDTO reqDTO = new FileCreateReqDTO();
            reqDTO.setName(name);
            reqDTO.setDirectory(directory);
            reqDTO.setType(type);
            if (content != null) {
                reqDTO.setContent(java.util.Base64.getDecoder().decode(content));
            }
            return fileApi.createFile(reqDTO).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling infra_file_upload", e);
            return Map.of("error", e.getMessage());
        }
    }

    // ==================== Member Tools ====================

    private Object callMemberUserList(Map<String, Object> input) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> idsList = (List<Number>) input.get("ids");
            List<Long> ids = idsList.stream().map(Number::longValue).toList();
            log.info("[MCP] member_user_list called with ids={}", ids);
            return memberUserApi.getUserList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling member_user_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callMemberUserGet(Map<String, Object> input) {
        try {
            Long id = ((Number) input.get("id")).longValue();
            log.info("[MCP] member_user_get called with id={}", id);
            return memberUserApi.getUser(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling member_user_get", e);
            return Map.of("error", e.getMessage());
        }
    }

    // ==================== Product Tools ====================

    private Object callProductSpuList(Map<String, Object> input) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> idsList = (List<Number>) input.get("ids");
            List<Long> ids = idsList.stream().map(Number::longValue).toList();
            log.info("[MCP] product_spu_list called with ids={}", ids);
            return productSpuApi.getSpuList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling product_spu_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callProductSpuGet(Map<String, Object> input) {
        try {
            Long id = ((Number) input.get("id")).longValue();
            log.info("[MCP] product_spu_get called with id={}", id);
            return productSpuApi.getSpu(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling product_spu_get", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callProductSkuList(Map<String, Object> input) {
        try {
            @SuppressWarnings("unchecked")
            List<Number> idsList = (List<Number>) input.get("ids");
            List<Long> ids = idsList.stream().map(Number::longValue).toList();
            log.info("[MCP] product_sku_list called with ids={}", ids);
            return productSkuApi.getSkuList(ids).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling product_sku_list", e);
            return Map.of("error", e.getMessage());
        }
    }

    private Object callProductSkuGet(Map<String, Object> input) {
        try {
            Long id = ((Number) input.get("id")).longValue();
            log.info("[MCP] product_sku_get called with id={}", id);
            return productSkuApi.getSku(id).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP] Error calling product_sku_get", e);
            return Map.of("error", e.getMessage());
        }
    }

    // ==================== Tool List ====================

    private Map<String, Object> getToolList() {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", getToolDescriptions());
        return result;
    }

    private Map<String, String> getToolDescriptions() {
        Map<String, String> tools = new HashMap<>();

        // System tools
        tools.put("ping", "Ping the MCP server to check connectivity");

        // Member tools
        tools.put("member_user_get", "Get member user by ID");
        tools.put("member_user_list", "Get list of member users");

        // Product tools
        tools.put("product_spu_get", "Get product SPU by ID");
        tools.put("product_spu_list", "Get list of product SPU");
        tools.put("product_sku_get", "Get product SKU by ID");
        tools.put("product_sku_list", "Get list of product SKU");
        tools.put("product_sku_list_by_spu_id", "Get SKU list by SPU ID");

        // System tools
        tools.put("system_dept_get", "Get department by ID");
        tools.put("system_dept_list", "Get list of departments");
        tools.put("system_dept_child_list", "Get child departments");
        tools.put("system_dict_data_list", "Get dictionary data list");
        tools.put("system_admin_user_get", "Get admin user by ID");
        tools.put("system_admin_user_list", "Get list of admin users");
        tools.put("system_admin_user_list_by_dept", "Get admin users by department");
        tools.put("system_post_list", "Get list of posts");

        // Infra tools
        tools.put("infra_config_get", "Get configuration value by key");
        tools.put("infra_file_presigned_url", "Get file presigned URL");
        tools.put("infra_file_upload", "Upload file and return access path");

        return tools;
    }

    /**
     * List all available MCP tools
     */
    @GetMapping("/tools")
    public Map<String, Object> listTools() {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", getToolDescriptions());
        result.put("count", getToolDescriptions().size());
        return result;
    }
}
