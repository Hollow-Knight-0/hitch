package com.heima.stroke.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    //TODO 2 调百度路径计算两点间的距离，和预估抵达时长
    /**
     * 计算两点间的距离，和预估抵达时长
     * @param origins 起点
     * @param destinations 终点
     * @return
     */
    public RoutePlanResultBO pathPlanning(String origins, String destinations) {
        //对接文档：https://lbs.baidu.com/faq/api?title=webapi/routchtout-drive
        //api + "?origins=" + origins + "&destinations=" + destinations + "&ak=" + ak;

        //拼装参数
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("origins", origins);
        reqMap.put("destinations", destinations);
        reqMap.put("ak", ak);
        logger.info("百度步行批量算路参数:{}", reqMap);
        String result = null;
        try {
            result = HttpClientUtils.doGet(api, reqMap);
            logger.info("百度步行批量算路返回:{}", result);
        } catch (Exception e) {
            logger.error("调用百度步行批量算路API失败", e);
            throw new RuntimeException(e);
        }

        // 解析响应结果
        RoutePlanResultBO routePlanResultBO = null;
        JSONObject jsonObject = JSON.parseObject(result);
        // 校验响应状态（注意：百度返回的status是int类型，原代码用String.equals("0")会匹配失败！）
        if (jsonObject != null && jsonObject.getIntValue("status") == 0) {
            JSONArray resultArray = jsonObject.getJSONArray("result");
            if (resultArray != null && !resultArray.isEmpty()) {
                // 将JSON数组第一个元素转为BO对象（如需全部结果，可返回List<RoutePlanResultBO>）
                routePlanResultBO = resultArray.getObject(0, RoutePlanResultBO.class);
            } else {
                logger.warn("百度步行批量算路返回结果为空");
            }
        } else {
            String errorMsg = jsonObject != null ? jsonObject.getString("message") : "未知错误";
            logger.error("百度步行批量算路失败，状态码：{}，错误信息：{}",
                    jsonObject != null ? jsonObject.getIntValue("status") : -1, errorMsg);
        }

        return routePlanResultBO;
    }
}
