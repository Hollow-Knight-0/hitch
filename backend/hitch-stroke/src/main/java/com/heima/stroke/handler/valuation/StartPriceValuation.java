package com.heima.stroke.handler.valuation;

public class StartPriceValuation implements Valuation {
    private Valuation valuation;

    //起步价：¥10元（包含3公里）
    public static final int START_PRICE = 10;

    public StartPriceValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        float beforeCost = valuation == null ? 0f : valuation.calculation(km);
        return beforeCost + START_PRICE;
    }
}
