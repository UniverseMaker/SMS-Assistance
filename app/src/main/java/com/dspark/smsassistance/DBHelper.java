package com.dspark.smsassistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // String 보다 StringBuffer가 Query 만들기 편하다.
        //StringBuffer sb = new StringBuffer();
        //sb.append(" CREATE TABLE RECORD ( ID INTEGER PRIMARY KEY AUTOINCREMENT,  NAME TEXT,  TARGET TEXT,  WINNER TEXT,  DATE TEXT ) ");

        // SQLite Database로 쿼리 실행
        db.execSQL("CREATE TABLE REQUEST ( ID INTEGER PRIMARY KEY AUTOINCREMENT,  TITLE TEXT,  CONTENTS TEXT,  TIME TEXT )");
        //Toast.makeText(context, "Table 생성완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete("REQUEST", null, null);
    }

    public void querySQL(String sql)
    {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
    }

    public Map<Integer, ArrayList<String>> getRecord()
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ID, TITLE, CONTENTS, TIME FROM REQUEST ORDER BY ID DESC", null);
        Map<Integer, ArrayList<String>> data = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while( cursor.moveToNext() ) {
            data.put(cursor.getInt(0), new ArrayList<String>());
            data.get(cursor.getInt(0)).add(cursor.getString(1));
            data.get(cursor.getInt(0)).add(cursor.getString(2));
            data.get(cursor.getInt(0)).add(cursor.getString(3));
        }
        cursor.close();
        return data;
    }

    public void insertRecord(String title, String contents, String date)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO REQUEST (TITLE, CONTENTS, TIME )  VALUES ( '" + title  + "', '" + contents + "', '" + date + "' )");
    }

    public void removeRecord(String id)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM REQUEST WHERE ID=" + id);
    }

    public int countRecord(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount= db.rawQuery("SELECT count(*) FROM REQUEST", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        return count;
    }


}
