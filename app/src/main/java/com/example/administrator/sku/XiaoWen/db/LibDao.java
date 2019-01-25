package com.example.administrator.sku.XiaoWen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.sku.XiaoWen.bean.LibBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\12\6 0006.
 */

public class LibDao {
    private LibOpenHelper helper;
    private SQLiteDatabase db;

    public LibDao(Context context) {
        helper = new LibOpenHelper(context);
    }

    //写增删改查的方法
    public void init() {
        //打开数据库
        db = helper.getReadableDatabase();
    }

    //添加的方法
    public boolean insert(String time, String lib, String sku) {
//        boolean isExist = isNewsExist();
//        if (isExist) {
//            db.close();
//            return false; //返回添加失败
//        } else {
        init();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        contentValues.put("lib", lib);
        contentValues.put("sku", sku);
        db.insert("libBao", null, contentValues);
        db.close();
        return true;//返回添加成功
//        }
    }


    //删除的方法
    public void delete() {
        init();
        //根据newsURL进行数据删除
        db.delete("libBao", null, null);
        db.close();
    }

    //查询的方法
    public List<String> select(String time) {
        init();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("libBao", null, "time = ? ", new String[]{time}, null, null, null);
        while (cursor.moveToNext()) {
            String lib = cursor.getString(cursor.getColumnIndex("lib"));
            list.add(lib);
        }
        return list;
    }

    //查询的方法
    public List<String> selectzong(String times, String libs) {
        init();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("libBao", null, "time = ? and lib = ? ", new String[]{times, libs}, null, null, null);
        while (cursor.moveToNext()) {
            String sku = cursor.getString(cursor.getColumnIndex("sku"));
            list.add(sku);
        }
        return list;
    }

    //判断是否存在
    public boolean isNewsExist() {
        init();
        Cursor cursor = db.query("libBao", null, null, null, null, null, null);
//        Log.i("Tag",newsInfo.getUrl());
        if (cursor.moveToFirst()) {
            return true; // 已经存在该数据
        } else {
            return false;//不存在
        }
    }
}
