package com.heima.stroke.handler.valuation;

/**
 * 取货计费装饰器
 * 起步价3元（包含1km），之后每超出500米加1元
 */
public class PickupValuation implements Valuation {
    private Valuation valuation;

    //起步价：¥3元（包含1km）
    public static final int START_PRICE = 3;
    //超出距离单价：每500米¥1元
    public static final float EXTRA_COST_PER_500M = 1.0f;

    public PickupValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float meters) {
        float beforeCost = valuation == null ? 0f : valuation.calculation(meters);
        
        //转换为千米进行计算
        float km = meters / 1000.0f;
        
        //不足1km按1km计算
        if (km <= 1.0f) {
            return beforeCost + START_PRICE;
        } else {
            //超过1km的部分，每500米加1元
            float extraMeters = meters - 1000.0f;
            //转换为500米的倍数，向上取整
            int extraUnits = (int) Math.ceil(extraMeters / 500.0f);
            return beforeCost + START_PRICE + (extraUnits * EXTRA_COST_PER_500M);
        }
    }
}