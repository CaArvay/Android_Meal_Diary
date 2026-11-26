package com.example.colearvay_project;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMeal extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    ActivityResultLauncher<String[]> locationPermissionLauncher;

    private MealDBHelper db;

    private ImageButton imgbtnAddPhoto;
    private EditText edtRestaurantName;
    private EditText edtMealName;
    private EditText edtPrice;
    private EditText edtDate;
    private ImageButton imgbtnStar1;
    private ImageButton imgbtnStar2;
    private ImageButton imgbtnStar3;
    private ImageButton imgbtnStar4;
    private ImageButton imgbtnStar5;
    private DateFormat formatter = new SimpleDateFormat("MMMM d, YYY");
    Date today = new Date();
    String sToday = formatter.format(today);

    static final int CAMERA_REQUEST_CODE = 1;

    private int starRating = 1;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assignControls();

        //Automatically prompt for location permission... Doesn't work?
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);

                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if(fineLocationGranted == null && coarseLocationGranted == null){
                        showPermissionDeniedMessage();
                    }
                }
        );

        //Update location info
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                makeUseOfNewLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {      }

            @Override
            public void onProviderEnabled(String provider) {     }

            @Override
            public void onProviderDisabled(String provider) {   }

        };

        try
        {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 5, locationListener);
        }
        catch (SecurityException e) // if permission not granted
        {

        }

    }

    /**
     * Function uses location information to find the current address the user is at
     * @param location
     */
    private void makeUseOfNewLocation(Location location)
    {
        //Create list to hold the user's most recent locations
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        try
        {
            //using user's latitude and longitude add most recent address to List
            addresses = geocoder.getFromLocation(lat, lon, 1);
        }
        catch (IOException exp)
        {

        }

        //Make sure the list is not empty and then grab address information from first item in List
        if (!(addresses == null || addresses.isEmpty()))
        {
//            address = addresses.get(0).getAddressLine(1);
            address = addresses.get(0).getSubThoroughfare();
            address += " " + addresses.get(0).getThoroughfare();
            address += " - " + addresses.get(0).getLocality();
        }
        else
        {
            //If List is empty report no location found
            address = "No Location Found";
        }

    }

    /**
     * Stop listening for location once the activity is stopped
     */
    protected void onStop()
    {
        locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    /**
     * When the activity resumes look for new locaiton data
     */
    protected void onResume()
    {
        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
        }
        catch (SecurityException e) // if permission not granted
        {

        }
        super.onResume();
    }

    private void showPermissionDeniedMessage()
    {
        Toast.makeText(this, "Permission denied. Feature disabled", Toast.LENGTH_SHORT).show();
    }


    /**
     * Function assigns variable to each item from the View
     */
    private void assignControls()
    {
        db = new MealDBHelper(this);
        edtRestaurantName = (EditText)findViewById(R.id.edtRestaurantName);
        edtMealName = (EditText)findViewById(R.id.edtMealName);
        edtPrice = (EditText)findViewById(R.id.edtPrice);
        edtDate = (EditText)findViewById(R.id.edtDate);
        imgbtnAddPhoto = (ImageButton) findViewById(R.id.imgbtnAddPhoto);
        imgbtnStar1 = (ImageButton) findViewById(R.id.imgbtnStar1);
        imgbtnStar2 = (ImageButton) findViewById(R.id.imgbtnStar2);
        imgbtnStar3 = (ImageButton) findViewById(R.id.imgbtnStar3);
        imgbtnStar4 = (ImageButton) findViewById(R.id.imgbtnStar4);
        imgbtnStar5 = (ImageButton) findViewById(R.id.imgbtnStar5);

        edtDate.setText(sToday);
    }

    /**
     * Function for save button. Update the database with the new item when clicked.
     * Exit the activity and return to the main screen
     * @param actionView
     */
    public void saveClicked(View actionView)
    {
        newMeal();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Function for adding a new item to the database. Create a new meal with the info
     * from the user, pass new meal to database to add it
     */
    private void newMeal()
    {
        String name = edtMealName.getText().toString().trim();
        String restaurant = edtRestaurantName.getText().toString().trim();

        //Make sure the number is a double. If not set it to 0.00
        Double price;
        try
        {
            price = Double.parseDouble(edtPrice.getText().toString().trim());
        }
        catch (NumberFormatException exp)
        {
            price = 0.00;
        }

        //Create new meal in the database. Double check that the meal name and
        // restaurant fields were not left empty. If they were set default values
        db.open();
        Meal newMeal = new Meal(
                name.equals("") ? "No Meal Given" : name,
                restaurant.equals("") ? "No Restaurant Given" : restaurant,
                address,
                starRating,
                price,
                sToday);
        db.createMeal(newMeal);
        db.close();
    }

    /**
     * Function for the one star image button. Change the star rating variable to one
     * and set the star images accordingly
     * @param v
     */
    public void oneStar(View v)
    {
        starRating = 1;

        imgbtnStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar2.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnStar3.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnStar4.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the two star image button. Change the star rating variable to two
     * and set the star images accordingly
     * @param v
     */
    public void twoStar(View v)
    {
        starRating = 2;

        imgbtnStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar3.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnStar4.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the three star image button. Change the star rating variable to three
     * and set the star images accordingly
     * @param v
     */
    public void threeStar(View v)
    {
        starRating = 3;

        imgbtnStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar3.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar4.setImageResource(android.R.drawable.btn_star_big_off);
        imgbtnStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the four star image button. Change the star rating variable to four
     * and set the star images accordingly
     * @param v
     */
    public void fourStar(View v)
    {
        starRating = 4;

        imgbtnStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar3.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar4.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar5.setImageResource(android.R.drawable.btn_star_big_off);
    }

    /**
     * Function for the five star image button. Change the star rating variable to five
     * and set the star images accordingly
     * @param v
     */
    public void fiveStar(View v)
    {
        starRating = 5;

        imgbtnStar1.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar2.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar3.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar4.setImageResource(android.R.drawable.btn_star_big_on);
        imgbtnStar5.setImageResource(android.R.drawable.btn_star_big_on);
    }

    /**
     * When camera image button is hit start a new activty with the camera. Attempted
     * to store photo taken as a file for later but could never figure out how :(
     * @param v
     */
    public void cameraClicked(View v)
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        if(cameraIntent.resolveActivity(getPackageManager()) != null)
//        {
//            try
//            {
//                photoFile = createImageFile();
//            }
//            catch (IOException exp)
//            {
//
//            }
//            if (photoFile != null)
//            {
//                photoUri = FileProvider.getUriForFile(
//                        this,
//                        getPackageName() + ".fileprovider",
//                        photoFile);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//            }

//        }

    }

    /**
     * Function that would create a file for a new photo
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * When camera activity ends get photo back from camera and set it as the new image for the image button
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            Bitmap imageBitmap = (Bitmap) b.get("data");
            imgbtnAddPhoto.setImageBitmap(imageBitmap);
        }
    }

}