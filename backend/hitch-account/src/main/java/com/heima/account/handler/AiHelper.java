package com.heima.account.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.commons.utils.RequestUtils;
import com.heima.modules.po.AuthenticationPO;
import com.heima.modules.po.VehiclePO;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class AiHelper {
    @Value("${baidu.apikey}")
    private String API_KEY;
    @Value("${baidu.secretkey}")
    private String SECRET_KEY;

//    public static final Logger logger = LoggerFactory.getLogger(AiHelper.class);

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


//    public static void main(String[] args) throws IOException {
//        String code = new AiHelper().getLicense(null);
//        System.out.println(code);
//    }

    /*
    图像识别，获取车牌信息
    文档（行驶证识别）：https://cloud.baidu.com/doc/OCR/s/yk3h7y3ks
    文档（车牌识别）：https://cloud.baidu.com/doc/OCR/s/ck3h7y191
    获取车辆照片url
    将url下载到某个临时文件夹
    将文件编码为base64 （不能直接使用url，因为不是公网存储）
    调百度AI接口，返回对应信息
    对比：行驶证车牌 和 车辆车牌是否一致
    如果一致，设置车牌信息，认证通过，身份变更为车主
    */
    public String getCarLicense(VehiclePO vehiclePO) throws IOException {
        //获取token
        String accessToken = getAccessToken();
        //根据车辆照片获得车牌号
        String numberByCarFront = getNumberByCarFront(accessToken, vehiclePO.getCarFrontPhoto());
        //根据行驶证获得车牌号
        String numberByCarBack = getNumberByCarBack(accessToken, vehiclePO.getCarBackPhoto());
        //判断是否一致
        if (!numberByCarFront.equals(numberByCarBack) || StringUtils.isAnyEmpty(numberByCarFront, numberByCarBack)) {
            return null;
        }
        return numberByCarFront;
    }

    /**
     * 身份证照片识别并解析
     * @param authenticationPO 包含身份证照片
     * @return 身份证照片解析的数据
     */
    public AuthenticationPO getUserLicense(AuthenticationPO authenticationPO) throws IOException {
        String accessToken = getAccessToken();

        //请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=" + accessToken;
        //请求参数（base64数据）
        FormBody formBody = new FormBody.Builder()
                .add("image", imageUrlToBase64(authenticationPO.getCardIdFrontPhoto()))
                .add("id_card_side","front")
                .build();
        //构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        //解析数据
        Response response = HTTP_CLIENT.newCall(request).execute();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(response.body().string());

//        //用户照片
//        String photoBase64 = jsonNode.path("photo").asText();

        //用户信息
        JsonNode wordsResult = jsonNode.path("words_result");
        authenticationPO.setUseralias(wordsResult.path("姓名").path("words").asText());
        authenticationPO.setCardId(wordsResult.path("公民身份号码").path("words").asText());
        authenticationPO.setBirth(wordsResult.path("出生").path("words").asText());
        authenticationPO.setStatus("1");
        authenticationPO.setCreatedBy(RequestUtils.getCurrentUserId());
        authenticationPO.setUpdatedBy(RequestUtils.getCurrentUserId());

        return authenticationPO;
    }

    /**
     * 获取 accessToken
     */
    private String getAccessToken() throws IOException {
        String url = "https://aip.baidubce.com/oauth/2.0/token"
                + "?grant_type=client_credentials"
                + "&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY;

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(response.body().string());
        return jsonNode.path("access_token").textValue();
    }

    /**
     * 根据车辆前部照片获取车牌号
     */
    private String getNumberByCarFront(String accessToken, String carFrontPhoto) throws IOException {
        //请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate?access_token=" + accessToken;
        //请求参数（base64数据）
        FormBody formBody = new FormBody.Builder()
                .add("image", imageUrlToBase64(carFrontPhoto))
                .build();
        //构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        //解析数据
        Response response = HTTP_CLIENT.newCall(request).execute();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(response.body().string());
        return jsonNode.path("words_result").path("number").asText();
    }

    /**
     * 根据行驶证照片获取车牌号
     */
    private String getNumberByCarBack(String accessToken, String carBackPhoto) throws IOException {
        //请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/vehicle_license?access_token=" + accessToken;
        //请求参数（base64数据）
        FormBody formBody = new FormBody.Builder()
                .add("image", imageUrlToBase64(carBackPhoto))
                .build();
        //构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        //解析数据
        Response response = HTTP_CLIENT.newCall(request).execute();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(response.body().string());
        return jsonNode.path("words_result").path("号牌号码").path("words").asText();
    }

    /**
     * 将图片url转换为Base64字符串
     *
     * @param imageUrl 图片的URL
     * @return Base64编码的字符串
     */
    public static String imageUrlToBase64(String imageUrl) throws IOException {
        // 发送GET请求下载图片
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        Response response = HTTP_CLIENT.newCall(request).execute();

        // 读取图片流
        InputStream inputStream = response.body().byteStream();

        // 读取所有字节
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        // 获取字节数组
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // 转换为Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
