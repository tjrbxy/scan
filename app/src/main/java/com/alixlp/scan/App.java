package com.alixlp.scan;

import android.app.Application;

import com.alixlp.scan.utils.SPUtils;
import com.alixlp.scan.utils.T;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 提示信息
        T.init(this);
        // 本地存储
        SPUtils.init(this, "setting.scan");
    }
}
