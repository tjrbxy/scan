package com.alixlp.scan.biz;

import com.alixlp.scan.utils.SPUtils;

public class BaseBiz {

    private String APP_DB = "app_db";
    protected final String API_URL = "https://" + SPUtils.getInstance().get(APP_DB, "");
}
