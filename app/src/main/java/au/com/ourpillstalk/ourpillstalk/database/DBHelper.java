package au.com.ourpillstalk.ourpillstalk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Pills_Database";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Scanned_Pills";
    public static final String COL1 = "S_No";
    public static final String COL2 = "XML";
    public static final String COL3 = "Name";
    public static final String COL4 = "Date_and_Time";
    public static final String COL5 = "PharmacyName";
    public static final String COL6 = "DispensedDate";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("Create table if not exists " + TABLE_NAME + "("
                + COL1 + " integer primary key autoincrement, "
                + COL2 + " text,"
                + COL3 + " text,"
                + COL4 + " text,"
                + COL5 + " text,"
                + COL6 + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }


//  *******************************************************************************************   //
    //  for data insertion  //

    public boolean insertDataToScannedPills(String xml, String Name, String CurrentDate, String PharmacyName, String DispensedDate)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put(COL2, xml);
        con.put(COL3, Name);
        con.put(COL4, CurrentDate);
        con.put(COL5, PharmacyName);
        con.put(COL6, DispensedDate);
        long result = db.insert(TABLE_NAME, null, con);
        if (result == -1)
            return false;
        else
            return true;
    }

    //  for data retrieve  //

    public Cursor getDataFromScannedPills(String s_no)
    {
        String WHERE = COL1 + "=?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE "+WHERE, new String[]{s_no});
        return res;
    }

    public Cursor getDataFromScannedPills()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME, null);
        return res;
    }

    //  for data delete  //

    public boolean deleteDataFromScannedPills(String sno)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABLE_NAME, "S_No = ?", new String[] {sno});
        if (res > 0)
            return true;
        else
            return false;
    }

    public void deleteAllDataFromScannedPills()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
}