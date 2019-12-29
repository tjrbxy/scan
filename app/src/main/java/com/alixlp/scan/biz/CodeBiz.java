package com.alixlp.scan.biz;

import android.util.Log;

import com.alixlp.scan.net.CommonCallback;
import com.alixlp.scan.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeBiz extends BaseBiz {
    private static final String TAG = "CodeBiz-app";

    /**
     * 队列上传文件 2019.12.29
     *
     * @param device
     * @param path
     * @param commonCallback
     */
    public void uploadCode(String device, File path, CommonCallback<List> commonCallback) {
        String url = this.API + "/api/v1/code";
        Map header = new HashMap();
        header.put("APPID", (String) SPUtils.getInstance().get(this.APP_DB, ""));
        OkHttpUtils
                .post()
                .url(url)
                .headers(header)
                .addParams("device", device)
                .addFile("goods", "goods.txt", path)
                .build()
                .execute(commonCallback);
    }

    /**
     * @param name
     * @param path
     * @param commonCallback
     */
    public void uploadFile(String device, String name, File path,
                           CommonCallback<List>
                                   commonCallback) {
        String url = this.API + "/api/v1/code";
        //  String url = API_URL + "/api.php/upload";
        Log.d(TAG, "uploadFile: " + url);
        Map header = new HashMap();
        header.put("APPID", "ay.alixlp.com");
        OkHttpUtils
                .post()
                .url(url)
                .headers(header)
                .addParams("device", device)
                .addFile(name, "goods.txt", path)
                .build()
                .execute(commonCallback);
    }


    public void uploadCode(String code, String goodsId, String boxNum, String filename,
                           CommonCallback<List> commonCallback) {
        String url = API_URL + "/api.php/code";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("code", code)
                .addParams("goodsId", goodsId + "")
                .addParams("boxNum", boxNum)
                .addParams("filename", filename)
                .build()
                .execute(commonCallback);
    }

    public void uploadInsertCode(String filename, CommonCallback<List> commonCallback) {
        String url = API_URL + "/api.php/code/insertCode";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("filename", filename)
                .build()
                .execute(commonCallback);
    }
}
