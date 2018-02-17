package com.manav.edfora;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by manav on 16/12/17.
 */

public class OlaPlayDB extends SQLiteOpenHelper {

    public static OlaPlayDB instance;

    private OlaPlayDB(Context context) {
        super(context, "olaplay.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      UserHistory.createTables(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Returns the instance of OlaPlayDB class
    public static OlaPlayDB getDBInstance() {
        if (instance == null) {
            instance = new OlaPlayDB(AppContext.getAppContext());
        }
        return instance;
    }

    //Creating the instance of Sqlite Database.
    public static SQLiteDatabase getDB() {
        return getDBInstance().getWritableDatabase();
    }
}
