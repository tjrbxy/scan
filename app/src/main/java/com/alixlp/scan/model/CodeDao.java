package com.example.scan.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.scan.CodeInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-09-28.
 */

public class CodeDao {
    private SQLiteDatabase db;

    public CodeDao(Context context) {
        String path = Environment.getExternalStorageDirectory() + "/code.db";
        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, path, null, 2) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                String sql = "create table daili_code (_id integer primary key autoincrement," +
                        "goods_id integer, " +
                        "box_num varhcar(6)," +
                        "code_num varhcar(20) )";
                sqLiteDatabase.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            }
        };
        db = helper.getReadableDatabase();
    }

    public void addCode(CodeInfo codeInfo) {
        String sql = "insert into daili_code(goods_id,box_num,code_num)values(?,?,?)";
        db.execSQL(sql, new String[]{codeInfo.getGoodsId() + "", codeInfo.getBoxNum(), codeInfo.getCodeNum()});
    }

    public Cursor getCode(String... strs) {
        //1.查询所有(没有参数)
        String sql = "select * from daili_code ";
        //2.含条件查询（姓名/年龄/编号）（参数形式：第一个参数指明条件，第二个参数指明条件值）
        if (strs.length != 0) {
            sql += " where " + strs[0] + "='" + strs[1] + "'";
        }
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public ArrayList<CodeInfo> getCodeList(String... strs) {
        ArrayList<CodeInfo> list = new ArrayList<>();
        Cursor c = getCode(strs);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            int goodsId = c.getInt(1);
            String boxNum = c.getString(2);
            String codeNum = c.getString(3);
            CodeInfo s = new CodeInfo(id, goodsId, boxNum, codeNum);
            list.add(s);
        }
        return list;
    }

    public Cursor getCode(String query) {
        String sql = "select * from daili_code ";
        //2.含条件查询（姓名/年龄/编号）（参数形式：第一个参数指明条件，第二个参数指明条件值）
        if (query.length() != 0) {
            sql += " where  box_num like '%" + query + "' OR code_num like '%" + query + "'";
        }
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public ArrayList<CodeInfo> getCodeList(String query) {
        ArrayList<CodeInfo> list = new ArrayList<>();
        Cursor c = getCode(query);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            int goodsId = c.getInt(1);
            String boxNum = c.getString(2);
            String codeNum = c.getString(3);
            CodeInfo s = new CodeInfo(id, goodsId, boxNum, codeNum);
            list.add(s);
        }
        return list;
    }

}
