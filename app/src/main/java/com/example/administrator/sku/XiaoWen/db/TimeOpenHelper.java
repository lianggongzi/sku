package com.example.administrator.sku.XiaoWen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018\12\6 0006.
 */

public class TimeOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_NEWS = "CREATE TABLE timeBiao (time text)";
    public static final String DB_NAME = "time.db";

    public TimeOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表（newsInfo 中有什么字段，数据库中就要有什么字段）
        db.execSQL(CREATE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
