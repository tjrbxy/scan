package com.alixlp.scan.biz;

import android.util.Log;

import com.alixlp.scan.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.List;

public class CodeBiz extends BaseBiz {
    private static final String TAG = "CodeBiz-app";
    private final String APP_DB = "app_db";

    /**
     * @param name
     * @param path
     * @param commonCallback
     */
    public void uploadFile(String device, String name, File path,
                           CommonCallback<List>
                                   commonCallback) {
        String url = API_URL + "/api.php/upload";
        Log.d(TAG, "uploadFile: " + url);
        OkHttpUtils
                .post()
                .url(url)
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
