package com.alixlp.scan.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alixlp.scan.R;
import com.alixlp.scan.biz.GoodsBiz;
import com.alixlp.scan.json.Goods;
import com.alixlp.scan.net.CommonCallback;
import com.alixlp.scan.utils.GsonUtil;
import com.alixlp.scan.utils.NetworkUtil;
import com.alixlp.scan.utils.SPUtils;
import com.alixlp.scan.utils.T;
import com.google.gson.reflect.TypeToken;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingActivity extends BaseActivity {

    @BindView(R.id.app_save)
    Button mBtnSave;
    @BindView(R.id.app_db)
    EditText appDb;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.app_packing_num)
    TextView appPackingNum;
    @BindView(R.id.app_switch)
    com.suke.widget.SwitchButton mBtnSwitch;
    // Switch mBtnSwitch;
    private static final String TAG = "SettingActivity-app";

    private GoodsBiz mGoodsBiz = new GoodsBiz();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initSet();
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
        if (item.getItemId() == R.id.app_empty) {
            Log.d(TAG, "onOptionsItemSelected: " + APP_CURR_BOX + goodsId);
            SPUtils.getInstance().put(APP_CURR_BOX + goodsId, "");
            SPUtils.getInstance().put(APP_PACKING_GOODS + goodsId, "");
            SPUtils.getInstance().put(APP_BOX_UN, "");
            T.showToast("数据已清空");
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSet() {
        // 获取本地缓存
        String dbStr = (String) SPUtils.getInstance().get(APP_DB, "demo.masaic.net");
        if (dbStr.length() > 1) {
            appDb.setText(dbStr);
            mBtnSave.setText(R.string.app_update);
        }
        String jsonGoods = (String) SPUtils.getInstance().get(APP_GOODS, "");
        if (jsonGoods.length() > 10) {

            List<Goods> goods = GsonUtil.getGson().fromJson(jsonGoods, new TypeToken<List<Goods>>() {
            }.getType());
            String str = "每箱的数量 :<font color =red >" + SPUtils.getInstance().get(APP_PACKING_NUM, 0)
                    + "</font>";
            appPackingNum.setText(Html.fromHtml(str));
            spinner.setAdapter(new AppListAdapter(SettingActivity.this, goods));
            spinner.setSelection((Integer) SPUtils.getInstance().get(APP_GOODS_KEY, 0), true);
        }
        mBtnSwitch.setChecked((Boolean) SPUtils.getInstance().get(APP_LANGUAGE, false));
        // 选择框

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Goods goodsInfo = (Goods) parent.getItemAtPosition(position);
                String str = "每箱的数量 :<font color =red >" + goodsInfo.getNum() + "</font>";
                appPackingNum.setText(Html.fromHtml(str));
                SPUtils.getInstance().put(APP_GOODS_KEY, position);
                SPUtils.getInstance().put(APP_GOODS_ID, goodsInfo.getId());
                SPUtils.getInstance().put(APP_GOODS_NAME, goodsInfo.getName());
                SPUtils.getInstance().put(APP_PACKING_NUM, goodsInfo.getNum());
                T.showToast("选中信息：(" + goodsInfo.getName() + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                T.showToast("选择信息为1：(" + parent + ")");
            }
        });
        mBtnSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                if (isChecked) {
                    T.showToast("粤语已开启！");
                } else {
                    T.showToast("粤语已关闭！");
                }
                SPUtils.getInstance().put(APP_LANGUAGE, isChecked);
            }
        });

    }

    public class AppListAdapter extends BaseAdapter {

        private Context mContext;
        private List<Goods> mInfos;

        public AppListAdapter(Context context, List<Goods> infos) {
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
            viewHolder.nameTextView.setText(mInfos.get(position).getName());
            // viewHolder.avatarImageView.setVisibility(View.GONE);

            return convertView;
        }

        class ViewHolder {
            ImageView avatarImageView;
            TextView nameTextView;
        }
    }

    @OnClick({R.id.app_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.app_save:
                if (!NetworkUtil.isNetworkAvailable(SettingActivity.this)) {
                    T.showToast("请在有网络的情况下使用此按钮。");
                    return;
                }
                // 当按钮点击时调用
                String db = appDb.getText().toString();
                SPUtils.getInstance().put(APP_DB, db);
                mBtnSave.setText(R.string.app_update);
                T.showToast("数据库被设置为(" + db + ")");
                // 获取数据
                mGoodsBiz.goodsList(new CommonCallback<List<Goods>>() {
                    @Override
                    public void onError(Exception e) {
                        T.showToast(e.getMessage());
                        return;
                    }

                    @Override
                    public void onSuccess(List<Goods> response, String info) {
                        try {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject tmpObj;
                            for (int i = 0; i < response.size(); i++) {
                                tmpObj = new JSONObject();
                                tmpObj.put("id", response.get(i).getId());
                                tmpObj.put("name", response.get(i).getName());
                                tmpObj.put("num", response.get(i).getNum());
                                jsonArray.put(tmpObj);
                            }
                            String goods = jsonArray.toString();
                            // 设置商品
                            SPUtils.getInstance().put(APP_GOODS, goods);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

}
