package com.example.gamemg.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.gamemg.RecordBean;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "record.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table record(" +
                "step integer ," +
                "time text(15))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveR(RecordBean bean) {
        getWritableDatabase().execSQL("insert into record values(?,?)",new Object[]{bean.getStep(),bean.getTime()});
        close();
    }

    public List<RecordBean> getRecord() {
        List<RecordBean> list = new ArrayList<>();
        Cursor q = getWritableDatabase().query("record", null, null, null, null, null, "step asc");
        while (q.moveToNext()) {
            int anInt = q.getInt(0);
            String string = q.getString(1);
            RecordBean bean = new RecordBean(anInt, string);
            list.add(bean);
        }
        q.close();
        close();
        return list;

    }
}
