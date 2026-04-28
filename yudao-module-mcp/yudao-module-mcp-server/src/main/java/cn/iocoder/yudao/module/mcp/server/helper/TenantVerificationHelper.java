package cn.iocoder.yudao.module.mcp.server.helper;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.mcp.server.context.McpTenantUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 租户归属校验助手
 *
 * 提供通用的租户校验逻辑，适配各种数据类型
 *
 * @author yudao
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantVerificationHelper {

    /**
     * 校验单个数据的租户归属
     *
     * @param data  数据对象
     * @param tenantGetter 获取数据租户 ID 的函数
     * @throws ServiceException 403 如果租户不匹配
     */
    public <T> void verifySingle(T data, java.util.function.Function<T, Long> tenantGetter) {
        if (data == null) {
            return;
        }
        Long dataTenantId = tenantGetter.apply(data);
        McpTenantUtils.verifyTenantOwnership(dataTenantId);
    }

    /**
     * 校验集合中所有数据的租户归属
     * 如果任何一个数据不属于当前租户，抛出异常
     *
     * @param dataList 数据列表
     * @param tenantGetter 获取数据租户 ID 的函数
     * @throws ServiceException 403 如果任何数据租户不匹配
     */
    public <T> void verifyCollection(Collection<T> dataList,
                                    java.util.function.Function<T, Long> tenantGetter) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        Long requestTenantId = McpTenantUtils.getTenantIdOrDefault();

        for (T data : dataList) {
            if (data == null) {
                continue;
            }
            Long dataTenantId = tenantGetter.apply(data);
            if (dataTenantId != null && !dataTenantId.equals(requestTenantId)) {
                log.warn("[MCP] Cross-tenant access in collection: dataTenantId={}, requestTenantId={}",
                        dataTenantId, requestTenantId);
                throw new ServiceException(403, "Data contains resources from other tenants");
            }
        }
    }

    /**
     * 校验并过滤集合，只保留属于当前租户的数据
     *
     * @param dataList 原始数据列表
     * @param tenantGetter 获取数据租户 ID 的函数
     * @param <T> 数据类型
     * @return 过滤后的列表
     */
    public <T> List<T> filterByTenant(Collection<T> dataList,
                                       java.util.function.Function<T, Long> tenantGetter) {
        if (dataList == null || dataList.isEmpty()) {
            return List.of();
        }

        Long requestTenantId = McpTenantUtils.getTenantIdOrDefault();

        return dataList.stream()
                .filter(Objects::nonNull)
                .filter(data -> {
                    Long dataTenantId = tenantGetter.apply(data);
                    return dataTenantId == null || dataTenantId.equals(requestTenantId);
                })
                .toList();
    }
}
