package com.kristian.travelerblog;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;


public class DatabaseHelper extends SQLiteOpenHelper {
    //definovanie verzie databázy
    public static final int database_version = 1;
    //vytvorenie požadovanej databázy
    private static final String CREATE_DATABASE = "CREATE TABLE " + DataTable.TableInfo.TABLE_NAME + " ("
            + DataTable.TableInfo.TABLE_ID + " INTEGER PRIMARY KEY, "
            + DataTable.TableInfo.TEXT + " TEXT, "
            + DataTable.TableInfo.TIMESTAMP + " TEXT, "
            + DataTable.TableInfo.LATITUDE + " REAL, "
            + DataTable.TableInfo.LONGITUDE + " REAL );";
    //zmazanie databázy
    private static final String DELETE_DATABASE =
            "DROP TABLE IF EXIST" + DataTable.TableInfo.TABLE_NAME;

    public DatabaseHelper(Context context){
        super(context, DataTable.TableInfo.DATABASE_NAME, null, database_version);
    }
    //vytvorenie databázy
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);

    }
    //obnovenie databázy
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_DATABASE);
        onCreate(db);
    }
    // metóda na vkladanie údajov do databázy
    public void insertData(DatabaseHelper dbh, Double Latitude, Double Longitude, String timeStamp, EditText Text){
        SQLiteDatabase SQL = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataTable.TableInfo.LATITUDE, Latitude);
        values.put(DataTable.TableInfo.LONGITUDE, Longitude);
        values.put(DataTable.TableInfo.TIMESTAMP, timeStamp);
        values.put(DataTable.TableInfo.TEXT, String.valueOf(Text));
        SQL.insert(DataTable.TableInfo.TABLE_NAME, "popis", values);

    }
    //Krok 7 -  Ukladanie popisu k fotografiám
    //update tabuĺky, kvôli pridaniu, modifikácií textu
    //vyhľadanie psrávneho miesta pre uloženie na základe IDčka
    public void updateTable(Long ID, String new_text, SQLiteDatabase sqLiteDatabase){
        ContentValues values = new ContentValues();
        values.put(DataTable.TableInfo.TEXT, new_text);
        String idFilter = "_id=" +ID;
        sqLiteDatabase.update(DataTable.TableInfo.TABLE_NAME, values, idFilter, null);
    }

}
