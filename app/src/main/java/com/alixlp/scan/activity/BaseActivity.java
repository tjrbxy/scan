package com.alixlp.scan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class BaseActivity extends Activity {
    protected final String LANGUAGE = "app_switch"; // 语言选择
    protected final String APP_CURR_BOX = "app_curr_box"; // 当前箱码
    protected final String APP_PACKING_GOODS = "app_packing_goods"; // 装箱商品
    protected final String APP_BOX_UN = "app_box_un"; // 已扫入的外箱码
    protected final String APP_GOODS_ID = "app_goods_id"; // 商品ID
    protected final String APP_DB = "app_db"; // API
    protected final String GOODS = "goods"; // 商品信息
    protected final String APP_PACKING_NUM = "app_packing_num"; //商品装箱数量
    protected final String APP_GOODS_KEY = "app_goods_key"; // 下拉商品对应的key
    protected final String APP_GOODS_NAME = "app_goods_name"; // 商品名称

    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // loading
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setMessage("处理中...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLoadingProgress();
        mLoadingDialog = null;
    }

    /**
     * 开始 loading
     */
    protected void startLoadingProgress() {
        mLoadingDialog.show();
    }

    /**
     * 停止 loading
     */
    protected void stopLoadingProgress() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
