package com.example.leader_list_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DB {
    public SQLiteDatabase db = null;
    private final static String DATABASE_NAME = "db_mid.db";   //資料庫
    private final static String TABLE_NAME = "table_mid";     //資料表
    private final static String _ID = "_id";                //資料表欄位
    private final static String NAME = "name";
    private final static String Sentence = "say";

    //建立資料表欄位
    private final static String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +  " (" + _ID + " INTEGER PRIMARY KEY," +
                    NAME + " TEXT," + Sentence + " TEXT)";

    private Context mCtx = null; //建構式
    public DB(Context ctx){
        this.mCtx = ctx;
    }

    public void open() throws SQLException { //開啟資料庫
        db = mCtx.openOrCreateDatabase(DATABASE_NAME,0,null);
        try{
            db.execSQL(CREATE_TABLE);
        }catch (Exception e) {
        }
    }

    public void close(){
        db.close();
    }

    //  public Cursor getAll() {  //查詢所有資料，取出所有的欄位
    //      return db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
    //  }

    public Cursor getAll() {  //查詢所有資料，只取出三個欄位
        return db.query(TABLE_NAME, new String[] {_ID,NAME,Sentence},
                null,null,null,null,null,null);

    }

    public Cursor get(long rowId) throws SQLException{ //查指定ID，只取三個
        Cursor mCursor = db.query(TABLE_NAME,
                new String[] {_ID,NAME,Sentence},
                _ID + "=" + rowId,null,null,null,null,null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long append(String name, String say){ //新增一筆資料
        ContentValues args = new ContentValues();
        args.put(NAME,name);
        args.put(Sentence,say);
        return db.insert(TABLE_NAME,null,args); //在空的格子填入?
    }

    public boolean delete(long rowId){
        return db.delete(TABLE_NAME,_ID + "=" + rowId ,null) > 0;
    }

    public boolean update(long rowId, String name, String say){ //更新指定資料
        ContentValues args = new ContentValues();
        args.put(NAME,name);
        args.put(Sentence,say);
        return db.update(TABLE_NAME,args,_ID + "=" + rowId , null) > 0;
    }
}
