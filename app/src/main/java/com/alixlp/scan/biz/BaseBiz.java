package com.alixlp.scan.biz;

import android.util.Log;

import com.alixlp.scan.utils.SPUtils;

class BaseBiz {

    private static final String TAG = "BaseBiz-app";

    String APP_DB = "app_db";
    String API_URL;

    final String API;

    {
        API = "https://api.masaic.net";
        API_URL = "https://" + SPUtils.getInstance().get(APP_DB, "");
        Log.d(TAG, "instance initializer: " + API);
    }
}
