package com.makeinindia.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class DBAdapters {
    Context context;
    MyDatabase dataBase;
    SQLiteDatabase db;

    public DBAdapters(Context paramContext) {
        this.context = paramContext;
        this.dataBase = new MyDatabase(paramContext);
    }

    public void close() {
        this.dataBase.close();
    }

        public DBAdapters createDatabase() {
        try {
            this.dataBase.createDataBase();
            return this;
        } catch (IOException localIOException) {
            Log.e("error", localIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
    }

    public Cursor get_details(String paramString1, String paramString2) throws Exception {
        try {
            paramString1 = "select  *  from call where number=" + paramString1 + " and countrycode=" + paramString2;
            Cursor paramString = this.db.rawQuery(paramString1, null);
            if (paramString != null) {
                paramString.moveToNext();
            }
            return paramString;
        } catch (Exception paramString) {
            Log.e("ccc", "getTestData >>" + paramString.toString());
            throw paramString;
        }
    }

    public String get_details_main(String paramString) throws Exception {
        try {
            paramString = "select  *  from call where number=" + paramString;
            Cursor paramString1 = this.db.rawQuery(paramString, null);
            if (paramString1 != null) {
                paramString1.moveToNext();
            }
            return paramString;
        } catch (Exception paramString1) {
            Log.e("ccc", "getTestData >>" + paramString1.toString());
            throw paramString1;
        }
    }

    public Cursor get_messages(String paramString1) throws Exception {
        try {
            paramString1 = "select  *  from Messages where Cat_ID=" + paramString1 + "";
            Cursor paramString = this.db.rawQuery(paramString1, null);
            if (paramString != null) {
                paramString.moveToNext();
            }
            return paramString;
        } catch (Exception paramString) {
            Log.e("ccc", "getTestData >>" + paramString.toString());
            throw paramString;
        }
    }

    public DBAdapters open()
            throws Exception {
        try {
            this.dataBase.openDataBase();
            this.dataBase.close();
            this.db = this.dataBase.getReadableDatabase();
            return this;
        } catch (Exception localSQLException) {
            Log.e("error", "open >>" + localSQLException.toString());
            throw localSQLException;
        }
    }
}
