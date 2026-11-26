package com.example.colearvay_project;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    RecyclerView itemList;
    TextView txtBestMeal;
    TextView txtBestRest;
    TextView txtBestRating;
    MealListAdapter adapter;
    ArrayList<Meal> meals;
    Random obRand;

    Meal currMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //pull all items from the database into an arraylist
        fillMealsArray();

        //create variables for all items on the page
        itemList = (RecyclerView) findViewById(R.id.itemView);
        txtBestMeal = findViewById(R.id.txtBestMeal);
        txtBestRest = findViewById(R.id.txtBestRest);
        txtBestRating = findViewById(R.id.txtBestRating);

        //Add a recycler view to the page with each item from the database
        // display them in reverse order (newest entries first) and make them clickable
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        itemList.setLayoutManager(manager);
        adapter = new MealListAdapter(this, meals);
        itemList.setAdapter(adapter);
        adapter.setClickListener(new MealListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                currMeal = meals.get(position);

                //When an item in the recycler view is clicked open a new activity to view all the info about that item
                Intent intent = new Intent(getApplicationContext(), ViewMeal.class);
                intent.putExtra("mealID", currMeal.id);
                startActivityForResult(intent, 1);
            }
        });


        //Add dividing lines between recycler view items
        DividerItemDecoration dividerItemDecoration =
        new DividerItemDecoration(itemList.getContext(), manager.getOrientation());
        itemList.addItemDecoration(dividerItemDecoration);

        //fill the favourite meal section of the page with an item from the database
        addBestMeal();

/////////////////////////////////////////////////////Set Up Only/////////////////////////////////////////////////////////////

        //These are only here if you want to start off with some entries in the database
        //Uncomment the lines to add 10 items, uncomment the delete line if you want to empty out the database
        //After running the app once with these uncommented, comment them back out
//        MealDBHelper db = new MealDBHelper(this);
//        db.open();
//        db.deleteAllMeals(); //Only uncomment if you want to empty the database before adding these items
//        db.createMeal(new Meal("Triple Dipper", "Chili's", "103 Main St - Saskatoon", 4, 14.50, "December 15, 2025"));
//        db.createMeal(new Meal("Southwest Burger", "Montana's", "106 South Ave - Saskatoon", 3, 19.95, "February 7, 2026"));
//        db.createMeal(new Meal("Chicken Sandwich", "Presto", "22 Fork St - Lloydminster", 2, 12.95, "February 23, 2026"));
//        db.createMeal(new Meal("Cajun Pasta", "Spagonolli's", "1122 Spin Rd - Edmonton", 5, 31.50, "March 11, 2026"));
//        db.createMeal(new Meal("Salmon Oshi", "Maki Maki", "6 Alphabet St - Saskatoon", 5, 28.10, "April 14, 2026"));
//        db.createMeal(new Meal("McDouble Meal", "McDonald's", "1317 Preston Ave - Saskatoon", 1, 16.73, "May 3, 2026"));
//        db.createMeal(new Meal("Beef Curry", "The Restaurant Lounge", "4314 Hillhurst Ave - Los Feliz", 5, 22.91, "May 29, 2026"));
//        db.createMeal(new Meal("Fish and Chips", "Seabass Paul's", "11 Milwaukee St - Regina", 4, 17.61, "June 1, 2026"));
//        db.createMeal(new Meal("Full Rack of Ribs", "The BBQ Pit", "12 Milwaukee St - Regina", 2, 44.15, "June 2, 2026"));
//        db.createMeal(new Meal("Chef Dave's Tortellini", "Big Spoon", "5515 Anglefort Ave", 3, 28.99, "June 9, 2026"));

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    /**
     * Function resumes the activity after the app finishes in another activity
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //Recreate the meals array from the database in case a meal was added or deleted
            fillMealsArray();
            //Pass the recreated array into the adapter to replace the old array
            adapter.refillMealArray(meals);

            //Add a new favourite meal every time the activity resumes
            addBestMeal();

            //Update the recycler view so it reflects the new database
            adapter.notifyDataSetChanged();
        }
        else {
            //If nothing was changed in the database you only need to change the favourite food
            addBestMeal();
        }
    }


    public void addMealClicked(View actionView)
    {
        //Open the activity to add a meal to the database when the add a meal button is clicked
        Intent intent = new Intent(this, AddMeal.class);
        this.startActivityForResult(intent, 1);
    }

    /**
     * Function will make a new arrayList with every item in the database
     */
    public void fillMealsArray()
    {
        meals = new ArrayList<>();

        MealDBHelper db = new MealDBHelper(this);
        db.open();
        Cursor C;
        C = db.getAllMeals();
        if (C.moveToFirst())
        {
            do
            {
                meals.add(new Meal(C.getLong(0), C.getString(1), C.getString(2), C.getString(3), Integer.parseInt(C.getString(4)), Double.parseDouble(C.getString(5)), C.getString(6)));
                //Log.i("Row: ", C.getLong(0) + ", " + C.getString(1) + ", " + C.getString(2) + ", " + C.getString(3)+ ", " + C.getInt(4)+ ", " + C.getDouble(5)+ ", " + C.getString(6));
            } while (C.moveToNext());
        }
        db.close();

    }

    /**
     * Query the database for all item items with a 5 rating. Pick a random one
     * and set it as the past favourite displayed on the main screen
     */
    public void addBestMeal()
    {
        obRand = new Random();

        MealDBHelper db = new MealDBHelper(this);
        db.open();
        Cursor C;
        C = db.getBestMeals();
        if (C.moveToPosition((int) (Math.random() * C.getCount())))
        {
            txtBestMeal.setText(C.getString(1));
            txtBestRest.setText(C.getString(2));
            txtBestRating.setText(C.getInt(4) + "/5");
        }
        else
        {
            txtBestMeal.setText("Nothing Yet!");
            txtBestRest.setText("Go Find A Favourite!");
            txtBestRating.setText("");
        }
        db.close();
    }

}