package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.product.api.sku.ProductSkuApi;
import cn.iocoder.yudao.module.product.api.sku.dto.ProductSkuRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Product SKU Module Tool Adapter
 *
 * Provides MCP tools for product SKU (Stock Keeping Unit) management.
 * SKU represents the specific product variant with specific attributes like size, color, etc.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSkuToolAdapter {

    private final ProductSkuApi productSkuApi;

    /**
     * Get product SKU by ID
     *
     * @param id Product SKU ID. Example: 1024
     * @return Product SKU details including price, stock, attributes
     */
    @Tool(description = """
        Get product SKU (Stock Keeping Unit) information by SKU ID.

        Parameters:
        - id (Long, required): Product SKU ID. Example: 1024

        Returns:
        - SKU ID, SPU ID, name, specification (JSON of attributes like size, color),
          price, market price, cost price, stock quantity, weight, status, create time

        Use cases:
        - Look up specific SKU details when user mentions a SKU ID
        - Get SKU pricing and stock information
        - Display product variant details
        - Check inventory availability
        """)
    public ProductSkuRespDTO product_sku_get(@ToolParam(description = "Product SKU ID. Example: 1024") Long id) {
        log.info("[MCP Tool] product_sku_get called with id={}", id);
        return productSkuApi.getSku(id).getCheckedData();
    }

    /**
     * Batch get product SKU by IDs
     *
     * @param ids List of product SKU IDs. Example: [1024, 2048]
     * @return List of product SKU details
     */
    @Tool(description = """
        Batch get product SKU (Stock Keeping Unit) information by SKU IDs.

        Parameters:
        - ids (List<Long>, required): List of product SKU IDs. Example: [1024, 2048]

        Returns:
        - List of SKUs with ID, SPU ID, name, specs, prices, stock, status

        Use cases:
        - Fetch multiple SKUs at once for efficiency
        - Get SKU details for cart operations
        - Batch validate SKU IDs
        - Display product variant comparisons
        """)
    public List<ProductSkuRespDTO> product_sku_list(@ToolParam(description = "List of product SKU IDs. Example: [1024, 2048]") List<Long> ids) {
        log.info("[MCP Tool] product_sku_list called with ids={}", ids);
        return productSkuApi.getSkuList(ids).getCheckedData();
    }

    /**
     * Get SKU list by SPU ID
     *
     * @param spuId Product SPU ID. Example: 1
     * @return List of all SKUs belonging to this SPU
     */
    @Tool(description = """
        Get all product SKUs (Stock Keeping Unit) belonging to a specific SPU.

        Parameters:
        - spuId (Long, required): Product SPU ID. Example: 1

        Returns:
        - List of SKUs with ID, SPU ID, name, specs (size, color, etc.), prices, stock, status

        Use cases:
        - Get all variants of a product when user wants to see all options
        - Display product color/size selection
        - List all available combinations for a product
        - Inventory check across all variants
        """)
    public List<ProductSkuRespDTO> product_sku_list_by_spu_id(@ToolParam(description = "Product SPU ID. Example: 1") Long spuId) {
        log.info("[MCP Tool] product_sku_list_by_spu_id called with spuId={}", spuId);
        return productSkuApi.getSkuListBySpuId(spuId).getCheckedData();
    }
}
