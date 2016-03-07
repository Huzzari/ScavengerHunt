package com.breg.scavengerhunt;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.Set;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    String bestProvider;
    double latitude = 0.0;
    double longitude = 0.0;
    int counter =0;
    int locationNum=1;
    int row, column;
    double[][] itemLocations =  new double[3][4];
    TextView text1;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker shovelMark, compassMark, mapMark;

    public static final String DATA = "com.breg.scavengerhunt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeData();
        //text1 = (TextView) findViewById(R.id.textView);

        Log.d("Test", "**************************************************" +
                "\n**************************************************");



        //initiate the GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        Log.d("Test", "1");
        // 1. choose the best location provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        bestProvider = locationManager.getBestProvider(criteria, true);
        Log.d("Location", "1- Recommended Location provider is " + bestProvider);
        Log.d("Test", "2");
        //lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
        //UpdateLocation(lastKnownLocation, "Last Known Location");

        Log.d("Test", "3");
        // 2. Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("Location", "2- A new location is found by the location provider ");
                UpdateLocation(location, "Location Changed");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


    }//end of onCreate

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng point1 = new LatLng(50.67046254, -120.3623406);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point1,16));
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();

        // Pull data if local db has no rows.
        if(dbAdapter.getAllRows().getCount() == 0){
            PullJSON pull = new PullJSON(this);
            pull.execute();
            try{
                if(pull.get().equals("done")) // Update map now with the local SQL DB data points.
                    addLocations(googleMap);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else // Update map with local SQL DB data points.
            addLocations(googleMap);
        dbAdapter.close();
    }

    public void onClick_collection(View v){
        Intent newActivity1 = new Intent(MapsActivity.this, CollectionActivity.class);
        startActivity(newActivity1);
    }

    public void addLocations(GoogleMap googleMap){
        row=0;
        column=0;
        DBAdapter testAdapter = new DBAdapter(this);
        testAdapter.open();
        Cursor c = testAdapter.getAllRows();
        testAdapter.close();
        //String thisRow = "";
        String centerPoint, title;
        double latitude, longitude;

        while(!c.isAfterLast()){
            title = c.getString(c.getColumnIndex("title"));
            centerPoint = c.getString(c.getColumnIndex("desc"));
            latitude = c.getDouble(c.getColumnIndex("latitude"));
            longitude = c.getDouble(c.getColumnIndex("longitude"));

            /*
            thisRow += "TITLE: " + title + "\n";
            thisRow += "DESC: " + centerPoint + "\n";
            thisRow += "DATE: " + c.getString(c.getColumnIndex("date")) + "\n";
            thisRow += "ACTION: " + c.getString(c.getColumnIndex("action")) + "\n";
            thisRow += "LATITUDE: " + latitude + "\n";
            thisRow += "LONGITUDE: " + longitude + "\n";
            thisRow += "TIME: " + c.getDouble(c.getColumnIndex("time")) + "\n";
            thisRow += "SCORE: " + c.getDouble(c.getColumnIndex("score")) + "\n";
            thisRow += "***************************************************************\n";
            Log.d("Test", thisRow);*/

            if(c.getString(c.getColumnIndex("desc")).equals("Center")){


                // This is a center point.
                Log.d("Test", title + " has " + centerPoint + " at lat: " + latitude + " long: " + longitude);
                // Add this to gmap.
                if(row == 0) {
                    shovelMark = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shovel1))
                            .anchor(.5f, .5f) // Anchors the marker on the bottom left
                            .position(new LatLng(latitude, longitude)));
                    Log.d("Test", "New marker added.");
                    counter = 0;
                    row++;
                }
                else if(row == 1) {
                    compassMark = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.compass1))
                            .anchor(.5f, .5f) // Anchors the marker on the bottom left
                            .position(new LatLng(latitude, longitude)));
                    Log.d("Test", "New marker added.");
                    counter = 0;
                    row++;
                }
                else if(row == 2) {
                    mapMark = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map1))
                            .anchor(.5f, .5f) // Anchors the marker on the bottom left
                            .position(new LatLng(latitude, longitude)));
                    Log.d("Test", "New marker added.");
                    counter = 0;
                    row++;
                }
            }
            else if(counter == 0){
                itemLocations[row][0] = latitude;
                itemLocations[row][1] = longitude;
                counter++;
            }
            else if(counter == 1){
                itemLocations[row][2] = latitude;
                itemLocations[row][3] = longitude;
                counter++;
            }
            //thisRow = "";
            c.moveToNext();
        }
        c.close();
        checkMarks();
    }//end of addLocations

    public void UpdateLocation(Location location, String state){
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("Test", "#" + locationNum + "Location" + "** " + state + " ** - Lattitue = " + latitude + ", and Longitude = " + longitude);
        //text1.setText("#" + locationNum + "Location: " + state + " ** - \nLattitue = " + latitude + "\nLongitude = " + longitude);
        locationNum++;
    }

    public boolean itemSearch(View v){
        boolean result = false;
        SharedPreferences sp = getSharedPreferences(DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        boolean shovel, compass, map;

        shovel = sp.getBoolean("shovelFound", false);
        compass = sp.getBoolean("compassFound", false);
        map = sp.getBoolean("mapFound", false);

        //testValues
        //-----------------------------------------------------------------------------------------

        //shovel
        //latitude = 50.67046254;
        //longitude = -120.3623406;

        //compass
        //latitude = 50.67088255;
        //longitude = -120.3621892;

        //map
        //latitude = 50.67129548;
        //longitude = -120.363716;
        //-----------------------------------------------------------------------------------------


        if((latitude<itemLocations[0][0])&&(latitude>itemLocations[0][2])){
            if((longitude<itemLocations[0][1])&&(longitude>itemLocations[0][3])){
                if(!shovel) {
                    //found the shovel

                    editor.putBoolean("shovelFound", true);
                    editor.commit();
                    shovelMark.remove();
                    Toast.makeText(MapsActivity.this, "Congratulations! You found the shovel!", Toast.LENGTH_SHORT).show();

                    result = true;
                }
            }
        }
        else if((latitude<itemLocations[1][0])&&(latitude>itemLocations[1][2])){
            if((longitude<itemLocations[1][1])&&(longitude>itemLocations[1][3])) {
                if(!compass) {


                    //found the compass

                    editor.putBoolean("compassFound", true);
                    editor.commit();
                    compassMark.remove();
                    Toast.makeText(MapsActivity.this, "Congratulations! You found the compass!", Toast.LENGTH_SHORT).show();

                    result = true;
                }
            }
        }
        else if((latitude<itemLocations[2][0])&&(latitude>itemLocations[2][2])){
            if((longitude<itemLocations[2][1])&&(longitude>itemLocations[2][3])){
                if(!map) {
                    //found the map

                    editor.putBoolean("mapFound", true);
                    editor.commit();
                    mapMark.remove();
                    Toast.makeText(MapsActivity.this, "Congratulations! You found the map!", Toast.LENGTH_SHORT).show();

                    result = true;
                }
            }
        }
        if(!result){
            Toast.makeText(MapsActivity.this, "Move closer to an item.", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
        SharedPreferences sp = getSharedPreferences(DATA, MODE_PRIVATE);


    }

    public void initializeData() {

        SharedPreferences sp = getSharedPreferences(DATA, MODE_PRIVATE);
        // Test to see if Shared Prefs were initialized.
        if(sp.getBoolean("notInitialized", true))
        // Shared prefs have not be declared and initialized yet.
        {
            SharedPreferences.Editor editor = sp.edit();
            Log.d("app", "Creating shared prefs with item data.");

            editor.putBoolean("shovelFound", false);
            editor.putBoolean("compassFound", false);
            editor.putBoolean("mapFound", false);
            editor.putBoolean("notInitialized", false);
            editor.commit();
        }
        else
            Log.d("app", "Data already initialized");
    }

    public void checkMarks(){
        SharedPreferences sp = getSharedPreferences(DATA, MODE_PRIVATE);

        boolean shovel, compass, map;
        shovel = sp.getBoolean("shovelFound", false);
        compass = sp.getBoolean("compassFound", false);
        map = sp.getBoolean("mapFound", false);
        if(shovel){
            shovelMark.remove();
        }
        if(compass){
            compassMark.remove();
        }
        if(map){
            mapMark.remove();
        }
    }


}//end