package com.orm;

public class SugarApp extends android.app.Application {

    Database database;

    @Override
    public void onCreate() {

        // to make sure async task is initialized on ui thread
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            // ignore
        }

        super.onCreate();
        this.database = new Database(this, "sugar-db");
    }

    @Override
    public void onTerminate() {

        if (this.database != null) {
            this.database.closeDB();
        }
        super.onTerminate();
    }

}
