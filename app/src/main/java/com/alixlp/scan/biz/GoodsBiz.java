package com.alixlp.scan.biz;

import com.alixlp.scan.json.Goods;
import com.alixlp.scan.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

public class GoodsBiz extends BaseBiz {

    public GoodsBiz() {
        super();
    }

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
