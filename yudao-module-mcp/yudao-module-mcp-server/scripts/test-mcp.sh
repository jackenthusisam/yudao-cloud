#!/bin/bash
# MCP Server 测试脚本

BASE_URL="http://localhost:48100"
API_KEY="your-api-key-for-tenant-1"

echo "========================================"
echo "MCP Server 测试脚本"
echo "========================================"
echo ""

# 测试 1: MCP SSE 端点
echo "测试 1: MCP SSE 端点"
echo "-----------------------------------"
curl -s -w "\nHTTP Status: %{http_code}\n" \
     -H "X-MCP-API-Key: $API_KEY" \
     "$BASE_URL/mcp/sse"
echo ""

# 测试 2: MCP Tools 列表
echo ""
echo "测试 2: MCP Tools 列表"
echo "-----------------------------------"
curl -s -H "X-MCP-API-Key: $API_KEY" \
     "$BASE_URL/mcp/tools" | python3 -m json.tool
echo ""

# 测试 3: 不带 API Key（应该返回 401）
echo ""
echo "测试 3: 不带 API Key（验证认证）"
echo "-----------------------------------"
curl -s -w "\nHTTP Status: %{http_code}\n" \
     "$BASE_URL/mcp/sse"
echo ""

# 测试 4: 错误的 API Key
echo ""
echo "测试 4: 错误的 API Key"
echo "-----------------------------------"
curl -s -w "\nHTTP Status: %{http_code}\n" \
     -H "X-MCP-API-Key: wrong-key" \
     "$BASE_URL/mcp/sse"
echo ""

echo "========================================"
echo "测试完成"
echo "========================================"
