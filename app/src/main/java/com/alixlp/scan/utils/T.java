package com.alixlp.scan.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by zhy on 16/10/23.
 */
public class T {

    private static Toast toast;

    public static void showToast(
            String content) {
        toast.setText(content);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static void showToast(
            String content,int duration) {
        toast.setText(content);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.show();
    }

    public static void init(Context context) {
        toast = Toast.makeText(context,
                "",
                Toast.LENGTH_SHORT);
    }
}
