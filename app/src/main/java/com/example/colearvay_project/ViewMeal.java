package com.example.colearvay_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewMeal extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private MealDBHelper db;
    private Cursor cursor;

    private Spinner spinnerFood;
    private ImageView imgFood;
    private EditText edtFoodName;
    private EditText edtRestName;
    private EditText edtAddress;
    private ImageButton imgbtnEditStar1;
    private ImageButton imgbtnEditStar2;
    private ImageButton imgbtnEditStar3;
    private ImageButton imgbtnEditStar4;
    private ImageButton imgbtnEditStar5;
    private EditText edtMealDate;
    private EditText edtMealPrice;
    private Button btnBack;

    private int starRating;
    private long currMealID = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Unpack extra information passed in upon activity start. Find the clicked meal ID
        Intent i = getIntent();
        Bundle b = i.getExtras();
        currMealID = b.getLong("mealID");

        //Associate varibales to everything from the view
        spinnerFood = (Spinner) findViewById(R.id.spinnerFood);
        imgFood = findViewById(R.id.imgFood);
        edtFoodName = findViewById(R.id.edtFoodName);
        edtRestName = findViewById(R.id.edtRestName);
        edtAddress = findViewById(R.id.edtAddress);
        imgbtnEditStar1 = findViewById(R.id.imgbtnEditStar1);
        imgbtnEditStar2 = findViewById(R.id.imgbtnEditStar2);
        imgbtnEditStar3 = findViewById(R.id.imgbtnEditStar3);
        imgbtnEditStar4 = findViewById(R.id.imgbtnEditStar4);
        imgbtnEditStar5 = findViewById(R.id.imgbtnEditStar5);
        edtMealDate = findViewById(R.id.edtMealDate);
        edtMealPrice = findViewById(R.id.edtMealPrice);
        btnBack = findViewById(R.id.btnBack);

        //Open the database to set the spinner
        db = new MealDBHelper(this);
        db.open();
        spinnerFood.setOnItemSelectedListener(this);
        refreshData();
        startManagingCursor(cursor);

        //Using the ID find the meal from the database. Add data elements to the page
        db.open();
        cursor = db.getMeal(currMealID);
        if (cursor.moveToFirst())
        {
            this.currMealID = cursor.getLong(0);
            edtFoodName.setText(cursor.getString(1));
            edtRestName.setText(cursor.getString(2));
            edtAddress.setText(cursor.getString(3));
            starRating = cursor.getInt(4);
            edtMealDate.setText(cursor.getString(6));
            edtMealPrice.setText(cursor.getString(5));

            //Depending on the rating light up that amount of stars
            switch (starRating)
            {
                case 1:
                    imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_off);
                    imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_off);
                    imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
                    imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                    break;
                case 2:
                    imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_off);
                    imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
                    imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                    break;
                case 3:
                    imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
                    imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                    break;
                case 4:
                    imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                    break;
                case 5:
                    imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_on);
                    imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_on);
                    break;
                default:
                    break;
            }
        }

        //return to previous activity when Return to Main Button is clicked
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED);
            finish();
        });

    }

    /**
     * Set the spinner to the appropriate text every time it is moved to a new entry
     */
    private void refreshData()
    {
        db.open();
        cursor = db.getAllMeals();
        String[] cols = new String[] {db.DATE};
        int[] views = new int[] {android.R.id.text1};
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_list_item_1, cursor, cols, views, 0);
        spinnerFood.setAdapter(adapter);
        db.close();
    }

    /**
     * When the Spinner selects a new item fill in the appropriate information for that item
     * @param arg0 The AdapterView where the selection happened
     * @param arg1 The view within the AdapterView that was clicked
     * @param arg2 The position of the view in the adapter
     * @param arg3 The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        currMealID = cursor.getLong(0);
        edtFoodName.setText(cursor.getString(1));
        edtRestName.setText(cursor.getString(2));
        edtAddress.setText(cursor.getString(3));
        edtMealDate.setText(cursor.getString(6));
        edtMealPrice.setText(cursor.getString(5));

        switch (cursor.getInt(4))
        {
            case 1:
                imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_off);
                imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_off);
                imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
                imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 2:
                imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_off);
                imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
                imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 3:
                imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
                imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 4:
                imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
                break;
            case 5:
                imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_on);
                imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_on);
                break;
            default:
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
    }


    /**
     * Function for when save changes button is clicked. Update the current meal in the
     * database with the information from the text fields. Then return to main screen
     * @param actionView
     */
    public void saveClicked(View actionView)
    {
        updateMeal();

        Intent intent = new Intent();
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Function for updating a Meal in the database. Create a meal with the current ID,
     * pass it to the database to overwrite the old data
     */
    private void updateMeal()
    {
        db.open();
        Meal newMeal = new Meal(
                currMealID,
                edtFoodName.getText().toString(),
                edtRestName.getText().toString(),
                edtAddress.getText().toString(),
                starRating,
                Double.parseDouble(edtMealPrice.getText().toString()),
                edtMealDate.getText().toString());
        db.updateMeal(newMeal);
        db.close();
        refreshData();
    }

    /**
     * Function for when the delete button is clicked. Remove that item from the
     * database and return to the main screen
     * @param actionView
     */
    public void deleteClicked(View actionView)
    {
            deleteMeal();
            Intent intent = new Intent();
            setResult(RESULT_OK);
            finish();
    }

    /**
     * Function that deletes the item from the database. Using the current ID
     * find the associated item in the database and delete it
     */
    private void deleteMeal()
    {
        db.open();
        Meal newMeal = new Meal(
                currMealID,
                edtFoodName.getText().toString(),
                edtRestName.getText().toString(),
                edtAddress.getText().toString(),
                starRating,
                Double.parseDouble(edtMealPrice.getText().toString()),
                edtMealDate.getText().toString());
        db.deleteMeal(newMeal);
        db.close();
        refreshData();
    }


    /**
     * Function for the one star image button. Change the star rating variable to one
     * and set the star images accordingly
     * @param v
     */
    public void oneStar(View v)
    {
        starRating = 1;

        imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the two star image button. Change the star rating variable to two
     * and set the star images accordingly
     * @param v
     */
    public void twoStar(View v)
    {
        starRating = 2;

        imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the three star image button. Change the star rating variable to three
     * and set the star images accordingly
     * @param v
     */
    public void threeStar(View v)
    {
        starRating = 3;

        imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the four star image button. Change the star rating variable to four
     * and set the star images accordingly
     * @param v
     */
    public void fourStar(View v)
    {
        starRating = 4;

        imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the five star image button. Change the star rating variable to five
     * and set the star images accordingly
     * @param v
     */
    public void fiveStar(View v)
    {
        starRating = 5;

        imgbtnEditStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar3.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar4.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnEditStar5.setImageResource(android.R.drawable.btn_star_big_on);
    }


}