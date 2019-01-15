package com.alixlp.scan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.alixlp.scan.R;
import com.alixlp.scan.utils.SPUtils;
import com.alixlp.scan.utils.T;
import com.alixlp.scan.model.GoodsInfo;
import com.alixlp.scan.model.GoodsResult;
import com.alixlp.scan.res.GoodsRes;
import com.alixlp.scan.utils.NetworkUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class SettingActivity extends BaseActivity {


    private Button btnSave; // 保存
    private EditText appDb; // 设置url
    private Switch aSwitch;
    private Spinner spinner;
    private TextView appPackingNum;

    private ListView mListView;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initEvent();
        initSet();
    }

    private void initEvent() {
    }

    // 创建option菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载资源
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }

    // 菜单选择按钮事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int goodsId = (int) SPUtils.getInstance().get(APP_GOODS_ID, 0);
        switch (item.getItemId()) {
            case R.id.app_empty:
                Log.d(TAG, "onOptionsItemSelected: " + APP_CURR_BOX + goodsId);
                SPUtils.getInstance().put(APP_CURR_BOX + goodsId, "");
                SPUtils.getInstance().put(APP_PACKING_GOODS + goodsId, "");
                SPUtils.getInstance().put(APP_BOX_UN, "");
                T.showToast("数据已清空");
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        btnSave = (Button) findViewById(R.id.app_save);
        appDb = (EditText) findViewById(R.id.app_db);
        spinner = (Spinner) findViewById(R.id.spinner); // 下拉框
        appPackingNum = (TextView) findViewById(R.id.app_packing_num);
        aSwitch = (Switch) findViewById(R.id.app_switch);
    }


    private void initSet() {
        // 获取本地缓存
        String dbStr = (String) SPUtils.getInstance().get(APP_DB, "new.913fang.com");
        if (dbStr.length() > 1) {
            appDb.setText(dbStr);
            btnSave.setText(R.string.app_update);
        }
        String goods = (String) SPUtils.getInstance().get(APP_GOODS, "");
        if (goods.length() > 10) {
            GoodsResult goodsResult = new GoodsResult();
            try {
                JSONObject jsonObject = new JSONObject(goods);
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");
                goodsResult.setStatus(status);
                goodsResult.setMessage(message);
                List<GoodsInfo> goodsInfos = new ArrayList<>();
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    GoodsInfo goodsInfo = new GoodsInfo();
                    JSONObject tempJsonObject = (JSONObject) dataArray.get(i);
                    goodsInfo.setGoodsId(tempJsonObject.getInt("id"));
                    goodsInfo.setGoodsName(tempJsonObject.getString("name"));
                    goodsInfo.setGoodsNum(tempJsonObject.getInt("num"));
                    goodsInfos.add(goodsInfo);
                }
                goodsResult.setGoodsInfo(goodsInfos);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String str = "装箱数:<font color =red >" + SPUtils.getInstance().get(APP_PACKING_NUM, 0)
                    + "</font>";
            appPackingNum.setText(Html.fromHtml(str));
            spinner.setAdapter(new AppListAdapter(SettingActivity.this, goodsResult.getGoodsInfo
                    ()));

            spinner.setSelection((Integer) SPUtils.getInstance().get(APP_GOODS_KEY, 0), true);
        }
        ;
        aSwitch.setChecked((Boolean) SPUtils.getInstance().get(APP_LANGUAGE, false));
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkAvailable(SettingActivity.this)) {
                    T.showToast("请在有网络的情况下使用此按钮。");
                    return;
                }
                // 当按钮点击时调用
                String db = appDb.getText().toString();
                SPUtils.getInstance().put(APP_DB, db);
                btnSave.setText(R.string.app_update);
                T.showToast("数据库被设置为(" + db + ")");
                // 获取数据
                getGoodsData();
                // 获取下拉框数据

            }
        });
        // 选择框

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = (GoodsInfo) parent.getItemAtPosition(position);
                String str = "装箱数:<font color =red >" + goodsInfo.getGoodsNum() + "</font>";
                appPackingNum.setText(Html.fromHtml(str));
                SPUtils.getInstance().put(APP_GOODS_KEY, position);
                SPUtils.getInstance().put(APP_GOODS_ID, goodsInfo.getGoodsId());
                SPUtils.getInstance().put(APP_GOODS_NAME, goodsInfo.getGoodsName());
                SPUtils.getInstance().put(APP_PACKING_NUM, goodsInfo.getGoodsNum());
                T.showToast("将要扫描信息：(" + goodsInfo.toString() + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                T.showToast("选择信息为1：(" + parent + ")");
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    T.showToast("您选择了粤语提示！");
                } else {
                    T.showToast("使用默认普通话提示！");
                }
                SPUtils.getInstance().put(APP_LANGUAGE, isChecked);
            }
        });
    }

    private void getGoodsData() {
        ;
        String url = "http://" + SPUtils.getInstance().get(APP_DB, "new.913fang.com") + "/api" +
                ".php/goods";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: " + call);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SPUtils.getInstance().put(APP_GOODS, response);
                        Log.d(TAG, "onResponse: " + response);
                        Gson gson = new Gson();
                        GoodsRes res = gson.fromJson(response, GoodsRes.class);

                        Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "onResponse: res" + res.getData());
                    }
                });

    }

    public class AppListAdapter extends BaseAdapter {

        private Context mContext;
        private List<GoodsInfo> mInfos;

        public AppListAdapter(Context context, List<GoodsInfo> infos) {
            mContext = context;
            mInfos = infos;
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_demo_list, null);
                // 获取控件
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.title_text_view);
                // viewHolder.avatarImageView = (ImageView) convertView.findViewById(R.id
                // .icon_image_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // 和数据之间进行绑定
            viewHolder.nameTextView.setText(mInfos.get(position).getGoodsName());
            // viewHolder.avatarImageView.setVisibility(View.GONE);

            return convertView;
        }

        class ViewHolder {
            ImageView avatarImageView;
            TextView nameTextView;
        }
    }


    public class AppAsyncTask extends AsyncTask<Void, Integer, String> {

        // 新进程处理
        @Override
        protected String doInBackground(Void... params) {
            String url = "http://" + SPUtils.getInstance().get(APP_DB, "new.913fang.com") + "/api" +
                    ".php/goods";
            return request(url);
        }

        // 请求成功
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            GoodsResult goodsResult = new GoodsResult();
            try {
                JSONObject jsonObject = new JSONObject(result);
                SPUtils.getInstance().put(APP_GOODS, result);
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");
                goodsResult.setStatus(status);
                goodsResult.setMessage(message);
                List<GoodsInfo> goodsInfos = new ArrayList<>();
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    GoodsInfo goodsInfo = new GoodsInfo();
                    JSONObject tempJsonObject = (JSONObject) dataArray.get(i);
                    goodsInfo.setGoodsId(tempJsonObject.getInt("id"));
                    goodsInfo.setGoodsName(tempJsonObject.getString("name"));
                    goodsInfo.setGoodsNum(tempJsonObject.getInt("num"));
                    goodsInfos.add(goodsInfo);
                }
                goodsResult.setGoodsInfo(goodsInfos);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String str = "装箱数:<font color =red >" + SPUtils.getInstance().get(APP_PACKING_NUM, 0)
                    + "</font>";
            appPackingNum.setText(Html.fromHtml(str));
            spinner.setAdapter(new AppListAdapter(SettingActivity.this, goodsResult.getGoodsInfo
                    ()));
            spinner.setSelection((Integer) SPUtils.getInstance().get(APP_GOODS_KEY, 0), true);
        }


        // http 请求
        private String request(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.setRequestMethod("GET");  // GET POST
                connection.connect();
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String result = null;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(connection
                            .getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                } else {
                    // TODO:
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
