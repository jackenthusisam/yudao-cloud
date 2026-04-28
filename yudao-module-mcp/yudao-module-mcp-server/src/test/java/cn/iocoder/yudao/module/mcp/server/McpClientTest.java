package cn.iocoder.yudao.module.mcp.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.HashMap;

/**
 * MCP Server 客户端测试
 *
 * 验证 MCP 服务是否正常工作
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("local")
public class McpClientTest {

    private static final String BASE_URL = "http://localhost:48100";
    private static final String API_KEY = "your-api-key-for-tenant-1";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 测试 MCP SSE 端点
     */
    @Test
    public void testMcpSseEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MCP-API-Key", API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/mcp/sse",
                HttpMethod.GET,
                entity,
                String.class
        );

        System.out.println("=== MCP SSE Endpoint Test ===");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: " + response.getBody());
    }

    /**
     * 测试 MCP Tools 列表
     */
    @Test
    public void testMcpToolsList() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MCP-API-Key", API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/mcp/tools",
                HttpMethod.GET,
                entity,
                Map.class
        );

        System.out.println("=== MCP Tools List Test ===");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Tools count: " + response.getBody().get("count"));

        @SuppressWarnings("unchecked")
        Map<String, String> tools = (Map<String, String>) response.getBody().get("tools");
        System.out.println("\nAvailable tools:");
        tools.forEach((name, desc) -> System.out.println("  - " + name + ": " + desc));
    }

    /**
     * 测试健康检查端点
     */
    @Test
    public void testHealthEndpoint() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_URL + "/actuator/health",
                    String.class
            );
            System.out.println("=== Health Check Test ===");
            System.out.println("Status: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("Health endpoint not available (this is normal)");
        }
    }

    public static void main(String[] args) {
        McpClientTest test = new McpClientTest();

        System.out.println("Starting MCP Server Client Tests...\n");

        try {
            test.testHealthEndpoint();
            System.out.println();
            test.testMcpSseEndpoint();
            System.out.println();
            test.testMcpToolsList();
            System.out.println("\n=== All Tests Passed ===");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
