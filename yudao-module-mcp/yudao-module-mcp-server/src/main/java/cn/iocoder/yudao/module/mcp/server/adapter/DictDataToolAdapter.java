package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.framework.common.biz.system.dict.DictDataCommonApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 字典数据模块 MCP Tool Adapter
 *
 * 提供字典数据查询的 MCP Tools
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DictDataToolAdapter {

    private final cn.iocoder.yudao.framework.common.biz.system.dict.DictDataCommonApi dictDataCommonApi;

    /**
     * 根据字典类型获取字典数据列表
     *
     * @param dictType 字典类型(如: SEX, NORMAL_STATUS 等)
     * @return 字典数据列表(包含键值、标签、排序等)
     */
    @Tool(name = "system_dict_data_list", description = "根据字典类型获取该类型下的所有字典数据。返回字典键值、标签、样式属性、排序等信息。适用于动态获取下拉选项、状态显示、枚举值映射等场景。常见字典类型: SEX(性别)、NORMAL_STATUS(状态)、sys_user_status(用户状态)等。")
    public List<?> dictDataList(@ToolParam(description = "字典类型(英文大写,如 SEX, NORMAL_STATUS, sys_user_status)", required = true) String dictType) {
        log.info("[MCP Tool] system_dict_data_list called with dictType={}", dictType);
        try {
            return dictDataCommonApi.getDictDataList(dictType).getCheckedData();
        } catch (Exception e) {
            log.error("[MCP Tool] Error calling system_dict_data_list", e);
            throw new RuntimeException("获取字典数据列表失败: " + e.getMessage());
        }
    }
}
