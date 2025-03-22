package com.heima.stroke.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.commons.domin.bo.RoutePlanResultBO;
import com.heima.commons.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BaiduMapClient {
    @Value("${baidu.map.api}")
    private String api;
    @Value("${baidu.map.ak}")
    private String ak;

    private final static Logger logger = LoggerFactory.getLogger(BaiduMapClient.class);

    //TODO:任务3.2-调百度路径计算两点间的距离，和预估抵达时长
    public RoutePlanResultBO pathPlanning(String origins, String destinations) {
        //对接文档：https://lbs.baidu.com/faq/api?title=webapi/routchtout-drive
//        String url = api + "?origins=" + origins + "&destinations=" + destinations + "&ak=" + ak;

        Map<String,String> reqMap = new HashMap<>();
        reqMap.put("origins", origins);
        reqMap.put("destinations", destinations);
        reqMap.put("ak",ak);
        logger.info("send to Baidu:{}",reqMap);
        String result = null;
        try {
            result = HttpClientUtils.doGet(api, reqMap);
            logger.info("get from Baidu:{}",result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        RoutePlanResultBO routePlanResultBO = null;
        JSONObject jsonObject = JSON.parseObject(result);
        if(jsonObject != null && jsonObject.getString("status").equals("0")){
            JSONArray resultArray = jsonObject.getJSONArray("result");
            if(resultArray != null && !resultArray.isEmpty()){
                routePlanResultBO = resultArray.toJavaList(RoutePlanResultBO.class).get(0);
            }
        }

        return routePlanResultBO;
    }
}
