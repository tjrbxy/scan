package com.alixlp.scan.biz;

import android.util.Log;

import com.alixlp.scan.json.Goods;
import com.alixlp.scan.net.CommonCallback;
import com.alixlp.scan.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

public class GoodsBiz extends BaseBiz {

    private static final String TAG = "GoodsBiz-app";

    public GoodsBiz() {
        super();
    }

    /**
     * @param commonCallback
     */
    public void goodsList(CommonCallback<List<Goods>> commonCallback) {

        Log.d(TAG, "goodsList: " + this.API_URL);
        String url = "https://" + SPUtils.getInstance().get(this.APP_DB, "") + "/api.php/goods";
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(commonCallback);
    }
}
