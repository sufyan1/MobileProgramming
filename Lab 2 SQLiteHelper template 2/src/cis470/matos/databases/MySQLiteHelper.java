package cis470.matos.databases;

/**
 * Created by user on 16/11/2015.
 * bu muhammad Suffyian
 */

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "myDB1";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String myDbPath = mySdPath  + "/myDB1.db";

        db = SQLiteDatabase.openDatabase(myDbPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);

//        db.beginTransaction();
//        try {
            // create table
            db.execSQL("create table tableDB ("
                    + " ID integer PRIMARY KEY autoincrement, "
                    + " firstName  text, " + " lastName text , " + " Age text );  ");
            // commit your changes
//            db.setTransactionSuccessful();
//
//        } catch (SQLException e1) {
//
//        } finally {
//            db.endTransaction();
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS tableDB");

        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /********************************************************************************/
    public void helperAddMethod(String fn,String ln,String ad,String sl ) {
        SQLiteDatabase db;

        String mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String myDbPath = mySdPath  + "/myDB1.db";

        db = SQLiteDatabase.openDatabase(myDbPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);

        db.beginTransaction();
        try {
            db.execSQL("insert into tableDB(firstName, lastName,address,salary) "
                    + " values ('"+ fn + "', '"+ln+"','"+ad+"','"+sl+"' );");
            // commit your changes
            db.setTransactionSuccessful();

        } catch (SQLiteException e2) {

        } finally {
            db.endTransaction();
        }
    }// insertSomeData

    public void helperUseDeleteMethod(String whereArgs) {
        SQLiteDatabase db;

        String mySdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String myDbPath = mySdPath  + "/myDB1.db";

        db = SQLiteDatabase.openDatabase(myDbPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);

        try {
            String[] whereArgsArray = { whereArgs };

            int recAffected = db.delete("tableDB", "firstName = ?",
                    whereArgsArray);
        } catch (Exception e) {

        }
    }


}


