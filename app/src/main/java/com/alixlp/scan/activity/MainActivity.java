package com.alixlp.scan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alixlp.scan.R;
import com.alixlp.scan.biz.CodeBiz;
import com.alixlp.scan.utils.CommonCallback;
import com.alixlp.scan.utils.GsonUtil;
import com.alixlp.scan.utils.NetworkUtil;
import com.alixlp.scan.utils.SPUtils;
import com.alixlp.scan.utils.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  主
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.scan_result)//显示结果区域
            EditText showScanResult;
    @BindView(R.id.app_curr_code) // 当前装箱产品
            TextView appCurr;
    @BindView(R.id.app_success_box) // 当前装成功箱数
            TextView appScuess;
    @BindView(R.id.app_setting) // 设置
            Button btnSet;
    @BindView(R.id.app_upload) //上传
            Button btnUp;
    @BindView(R.id.app_delete) //上传
            Button appDelete;

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action
    // private final String APP_PACKING_SUCCESS_NUM = "app_packing_success_num";

    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid, pSoundid, bSoundid, iSoundid, rSoundid, rcSoundid, sSoundid,
            outsideSoundid,sanSoundid;
    private String barcodeStr;

    private boolean isUse = false;
    private boolean isScaning = false;

    private ArrayList<Object> codeInfos = new ArrayList<>();
    private String appPackingGoods;
    private String appCurrBox;
    private File path = Environment.getExternalStorageDirectory();
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    private int scanNum = 0;
    private String Imei = "";
    private static final String TAG = "MainActivity-app";


    private CodeBiz mCodeBiz = new CodeBiz();
    private final BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            // 震动
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);

            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0); // 码的长度
            barcodeStr = new String(barcode, 0, barcodelen); // 扫描结果
            Log.i(TAG, "扫描内容: " + barcodeStr);
            if (goodsInfo.getId() == 0){
                T.showToast("请配置数据库，选择商品!");
                return;
            }
            // 兼容二维码url
            if (barcodeStr.indexOf("?f=") != -1) {
                // 加入验证
                if (barcodeStr.indexOf((String) SPUtils.getInstance().get(APP_DB, "")) == -1) {
                    soundpool.play(outsideSoundid, 1, 1, 0, 0, 1);
                    return;
                }
                barcodeStr = barcodeStr.split("=")[1];
                barcodelen = barcodeStr.length();
            } else {
                if (barcodeStr.length() != 6 && barcodeStr.indexOf((String) SPUtils.getInstance()
                        .get(APP_DB, "")) == -1) {
                    soundpool.play(outsideSoundid, 1, 1, 0, 0, 1);
                    return;
                }
            }
            appCurrBox = (String) SPUtils.getInstance().get(APP_CURR_BOX + "_" + goodsInfo.getId(), ""); // 外箱码
            appPackingGoods = (String) SPUtils.getInstance().get(APP_PACKING_GOODS + "_" + goodsInfo.getId(), "");
            // 保存箱码
            if (barcodelen == 6 && appCurrBox.length() == 0) {
                appCurrBox = barcodeStr;
                // 已扫描箱码
                String app_box_un = (String) SPUtils.getInstance().get(APP_BOX_UN, "");
                Log.i(TAG, "外箱码：" + appCurrBox);
                box.add(appCurrBox);
                if (app_box_un.indexOf(appCurrBox) == -1) {
                    String f = (app_box_un.length() == 0) ? "" : String.valueOf(',');
                    // 保存扫入码的信息
                    SPUtils.getInstance().put(APP_BOX_UN, app_box_un + f + barcodeStr);
                    // 保存当前箱码
                    SPUtils.getInstance().put(APP_CURR_BOX + "_" + goodsInfo.getId(), barcodeStr);
                    showScanResult.setText(barcodeStr + "\n");
                } else {
                    Log.i(TAG, "外箱码重复：" + appCurrBox);
                    soundpool.play(rcSoundid, 1, 1, 0, 0, 1);
                }
                return;
            }
            // 提示扫外箱码
            if (appCurrBox.length() != 6) {
                soundpool.play(bSoundid, 1, 1, 0, 0, 1);
                return;
            }
            // 提示扫产品码
            if (appPackingGoods.length() == 0 && barcodelen != 12) {
                soundpool.play(pSoundid, 1, 1, 0, 0, 1);
                return;
            }
            // 扫产品码
            String jsonStr = "";
            if (barcodelen > 6 && appCurrBox.length() == 6) {
                // 判断最近3箱是否重复
                ArrayList<Object> limitCode = new ArrayList<>();
                String json = (String) SPUtils.getInstance().get(APP_GOODS_LIMIT + "_" + goodsInfo.getId(),"");
                if (json.length() > 5 && json.indexOf(barcodeStr) != -1){
                    LinkedList linkedList = gson.fromJson(json, LinkedList.class);
                    Iterator<Map> iterator = linkedList.iterator();
                    String boxNum = "[]";
                    while (iterator.hasNext()) {
                        Map next = iterator.next();
                        if (next.values().toString().indexOf(barcodeStr) != -1) {
                            boxNum = next.keySet().toString();
                        }
                    }
                    soundpool.play(sanSoundid, 1, 1, 0, 0, 1);
                    T.showToast("该码先前已装入"+boxNum+"箱，请检查！", Toast.LENGTH_LONG);
                    Log.i(TAG, "该码先前已装入"+boxNum+"箱，请检查！" );
                    return;
                }
                codeInfos = new ArrayList<>();
                if (appPackingGoods.length() < 1) {
                    codeInfos.add(barcodeStr);
                    jsonStr = GsonUtil.getGson().toJson(codeInfos);
                    SPUtils.getInstance().put(APP_PACKING_GOODS + "_" + goodsInfo.getId(), jsonStr);
                    scanNum++;
                } else {
                    Boolean flag = false;
                    List list = gson.fromJson(appPackingGoods, List.class);
                    Iterator<String> it = list.iterator();
                    while(it.hasNext()){
                        String x = it.next();
                        if(x.equals( barcodeStr )){
                            flag = true;
                        }else{
                            codeInfos.add(x);
                        }
                    }
                    // 重复扫码提示
                    if (flag) {
                        soundpool.play(rSoundid, 1, 1, 0, 0, 1);
                        return;
                    }
                    codeInfos.add(barcodeStr);
                    scanNum = codeInfos.size();
                    jsonStr = GsonUtil.getGson().toJson(codeInfos);
                    SPUtils.getInstance().put(APP_PACKING_GOODS + "_" + goodsInfo.getId(), jsonStr);
                }
            }
            if (scanNum < goodsInfo.getNum() && barcodelen == 6) {
                soundpool.play(iSoundid, 1, 1, 0, 0, 1);
                return;
            }
            // 显示扫描结果
            if (scanNum == goodsInfo.getNum()) {
                scanNum = 0;
                for (int i = 0; i < codeInfos.size(); i++) {
                    WriteStringToFile2(codeInfos.get(i) + "," + "_" + goodsInfo.getId() + "," + appCurrBox + "\n");
                }
                SPUtils.getInstance().put(APP_CURR_BOX + "_" + goodsInfo.getId(), "");
                SPUtils.getInstance().put(APP_PACKING_GOODS + "_" + goodsInfo.getId(), "");
                int appSuccessNum = (int) SPUtils.getInstance().get(APP_PACKING_SUCCESS_NUM, 0);
                SPUtils.getInstance().put(APP_PACKING_SUCCESS_NUM, appSuccessNum + 1);
                showScanResult.setText("");
                appScuess.setText("已完成箱数：" + (appSuccessNum + 1) + " 箱");
                Log.i(TAG, "装箱成功的商品：" +codeInfos.toString());
                LinkedList<Map> objects = new LinkedList<>(); // 容器
                String json = (String) SPUtils.getInstance().get(APP_GOODS_LIMIT + "_" + goodsInfo.getId(),"");
                if (json.length() > 5){
                    objects = gson.fromJson(json, LinkedList.class);
                    if (objects.size() > 2){
                        objects.removeFirst();
                    }
                    Log.i(TAG, "当前存入的数量："+ objects.size() + objects.toString());
                }
                HashMap<String, List> map = new HashMap<>();
                map.put( appCurrBox, codeInfos);
                objects.addLast(map);

//                ArrayList<Object> objects = new ArrayList<>();
//                String json = (String) SPUtils.getInstance().get(APP_GOODS_LIMIT + "_" + goodsInfo.getId(),"");
//                if (json.length() > 5){
//                    objects = gson.fromJson(json, ArrayList.class);
//                    if (objects.size() > 2){
//                        objects.remove(0);
//                    }
//                    Log.i(TAG, "当前存入的数量："+ objects.size() + objects.toString());
//                }
//                objects.add(codeInfos);
                SPUtils.getInstance().put(APP_GOODS_LIMIT + "_" + goodsInfo.getId(),gson.toJson(objects));
                Log.i(TAG, "总码数: " + objects.toString());
                soundpool.play(sSoundid, 1, 1, 0, 0, 1);
            } else {
                showScanResult.setText(showScanResult.getText().toString() + barcodeStr + "\n");
            }
            appCurr.setText("当前装箱（" + goodsInfo.getName() + "）：" +
                    scanNum + "/" + goodsInfo.getNum());
        }

    };

    public void WriteStringToFile2(String string) {
        File file = new File(path + "/goods.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(path + "/goods.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(string);
            bw.close();
            fw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        // 保持屏幕亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        // 绑定ID
        ButterKnife.bind(this);
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Imei = TelephonyMgr.getDeviceId();
        // 初始化视图
        initView();
        //  事件
        initEvent();
        // 震动提示
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // actionBar = getActionBar();
        // actionBar.show();
        // 显示结果区域

        // 聚焦并显示光标
        showScanResult.setFocusable(true);
        showScanResult.requestFocus();

    }

    // 事件
    private void initEvent() {
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 本地备份
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("MMdd.HH.mm.ss");
                copyFile(path + "/goods.txt", path + "/backupdata/" + Imei + "/" + ft.format
                        (date) + "/");
                // 服务器上备份
                startLoadingProgress();
                mCodeBiz.uploadCode(Imei, new File(path + "/goods.txt"), new CommonCallback<List>() {
                    @Override
                    public void onError(Exception e) {
                        stopLoadingProgress();
                        T.showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(List response, String info) {
                        stopLoadingProgress();
                        T.showToast(info);
                        Log.i(TAG, "onSuccess: " + info);
                        //重置已扫箱数为0
                        T.showToast("数据全部上传完成。");
                        SPUtils.getInstance().put(APP_PACKING_SUCCESS_NUM, 0);
                        appScuess.setText("已完成箱数：0 箱");
                        progressDialog.dismiss();
                        File file = new File(path + "/goods.txt");
                        if (file.exists()) {
                            Date date = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat
                                    ("yyyyMMdd-HH-mm-ss");
                            File newfile = new File(path + "/goods-" + ft.format
                                    (date) +
                                    ".txt");
                            file.renameTo(newfile);
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                T.showToast("已经取消，不会对数据进行任何处理！");
            }
        });
    }


    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                File newFile = new File(newPath);
                if (!newFile.exists()) {
                    newFile.mkdirs();
                }
                FileOutputStream fs = new FileOutputStream(newPath + "goods.txt");
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            Log.i(TAG, "copyFile: 复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    private void initView() {
        builder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(MainActivity.this);
    }

    /**
     * 上传询问框
     */
    private void showDialogMsg() {
        builder.setTitle("上传提示");
        builder.setMessage("是否上传数据？");
        builder.setCancelable(true);
        builder.create().show();
    }

    /**
     * 检查文件是否存在
     *
     * @return
     */
    private boolean checkFile() {
        String goodsPath = path + "/goods.txt";

        File goods = new File(goodsPath);
        if (!goods.exists()) {
            return true;
        }
        return false;
    }

    private void initData() {
        // 聚焦并显示光标
        appCurrBox = (String) SPUtils.getInstance().get(APP_CURR_BOX + "_" + goodsInfo.getId(), "");
// 已经完成装箱数量
        int appPackingSuccessNum = (int) SPUtils.getInstance().get(APP_PACKING_SUCCESS_NUM, 0);
        String result = appCurrBox.length() > 0 ? appCurrBox + "\n" : "";
        appPackingGoods = (String) SPUtils.getInstance()
                .get(APP_PACKING_GOODS + "_" + goodsInfo.getId(), "");
        int num = 0;
        if ( appPackingGoods.length() > 2){
            List list = gson.fromJson(appPackingGoods, List.class);
            Iterator<String> it = list.iterator();
            while(it.hasNext()){
                result += it.next() + "\n";
                num++;
            }
        }
        showScanResult.setText(result);
        showScanResult.setFocusable(true);
        showScanResult.requestFocus();
        appCurr.setText("当前装箱（" + goodsInfo.getName() + "）：" + num + "/" + goodsInfo.getNum());
        appScuess.setText("已完成箱数：" + appPackingSuccessNum + " 箱");
    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();

        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);

        // 判断当前语言种类
        if ((Boolean) SPUtils.getInstance().get(APP_LANGUAGE, false)) {
            // 粤语
            bSoundid = soundpool.load(this, R.raw.codeboxc, 1); // 箱码
            pSoundid = soundpool.load(this, R.raw.codeproductc, 1); //产品吗
            iSoundid = soundpool.load(this, R.raw.codeincompletec, 1); //不完整
            rSoundid = soundpool.load(this, R.raw.coderepeatc, 1); // 产品码重复
            rcSoundid = soundpool.load(this, R.raw.codeboxrepeatc, 1); // 产品码重复
            sSoundid = soundpool.load(this, R.raw.codesuccessc, 1); //完成
            outsideSoundid = soundpool.load(this, R.raw.ctoutsidesoundid, 1); // 系统外码
            sanSoundid = soundpool.load(this, R.raw.sanc, 1); // 3箱重复
        } else {
            // 普通话
            bSoundid = soundpool.load(this, R.raw.codebox, 1);
            pSoundid = soundpool.load(this, R.raw.codeproduct, 1);
            iSoundid = soundpool.load(this, R.raw.codeincomplete, 1);
            rSoundid = soundpool.load(this, R.raw.coderepeat, 1);
            rcSoundid = soundpool.load(this, R.raw.codeboxrepeat, 1); // 产品码重复
            sSoundid = soundpool.load(this, R.raw.codesuccess, 1);
            outsideSoundid = soundpool.load(this, R.raw.outsidesoundid, 1); // 系统外码
            sanSoundid = soundpool.load(this, R.raw.san, 1); // 3箱重复
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        // 注销Receiver
        unregisterReceiver(mScanReceiver);
    }

    /**
     * 新开或重新回到该页会执行
     */
    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        // 设置数据
        initData();  // 加载数据
        initScan();

        IntentFilter filter = new IntentFilter();
        int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID
                .WEDGE_INTENT_DATA_STRING_TAG};
        String[] value_buf = mScanManager.getParameterString(idbuf);
        if (value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
            filter.addAction(value_buf[0]);
        } else {
            filter.addAction(SCAN_ACTION);
        }
        // 注册 Receiver
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.app_setting, R.id.app_upload, R.id.app_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // 点击设置按钮跳转到系统设置页面
            case R.id.app_setting:
                //创建一个Intent对象
                Intent intent = new Intent();
                //调用setClass方法指定启动某一个Activity
                intent.setClass(MainActivity.this, SettingActivity.class);
                //调用startActivity
                startActivity(intent);
                break;
            // 上传按钮事件
            case R.id.app_upload:
                // 判断网络是否可用
                boolean networkAvailable = NetworkUtil.isNetworkAvailable(MainActivity.this);
                if (!networkAvailable) {
                    T.showToast("网络不可用!");
                    return;
                }
                if (checkFile()) {
                    T.showToast("没有要上传的文件");
                    return;
                }
                String db = (String) SPUtils.getInstance().get(APP_DB, "");
                if (db.length() == 0) {
                    T.showToast("站点配置为空！");
                    return;
                }
                showDialogMsg();
                break;
            // 删除最后一个码
            case R.id.app_delete:
                appPackingGoods = (String) SPUtils.getInstance().get(APP_PACKING_GOODS + "_" + goodsInfo.getId(),
                        "[]");
                JSONObject jsonObject = null;
                codeInfos = new ArrayList<>();
                String jsonStr = "";
                try {
                    jsonObject = new JSONObject(appPackingGoods);
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length() - 1; i++) {
                        codeInfos.add(dataArray.getString(i));
                    }
                    jsonStr = GsonUtil.getGson().toJson(codeInfos);
                    SPUtils.getInstance().put(APP_PACKING_GOODS + "_" + goodsInfo.getId(), "{\"data\":" + jsonStr
                            + "}");
                    // 跳转
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
