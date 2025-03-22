package com.heima.stroke.handler.valuation;

public class BasicValuation implements Valuation {
    private Valuation valuation;

    //¥2.3元/公里
    public static final float BASIC = 2.3f;

    public BasicValuation(Valuation valuation){
        this.valuation = valuation;
    }

    @Override
    public float calculation(float km) {
        float beforeCost = valuation == null ? 0f : valuation.calculation(km);
        //不足1km按1km计算
        int ceil = (int) Math.ceil(km);
        if(ceil > 3){
            ceil = ceil - 3;
            return beforeCost + ceil * BASIC;
        }
        return beforeCost;
    }
}


