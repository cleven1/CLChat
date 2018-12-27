package com.cleven.clchat.API;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.Map;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

public class OkGoUtil {
    private CLNetworkCallBack callBack;
    public CLNetworkCallBack getCallBack() {
        return callBack;
    }
    public void setCallBack(CLNetworkCallBack callBack) {
        this.callBack = callBack;
    }

    public interface CLNetworkCallBack{
        void onSuccess(Map response);
        void onFailure(Map error);
    }

    public static void getRequets(String url, Object tag, HttpParams params, final CLNetworkCallBack callback) {
        OkGo.<String>get(url)
                .tag(tag)
                .params(params)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        final JSONObject parseMap = (JSONObject)JSON.parse(response.body());
                        if (parseMap.get("error_code").equals("0")){ //请求成功
                            if (callback != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(parseMap);
                                    }
                                });
                            }
                        }else {
                            if (callback != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFailure(parseMap);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        final JSONObject parseMap = new JSONObject();
                        parseMap.put("error_code","4500");
                        parseMap.put("error_msg","请求失败");
                        if (callback != null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onFailure(parseMap);
                                }
                            });
                        }
                    }
                });
    }

    public static void postRequest(String url, Object tag, HttpParams params, final CLNetworkCallBack callback) {

        OkGo.<String>post(url)
                .tag(tag)
                .params(params)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject parseMap = (JSONObject)JSON.parse(response.body());
                        if (parseMap.get("error_code").equals("0")){ //请求成功
                            if (callback != null){
                                callback.onSuccess(parseMap);
                            }
                        }else {
                            if (callback != null){
                                callback.onFailure(parseMap);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        JSONObject parseMap = new JSONObject();
                        parseMap.put("error_code","4500");
                        parseMap.put("error_msg","请求失败");
                        if (callback != null){
                            callback.onFailure(parseMap);
                        }
                    }
                });
    }
}
