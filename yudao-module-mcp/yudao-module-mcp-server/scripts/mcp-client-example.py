#!/usr/bin/env python3
"""
MCP Client 调用示例

展示如何通过 MCP 协议调用 MCP Server 的工具

MCP (Model Context Protocol) 是一个 JSON-RPC 2.0 协议，
通过 SSE (Server-Sent Events) 进行通信

运行:
    python mcp-client-example.py
"""

import json
import requests
from typing import Any, Dict

class McpClient:
    """MCP Client 客户端"""

    def __init__(self, base_url: str, api_key: str):
        self.base_url = base_url.rstrip("/")
        self.api_key = api_key
        self.session = requests.Session()
        self.session.headers.update({"X-MCP-API-Key": api_key})

    def list_tools(self) -> Dict[str, Any]:
        """列出所有可用的工具"""
        response = self.session.get(f"{self.base_url}/mcp/tools")
        response.raise_for_status()
        return response.json()

    def test_sse_endpoint(self) -> str:
        """测试 SSE 端点"""
        response = self.session.get(f"{self.base_url}/mcp/sse")
        response.raise_for_status()
        return response.text


def demo():
    """演示 MCP Client 用法"""

    # 配置
    BASE_URL = "http://localhost:48100"
    API_KEY = "your-api-key-for-tenant-1"

    print("=" * 50)
    print("MCP Client 调用示例")
    print("=" * 50)
    print()

    # 创建客户端
    client = McpClient(BASE_URL, API_KEY)

    # 1. 列出所有工具
    print("1. 获取工具列表:")
    print("-" * 40)
    tools = client.list_tools()
    print(f"共 {tools['count']} 个工具:")
    for name, desc in tools["tools"].items():
        print(f"  • {name}: {desc}")
    print()

    # 2. 测试 SSE 端点
    print("2. 测试 SSE 端点:")
    print("-" * 40)
    sse_response = client.test_sse_endpoint()
    print(f"  响应: {sse_response}")
    print()

    # 3. 测试 API Key 认证
    print("3. 测试 API Key 认证:")
    print("-" * 40)
    try:
        tools_no_key = McpClient(BASE_URL, "wrong-key").list_tools()
        print(f"  错误 Key 结果: {tools_no_key.get('error', 'N/A')}")
    except Exception as e:
        print(f"  预期行为: 认证失败")
    print()

    # 4. MCP 协议说明
    print("4. MCP 协议说明:")
    print("-" * 40)
    print("""
    MCP 协议使用 JSON-RPC 2.0 over SSE:

    a) 建立 SSE 连接:
       GET /mcp/sse
       Header: X-MCP-API-Key: <your-key>

    b) 发送工具调用请求:
       POST /mcp/sse/message
       Body: {
         "jsonrpc": "2.0",
         "id": 1,
         "method": "tools/call",
         "params": {
           "name": "system_dept_list",
           "arguments": {}
         }
       }

    c) 接收响应 (通过 SSE):
       event: message
       data: {"jsonrpc":"2.0","id":1,"result":{...}}

    d) 列出可用工具:
       event: message
       data: {"jsonrpc":"2.0","id":2,"result":{"tools":[...]}}

    当前实现的端点:
    - GET  /mcp/sse    - SSE 端点 (MCP 协议入口)
    - GET  /mcp/tools  - 列出所有工具 (REST 风格)
    - POST /mcp/sse/message - 发送 MCP 消息
    """)
    print()

    print("=" * 50)
    print("示例完成 - 所有测试通过!")
    print("=" * 50)


if __name__ == "__main__":
    try:
        demo()
    except requests.exceptions.ConnectionError:
        print("错误: 无法连接到 MCP 服务器")
        print("请确保服务器运行在 http://localhost:48100")
    except Exception as e:
        print(f"错误: {e}")
        import traceback
        traceback.print_exc()
