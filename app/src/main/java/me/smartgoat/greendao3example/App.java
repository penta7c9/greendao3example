package me.smartgoat.greendao3example;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import me.smartgoat.greendao3example.entity.DaoMaster;
import me.smartgoat.greendao3example.entity.DaoMaster.DevOpenHelper;
import me.smartgoat.greendao3example.entity.DaoSession;

public class App extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DevOpenHelper helper = new DevOpenHelper(this, ENCRYPTED ? "sample-db-encrypted" : "sample-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
