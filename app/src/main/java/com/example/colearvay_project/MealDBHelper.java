package com.example.colearvay_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MealDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "meal.db";
    private static final String TABLE_NAME = "Meals";
    private static final int DB_VERSION = 1;
    public static final String ID = "_id";
    public static final String MEAL = "meal";
    public static final String RESTAURANT = "restaurant";
    public static final String ADDRESS = "address";
    public static final String PRICE = "price";
    public static final String RATING = "rating";
    public static final String DATE = "date";
    public SQLiteDatabase sqlDB;


    public MealDBHelper(Context context)

    {//needs the context so it can find its location in the phone's file system. Without it, it wouldn't know where to look
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void open() throws SQLException
    {
        sqlDB = this.getWritableDatabase();
    }

    public void close()
    {
        sqlDB.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sCreate = "create table " +
                TABLE_NAME + " (" +
                ID + " integer primary key autoincrement, " +
                MEAL + " text not null, " +
                RESTAURANT + " text not null, " +
                ADDRESS + " text not null, " +
                RATING + " int not null, " +
                PRICE + " double not null, " +
                DATE + " text not null);";
        //Execute the create table sql statement
        db.execSQL(sCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Drop the existing table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Use the existing onCreate code to recreate the table
        onCreate(db);
    }



    //Create a new row in the table
    public long createMeal(Meal newMeal)
    {
        //A container storing each column value for a row
        ContentValues cvs = new ContentValues();
        //Add values for each column of the row
        cvs.put(MEAL, newMeal.mealName);
        cvs.put(RESTAURANT, newMeal.resName);
        cvs.put(ADDRESS, newMeal.resAddr);
        cvs.put(RATING, newMeal.rating);
        cvs.put(PRICE, newMeal.price);
        cvs.put(DATE, newMeal.date);

        //Execute the insert statement, which returns the autoincrement value
        long autoId = sqlDB.insert(TABLE_NAME, null, cvs);

        //Update the purchase with the assigned id value
        newMeal.id = autoId;
        return autoId;
    }



    //Update a row in the table
    public boolean updateMeal(Meal newMeal)
    {
        if(newMeal.id < 0)
        {
            return false;
        }
        else
        {
            //A container storing each column value for a row ContentValues
            ContentValues cvs = new ContentValues();
            //Add values for each column of the row
            cvs.put(MEAL, newMeal.mealName);
            cvs.put(RESTAURANT, newMeal.resName);
            cvs.put(ADDRESS, newMeal.resAddr);
            cvs.put(RATING, newMeal.rating);
            cvs.put(PRICE, newMeal.price);
            cvs.put(DATE, newMeal.date);
            return sqlDB.update(TABLE_NAME, cvs, ID + " = " + newMeal.id, null)> 0;
        }
    }



    //Delete a Meal in the table
    public boolean deleteMeal(Meal newMeal)
    {
        //Delete will return the number of rows affected. Compare this to 0 and return the value.
        return sqlDB.delete(TABLE_NAME, ID + "=" + newMeal.id, null) > 0;
    }

    //Delete all Meals in the table
    public boolean deleteAllMeals()
    {
        //Delete will return the number of rows affected. Compare this to 0 and return the value.
        return sqlDB.delete(TABLE_NAME, ID + ">=" + 0, null) > 0;
    }



    //Get all Meals in the table
    public Cursor getAllMeals()
    {
        String [] sFields = new String [] {ID, MEAL, RESTAURANT, ADDRESS, RATING, PRICE, DATE};
        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null );
    }



    //Get a single Meal from the table
    public Cursor getMeal(long id) throws SQLException
    {
        String [] sFields = new String [] {ID, MEAL, RESTAURANT, ADDRESS, RATING, PRICE, DATE};
        Cursor fpCursor = sqlDB.query(true, TABLE_NAME, sFields, ID + " = "
                + id, null, null, null, null, null);
        if(fpCursor != null)
        {
            fpCursor.moveToFirst();
        }
        return fpCursor;
    }

    //Get back every Meal in the database with a 5 star rating
    public Cursor getBestMeals()
    {
        String [] sFields = new String [] {ID, MEAL, RESTAURANT, ADDRESS, RATING, PRICE, DATE};
        Cursor fpCursor = sqlDB.query(true, TABLE_NAME, sFields, RATING + " = " + 5,
                            null, null, null, null, null);
        if(fpCursor != null)
        {
            fpCursor.moveToFirst();
        }
        return fpCursor;
    }

}
