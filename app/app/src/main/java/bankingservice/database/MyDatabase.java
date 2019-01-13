package bankingservice.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyDatabase extends SQLiteOpenHelper {
    private static String DB_NAME = "bank_checker.db";
    private static String DB_PATH;
    private static String TAG = "MyDatabaseHelper";

    static {
        DB_PATH = "";
    }

    private final Context mContext;
    private SQLiteDatabase mDataBase;

    public MyDatabase(Context paramContext) {
        super(paramContext, DB_NAME, null, 1);
        DB_PATH = "/data/data/" + paramContext.getPackageName() + "/databases/";
        this.mContext = paramContext;
    }

    private boolean checkDataBase() {
        return new File(DB_PATH + DB_NAME).exists();
    }

    private void copyDataBase()
            throws IOException {
        InputStream localInputStream = this.mContext.getAssets().open(DB_NAME);
        FileOutputStream localFileOutputStream = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] arrayOfByte = new byte['?'];
        for (; ; ) {
            int i = localInputStream.read(arrayOfByte);
            if (i <= 0) {
                break;
            }
            localFileOutputStream.write(arrayOfByte, 0, i);
        }
        localFileOutputStream.flush();
        localFileOutputStream.close();
        localInputStream.close();
    }

    public void close() {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.close();
            }
            super.close();
            return;
        } finally {
        }
    }

    public void createDataBase()
            throws IOException {
        if (!checkDataBase()) {
            getReadableDatabase();
            close();
        }
        try {
            copyDataBase();
            Log.e(TAG, "createDatabase database created");
            return;
        } catch (IOException localIOException) {
            throw new Error("ErrorCopyingDataBase");
        }
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
    }

    public boolean openDataBase()
            throws SQLException {
        this.mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, 268435456);
        return this.mDataBase != null;
    }
}
