package com.alixlp.scan.biz;

import com.alixlp.scan.json.Goods;
import com.alixlp.scan.net.CommonCallback;
import com.alixlp.scan.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsBiz extends BaseBiz {

    /**
     * @param commonCallback
     */
    public void goodsList(CommonCallback<List<Goods>> commonCallback) {

        String url = this.API_URL + "/api.php/goods";
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(commonCallback);
    }
}
