package com.alixlp.scan.biz;

import android.util.Log;

import com.alixlp.scan.config.AppConfig;
import com.alixlp.scan.utils.SPUtils;

class BaseBiz {

    private static final String TAG = "BaseBiz-app";

    String APP_DB = "app_db";
    String API_URL;
    {
        API_URL = AppConfig.REQUEST_SCHEME + SPUtils.getInstance().get(APP_DB, "");
        Log.d(TAG, "instance initializer: "+API_URL);
    }
}
