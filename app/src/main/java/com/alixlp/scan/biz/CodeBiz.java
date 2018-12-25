package com.alixlp.scan.biz;

import android.os.DropBoxManager;
import android.util.Log;

import com.alixlp.scan.net.CommonCallback;
import com.alixlp.scan.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.HasParamsable;

import java.io.File;
import java.util.List;

public class CodeBiz {
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
        String url = "http://" + SPUtils.getInstance().get(APP_DB, "") + "/api.php/upload";
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
        String url = "http://" + SPUtils.getInstance().get(APP_DB, "") + "/api.php/code";
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
        String url = "http://" + SPUtils.getInstance().get(APP_DB, "") + "/api.php/code/insertCode";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("filename", filename)
                .build()
                .execute(commonCallback);
    }
}
