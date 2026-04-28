package cn.iocoder.yudao.module.mcp.server.adapter;

import cn.iocoder.yudao.module.product.api.spu.ProductSpuApi;
import cn.iocoder.yudao.module.product.api.spu.dto.ProductSpuRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Product SPU Module Tool Adapter
 *
 * Provides MCP tools for product SPU (Standard Product Unit) management.
 * SPU is the base product entity that defines the common attributes of a product.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSpuToolAdapter {

    private final ProductSpuApi productSpuApi;

    /**
     * Get product SPU by ID
     *
     * @param id Product SPU ID. Example: 1
     * @return Product SPU details
     */
    @Tool(description = """
        Get product SPU (Standard Product Unit) information by SPU ID.

        Parameters:
        - id (Long, required): Product SPU ID. Example: 1

        Returns:
        - SPU ID, name, description, category ID, unit, brand ID, status (active/inactive),
          price range, main image, gallery images, tags, sales count, create time

        Use cases:
        - Look up specific product details when user mentions a product ID
        - Get product information for price queries
        - Display product details in recommendations
        - Validate product existence
        """)
    public ProductSpuRespDTO product_spu_get(@ToolParam(description = "Product SPU ID. Example: 1") Long id) {
        log.info("[MCP Tool] product_spu_get called with id={}", id);
        return productSpuApi.getSpu(id).getCheckedData();
    }

    /**
     * Batch get product SPU by IDs
     *
     * @param ids List of product SPU IDs. Example: [1, 2, 3]
     * @return List of product SPU details
     */
    @Tool(description = """
        Batch get product SPU (Standard Product Unit) information by SPU IDs.

        Parameters:
        - ids (List<Long>, required): List of product SPU IDs. Example: [1, 2, 3]

        Returns:
        - List of SPU products with ID, name, category, price range, images, status, sales count

        Use cases:
        - Fetch multiple products at once for efficiency
        - Get product list for category pages
        - Display product comparisons
        - Batch validate product IDs
        """)
    public List<ProductSpuRespDTO> product_spu_list(@ToolParam(description = "List of product SPU IDs. Example: [1, 2, 3]") List<Long> ids) {
        log.info("[MCP Tool] product_spu_list called with ids={}", ids);
        return productSpuApi.getSpuList(ids).getCheckedData();
    }
}
