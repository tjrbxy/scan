package com.alixlp.scan.biz;


import com.alixlp.scan.config.AppConfig;
import com.alixlp.scan.entity.Goods;
import com.alixlp.scan.utils.CommonCallback;
import com.alixlp.scan.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

public class GoodsBiz extends BaseBiz {

    private static final String TAG = "GoodsBiz-app";

    public GoodsBiz() {
        super();
    }

    /**
     *
     * @param commonCallback
     */
    public void goodsList(CommonCallback<List<Goods>> commonCallback) {

        Object api = SPUtils.getInstance().get(APP_DB, "");
        OkHttpUtils
                .post()
                .url(AppConfig.REQUEST_SCHEME + api + "/api.php/goods")
                .tag(this)
                .build()
                .execute(commonCallback);
    }
}
