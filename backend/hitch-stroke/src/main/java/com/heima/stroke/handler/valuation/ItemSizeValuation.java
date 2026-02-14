package com.heima.stroke.handler.valuation;

/**
 * 货物规格系数装饰器
 * 根据itemSize字段调整价格：
 * 1.小件 乘以0.9
 * 2.中件 乘以1.2
 * 3.大件 乘以1.5
 */
public class ItemSizeValuation implements Valuation {
    private Valuation valuation;
    private String itemSize;

    public ItemSizeValuation(Valuation valuation, String itemSize) {
        this.valuation = valuation;
        this.itemSize = itemSize;
    }

    @Override
    public float calculation(float meters) {
        float baseCost = valuation == null ? 0f : valuation.calculation(meters);
        
        //根据货物规格调整价格
        float coefficient = getItemSizeCoefficient();
        float finalCost = baseCost * coefficient;
        
        //保留一位小数
        return Math.round(finalCost * 10) / 10.0f;
    }

    /**
     * 获取货物规格系数
     * @return 系数
     */
    private float getItemSizeCoefficient() {
        if (itemSize == null || itemSize.isEmpty()) {
            return 1.0f; // 默认中件
        }
        
        switch (itemSize) {
            case "1": // 小件
                return 0.9f;
            case "2": // 中件
                return 1.2f;
            case "3": // 大件
                return 1.5f;
            default:
                return 1.2f; // 默认中件
        }
    }
}