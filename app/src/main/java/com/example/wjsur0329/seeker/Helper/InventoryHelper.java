package com.example.wjsur0329.seeker.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wjsur0329.seeker.data.DataBean;
import com.example.wjsur0329.seeker.data.InventoryBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wjsur0329 on 2017-12-17.
 */

public class InventoryHelper extends SQLiteOpenHelper {
    public InventoryHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int verison){
        super(context, name, factory, verison);

    }




    @Override
    //사용자가 지정한 이름의 db가 없을 때 호출됨. create table을 여기서 수행하면 됨.
    public void onCreate(SQLiteDatabase db){
        String sql = "create table inventory(itemId integer primary key, itemfndtime integer, itemName text, itemDesc text, imageUrl text, lat real, lng real)";
        db.execSQL(sql);
    }

    @Override
    //사용자가 지정한 이름의 db는 있는데 버전이 올라갔을 때 호출됨.
    //테이블의 디자인을 수정하던지, 테이블을 drop하고 새로 만들지 등을 결정해서 코드를 작성함.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sql = "drop table inventory if exist history";
        db.execSQL(sql);
        onCreate(db);
    }

    public long insert(DataBean bean){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        // 모든 값 넣고 현재 시각 추가
        values.put("itemId", bean.getItemId());
        values.put("itemName", bean.getItemName());
        values.put("itemDesc", bean.getItemDesc());
        Log.d("Stringoffff",bean.getImageUrl());
        bean.setImageUrl(bean.getImageUrl().substring(bean.getImageUrl().indexOf(":8080")+5));
        Log.d("String",bean.getImageUrl());
        values.put("imageUrl", bean.getImageUrl());
        values.put("Lat", bean.getLat());
        values.put("lng", bean.getLng());

        Date date = new Date();
        values.put("itemfndtime", date.getTime());
        return db.insert("inventory", null, values);
    }

    public long delete(InventoryBean bean){
        SQLiteDatabase db = getWritableDatabase();
        String id = String.valueOf(bean.getItemId());
        return db.delete("inventory", "itemId=?", new String[] {id});
    }

    public ArrayList<InventoryBean> getAll(){
        ArrayList<InventoryBean> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("inventory", null, null, null, null, null, null);

        while(cursor.moveToNext()){
            InventoryBean bean = new InventoryBean();
            bean.setItemId(cursor.getInt(cursor.getColumnIndex("itemId")));
            bean.setItemName(cursor.getString(cursor.getColumnIndex("itemName")));
            bean.setItemDesc(cursor.getString(cursor.getColumnIndex("itemDesc")));
            bean.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
            bean.setItemfndtime(cursor.getLong(cursor.getColumnIndex("itemfndtime")));
            bean.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            bean.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            result.add(bean);
        }
        return result;
    }

}
