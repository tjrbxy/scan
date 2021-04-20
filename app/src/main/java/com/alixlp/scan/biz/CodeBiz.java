package com.alixlp.scan.biz;

import com.alixlp.scan.config.AppConfig;
import com.alixlp.scan.utils.CommonCallback;
import com.alixlp.scan.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeBiz extends BaseBiz {
    private static final String TAG = "CodeBiz-app";

    public CodeBiz() {
        super();
    }

    /**
     * 队列上传文件 2019.12.29
     *
     * @param device
     * @param path
     * @param commonCallback
     */
    public void uploadCode(String device, File path, CommonCallback<List> commonCallback) {

        Map header = new HashMap();
        header.put("APPID", (String) SPUtils.getInstance().get(this.APP_DB, ""));
        OkHttpUtils
                .post()
                .url(AppConfig.API + "/api/v1/code")
                .headers(header)
                .addParams("device", device)
                .addFile("goods", "goods.txt", path)
                .build()
                .execute(commonCallback);
    }
}
