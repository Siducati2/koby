package com.innohawk.dan.sqlite;

/**
 * Created by innohawk on 16/3/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.innohawk.dan.appconfig.AppConfig;

import java.util.HashMap;

import static com.innohawk.dan.appconfig.AppConfig.TABLE_USER;

public class SQLiteActivity extends SQLiteOpenHelper {

    private static final String TAG = "OO SQLiteBBDD";

    public SQLiteActivity(Context context) {
        super(context, AppConfig.DATABASE_NAME, null, AppConfig.DATABASE_VERSION);
    }

    //http://www.sgoliver.net/blog/bases-de-datos-en-android-ii-insertaractualizareliminar/

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + AppConfig.TABLE_USER + "("
                + AppConfig.KEY_ID + " INTEGER PRIMARY KEY,"
                + AppConfig.KEY_USERNAME + " TEXT,"
                + AppConfig.KEY_PWR + " TEXT,"
                + AppConfig.KEY_STATUS + " TEXT,"
                + AppConfig.KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_GCM_TABLE = "CREATE TABLE " + AppConfig.TABLE_GCM + "("
                + AppConfig.KEY2_ID + " INTEGER PRIMARY KEY,"
                + AppConfig.KEY2_TOKEN + " TEXT,"
                + AppConfig.KEY2_EMAI + " TEXT,"
                + AppConfig.KEY2_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_GCM_TABLE);

        Log.d(TAG, "Tablas Creadas");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + AppConfig.TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     *
     * Update en la BBDD
     *
     */

    public void updateGCM(String token, Integer id, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConfig.KEY2_TOKEN, token);
        values.put(AppConfig.KEY2_CREATED_AT, created_at);
        Log.d(TAG, "el ID es: " + id);

        long id_ = db.update(AppConfig.TABLE_GCM, values, AppConfig.KEY2_ID + "='" + id + "'", null);
        db.close(); // Closing database connection
        Log.d(TAG, "Y despues de actualizar el ID es: " + id_);
    }

    /**
     * Grabar en la BBDD
     */

    public void addToken(String token, String emai, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppConfig.KEY2_TOKEN, token);
        values.put(AppConfig.KEY2_EMAI, emai);
        values.put(AppConfig.KEY2_CREATED_AT, created_at);

        // Inserting Row
        long id = db.insert(AppConfig.TABLE_GCM, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "Nuevos datos insertados en la BBDD GCM: " + id);
    }


    public void addUser(String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppConfig.KEY_USERNAME, username);
        values.put(AppConfig.KEY_PWR, email);

        // Inserting Row
        db.insert(AppConfig.TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "Usuario registrado: " + username);
    }

    /**
     * Obtenemos valores de la BBDD
     */
    public HashMap<String, String> getExistUsers() { //Tabla Usuario
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + AppConfig.TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("username", cursor.getString(1));
            user.put("pwr", cursor.getString(2));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public HashMap<String, String> getGCMvalues(String GCM_ID, String IMEI) { //Tabla GCM
        HashMap<String, String> gcm = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + AppConfig.TABLE_GCM +" WHERE token='"+GCM_ID+"' AND emai='"+IMEI+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery(selectQuery, null);
        // Move to first row
        row.moveToFirst();
        if (row.getCount() > 0) {
            gcm.put("id", row.getString(0));
            gcm.put("token", row.getString(1));
            gcm.put("emai", row.getString(2));
        }else{
            gcm.put("id", String.valueOf(0));
        }
        row.close();
        db.close();
        // return gcm
        Log.d(TAG, "Fetching GCM Values from Sqlite: " + gcm.toString());
        return gcm;
    }

    public HashMap<String, String> getGCMInitial() { //Tabla GCM Inicial, para ver si debemos meternos o no...
        HashMap<String, String> gcm = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + AppConfig.TABLE_GCM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor row = db.rawQuery(selectQuery, null);
        // Move to first row
        row.moveToFirst();
        if (row.getCount() > 0) {
            gcm.put("token", row.getString(1));
            gcm.put("emai", row.getString(2));
        }
        row.close();
        db.close();
        // return gcm
        Log.d(TAG, "Fetching GCM Values from Sqlite: " + gcm.toString());
        return gcm;
    }



    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Eliminacion de todos los datos de la BBDD");
    }

}

