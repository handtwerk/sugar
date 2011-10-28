package com.orm;


public class SugarApp extends android.app.Application {

    Database database;

    @Override
    public void onCreate() {

        super.onCreate();
        this.database = new Database(this);
    }

    @Override
    public void onTerminate() {

        if (this.database != null) {
            this.database.closeDB();
        }
        super.onTerminate();
    }

}
