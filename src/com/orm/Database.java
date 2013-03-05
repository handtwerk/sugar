package com.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

    private SugarDb        sugarDb;

    private SQLiteDatabase sqLiteDatabase;

    public Database(Context context, String pName) {
        this.sugarDb = new SugarDb(context, pName);
    }

    public SQLiteDatabase openDB() {

        Log.v("Sugar", "openDB");
        this.sqLiteDatabase = this.sugarDb.getWritableDatabase();

        return this.sqLiteDatabase;
    }

    public void closeDB() {
        if (this.sqLiteDatabase != null) {
            this.sqLiteDatabase.close();
            this.sqLiteDatabase = null;
        }
    }
}
