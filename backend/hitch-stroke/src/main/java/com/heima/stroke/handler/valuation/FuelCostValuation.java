package com.heima.stroke.handler.valuation;

public class FuelCostValuation implements Valuation {
    private Valuation valuation;

    //燃油附加费¥1元
    public static final int FUELCOST = 1;

    public FuelCostValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        float beforeCost = valuation == null ? 0f : valuation.calculation(km);
        return beforeCost + FUELCOST;
    }
}
