package com.heima.commons.constant;

public interface HtichConstants {
    //账户相关服务
    String SESSION_TOKEN_KEY = "SESSION_TOKEN_KEY";
    String HEADER_ACCOUNT_KEY = "X_ACCOUNT_KEY";
    String SESSION_TOKEN_PREFIX = "SESSION_TOKEN_";
    String ACCOUNT_TOKEN_PREFIX = "ACCOUNT_TOKEN_";


    //行程相关
    /*
    Key前缀	                    存储类型	    用途	                                    示例数据
    STROKE_GEO_PREFIX	        GEO	        存储行程的地理位置信息	                    trip_1001 -> (116.4, 39.9)
    STROKE_GEO_DISTANCE_PREFIX	Hash	    存储两次行程的距离信息（起点距离和终点距离）	"trip_1001" -> { "trip_2002": "3.2:4.1" }
    STROKE_GEO_ZSET_PREFIX	    ZSet	    存储匹配行程的评分	                        "trip_1001" -> { "trip_2002": 95.5 }
    STROKE_INVITE_PREFIX	    Hash	    存储行程的邀请状态	                        "trip_1001" -> { "trip_2002": "1" }
     */
    //出行直径范围
    float STROKE_DIAMETER_RANGE = 100;
    /* String STROKE_DATIL_PREFIX = "STROKE_DETAIL_";*/
    //邀请行程 hset 前缀
    String STROKE_INVITE_PREFIX = "STROKE_INVITE_";
    //司机GEO起点
    String STROKE_DRIVER_GEO_START = "STROKE_DRIVER_GEO_START";
    //司机GEO终点
    String STROKE_DRIVER_GEO_END = "STROKE_DRIVER_GEO_END";
    //乘客GEO起点
    String STROKE_PASSENGER_GEO_START = "STROKE_PASSENGER_GEO_START";
    //乘客GEO终点
    String STROKE_PASSENGER_GEO_END = "STROKE_PASSENGER_GEO_END";

    //GEO排序zset前缀
    String STROKE_GEO_ZSET_PREFIX = "STROKE_GEO_ZSET_";
    //GEO距离数据前缀
    String STROKE_GEO_DISTANCE_PREFIX = "STROKE_GEO_DISTANCE_";
    /**
     * 微信通知确认响应内容
     */
    String WX_NOTIFY_SUCCESSFUL_RESPONSE_RESULT = "<xml><return_code><![CDATA[SUCCESS]]></return_code ><return_msg ><![CDATA[OK]]></return_msg ></xml>";

    String NOTICE_COLLECTION = "NOTICE_COLLECTION";


    String STROKE_START_GEO = "hitch";

    String ACCOUNT_DEFAULT_AVATAR = "/web/img/default-header.jpg";
    String ACCOUNT_DEFAULT_PAYCODE = "/web/img/default-paycode.jpg";

    String BAIDU_AI_RESULT = "BAIDU_AI_RESULT";

    String IMAGE_SERVER_ADDR = "http://portal-hongbaoyu-java.itheima.net";


    static String getImageUrl(String imageUrl) {
        return IMAGE_SERVER_ADDR + imageUrl;
    }

}
