package cn.qihangerp.open.shein.helper;

import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.OrderDetailRequest;
import cn.qihangerp.open.shein.helper.model.OrderDetailResponse;
import cn.qihangerp.open.shein.helper.model.OrderListRequest;
import cn.qihangerp.open.shein.helper.model.OrderListResponse;
import cn.qihangerp.open.shein.helper.request.PurchaseOrderListRequest;
import cn.qihangerp.open.shein.helper.response.SheinPurchaseOrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SheinOrderApiHelper {

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();



    public static SheinPurchaseOrderResponse queryPurchaseOrderList(String serverUrl,String appKey,String appSecret,PurchaseOrderListRequest request) throws IOException {
        String url = serverUrl + "/open-api/order/purchase-order-infos";

        // ?
        Headers headers = SheinSignatureHelper.createHeadersJson(appKey,appSecret,url);

        // ?
        String requestJson = objectMapper.writeValueAsString(request);
        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("pageNumber", request.getPageNumber().toString());
        urlBuilder.addQueryParameter("pageSize", request.getPageSize().toString());
        if(request.getType()!=null){
            urlBuilder.addQueryParameter("type", request.getType().toString());
        }
        if(StringUtils.hasText(request.getUpdateTimeStart())) {
            urlBuilder.addQueryParameter("updateTimeStart", request.getUpdateTimeStart());
        }
        if(StringUtils.hasText(request.getUpdateTimeEnd())) {
            urlBuilder.addQueryParameter("updateTimeEnd", request.getUpdateTimeEnd());
        }
         url = urlBuilder.build().toString();
        // 
        Request httpRequest = new Request.Builder()
                .url(url)
                .headers(headers)
//                .post(requestBody)
                .build();
        SheinPurchaseOrderResponse sheinPurchaseOrderResponse=null;
        // ?
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            if (!response.isSuccessful()) {
                sheinPurchaseOrderResponse = new SheinPurchaseOrderResponse();
                sheinPurchaseOrderResponse.setCode(1400);
                sheinPurchaseOrderResponse.setMsg(responseBody);
                log.error("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }else {
                sheinPurchaseOrderResponse = objectMapper.readValue(responseBody, SheinPurchaseOrderResponse.class);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            e.printStackTrace();
        }
        return sheinPurchaseOrderResponse;
    }



    /**
     * 
     *
     * @param request 
     * @return SKU
     * @throws IOException API
     */
    public static OrderListResponse queryOrderList(OrderListRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/order/order-list";

        // ?
        Headers headers = SheinSignatureHelper.createHeaders(request.getAppKey(),request.getAppSecret(),url);

        // ?
        String requestJson = objectMapper.writeValueAsString(request);
        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));

        // 
        Request httpRequest = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        // ?
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }

            return objectMapper.readValue(responseBody, OrderListResponse.class);
        }
    }

    /**
     * 
     *
     * @param request 
     * @return SKU
     * @throws IOException API
     */
    public static OrderDetailResponse queryOrderDetail(OrderDetailRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/order/order-detail";

        // ?
        Headers headers = SheinSignatureHelper.createHeaders(request.getAppKey(),request.getAppSecret(),url);

        // ?
        String requestJson = objectMapper.writeValueAsString(request);
        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));

        // 
        Request httpRequest = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        // ?
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }

            return objectMapper.readValue(responseBody, OrderDetailResponse.class);
        }
    }


} 