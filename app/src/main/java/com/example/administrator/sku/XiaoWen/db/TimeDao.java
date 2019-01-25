package com.example.administrator.sku.XiaoWen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\12\6 0006.
 */

public class TimeDao {
    private TimeOpenHelper helper;
    private SQLiteDatabase db;

    public TimeDao(Context context) {
        helper = new TimeOpenHelper(context);
    }

    //写增删改查的方法
    public void init() {
        //打开数据库
        db = helper.getReadableDatabase();
    }

    //添加的方法
    public boolean insert(String time) {
        boolean isExist = isNewsExist(time);
        if (isExist) {
            db.close();
            return false; //返回添加失败
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("time", time);
            db.insert("timeBiao", null, contentValues);
            db.close();
            return true;//返回添加成功
        }
    }


    //删除的方法
    public void delete() {
        init();
        //根据newsURL进行数据删除
        db.delete("timeBiao", null, null);
        db.close();
    }

    //查询的方法
    public List<String> select() {
        init();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("timeBiao", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String time = cursor.getString(cursor.getColumnIndex("time"));
            list.add(time);
        }
        return list;
    }

    //判断是否存在
    public boolean isNewsExist(String time) {
        init();
        Cursor cursor = db.query("timeBiao", null, "time = ?", new String[]{time}, null, null, null);
//        Log.i("Tag",newsInfo.getUrl());
        if (cursor.moveToFirst()) {
            return true; // 已经存在该数据
        } else {
            return false;//不存在
        }
    }
}
