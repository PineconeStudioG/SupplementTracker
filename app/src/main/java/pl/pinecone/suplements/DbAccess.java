package pl.pinecone.suplements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DbAccess extends SQLiteOpenHelper {

    private final String TABLE_NAME = "supplements";
    private final String SUPPLEMENTS_NAME_COLUMN = "Supplement_Name";
    private final String SUPPLEMENTS_DOSE_COLUMN = "Supplement_dose";
    private final String SUPPLEMENTS_LASTATKEN_COLUMN = "Supplement_lasttaken";
    private final String SUPPLEMENTS_FREQUENCY_COLUMN = "Supplement_frequency";

    public DbAccess(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE supplements(ID INTEGER PRIMARY KEY AUTOINCREMENT, Supplement_Name TEXT, Supplement_dose TEXT, Supplement_lasttaken TEXT, Supplement_frequency INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addRecord(String supplement_name, String supplement_dose, String supplement_lasttaken, int supplement_frequency) {
        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor checkingCursor = db.rawQuery("SELECT " + this.SUPPLEMENTS_NAME_COLUMN + " FROM " + this.TABLE_NAME + " WHERE " + this.SUPPLEMENTS_NAME_COLUMN + " = '" + supplement_name + "'", null);
        if(checkingCursor.moveToFirst())
            return false;
        checkingCursor.close();

        ContentValues newData = new ContentValues();
        newData.put(this.SUPPLEMENTS_NAME_COLUMN,supplement_name);
        newData.put(this.SUPPLEMENTS_DOSE_COLUMN,supplement_dose);
        newData.put(this.SUPPLEMENTS_LASTATKEN_COLUMN, supplement_lasttaken);
        newData.put(this.SUPPLEMENTS_FREQUENCY_COLUMN,supplement_frequency);
        result = db.insert(this.TABLE_NAME,null,newData);
        db.close();
        return result != (-1);
    }

    public List<String[]> getSupplements() {
        String getSupplementsQuery = "SELECT " + this.SUPPLEMENTS_NAME_COLUMN + ", " + this.SUPPLEMENTS_DOSE_COLUMN + ", " + this.SUPPLEMENTS_LASTATKEN_COLUMN + ", " + this.SUPPLEMENTS_FREQUENCY_COLUMN + " FROM " + this.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getSupplementsQuery, null);
        List<String[]> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String supplementName = cursor.getString(0);
                String supplementDose = cursor.getString(1);
                String supplementLastTaken = cursor.getString(2);
                String supplementFrequency = Integer.toString(cursor.getInt(3));
                String[] supplementData = {supplementName, supplementDose, supplementLastTaken, supplementFrequency};
                data.add(supplementData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    public void updateRecordDate(String supplement_name)
    {
        Calendar currentCalendar = Calendar.getInstance();
        String dayNewLastTaken;
        String monthNewLastTaken;
        int yearNewLastTaken = currentCalendar.get(Calendar.YEAR);

        if(currentCalendar.get(Calendar.DAY_OF_MONTH) < 10)
            dayNewLastTaken = "0" + String.valueOf(currentCalendar.get(Calendar.DAY_OF_MONTH));
        else dayNewLastTaken = String.valueOf(currentCalendar.get(Calendar.DAY_OF_MONTH));

        if(currentCalendar.get(Calendar.MONTH)+1 < 10)
            monthNewLastTaken = "0" + String.valueOf(currentCalendar.get(Calendar.MONTH)+1);
        else monthNewLastTaken = String.valueOf(currentCalendar.get(Calendar.MONTH)+1);

        String newLastTaken = dayNewLastTaken+"/"+monthNewLastTaken+"/"+yearNewLastTaken;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newData = new ContentValues();
        newData.put(this.SUPPLEMENTS_LASTATKEN_COLUMN,newLastTaken);
        String selectionArgs = this.SUPPLEMENTS_NAME_COLUMN + " = ?";
        String []selectionArgsTab = {supplement_name};
        db.update(this.TABLE_NAME,newData,selectionArgs,selectionArgsTab);
        db.close();
    }

    public void updateRecord(String supplement_name, String supplement_dose, String supplement_lasttaken, int supplement_frequency)
    {
        ContentValues updatedContents = new ContentValues();
        updatedContents.put(this.SUPPLEMENTS_DOSE_COLUMN,supplement_dose);
        updatedContents.put(this.SUPPLEMENTS_LASTATKEN_COLUMN, supplement_lasttaken);
        updatedContents.put(this.SUPPLEMENTS_FREQUENCY_COLUMN,supplement_frequency);

        SQLiteDatabase db = this.getWritableDatabase();
        final String whereClause = this.SUPPLEMENTS_NAME_COLUMN + " =?";
        final String []selectArgs = {supplement_name};

        db.update(this.TABLE_NAME,updatedContents,whereClause,selectArgs);
        db.close();
    }

    public void deleteOneRecord(String supplement_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        final String[] delArgs = {supplement_name};
        db.delete(this.TABLE_NAME, this.SUPPLEMENTS_NAME_COLUMN + "= ?", delArgs);
        db.close();
    }
    public void deleteAllRecords()
    {
        final String deleteSupplementsQuery = "DELETE FROM " + this.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteSupplementsQuery);
        db.close();
    }
}