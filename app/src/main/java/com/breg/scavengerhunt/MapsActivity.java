package com.breg.scavengerhunt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    String bestProvider;
    double latitude;
    double longitude;
    int counter =0;
    int row, column;
    double[][] itemLocations =  new double[3][4];
    TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        text1 = (TextView) findViewById(R.id.textView);

        Log.d("Test", "**************************************************" +
                "\n**************************************************");



        //initiate the GPS
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location1 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);



        Log.d("Test", "1");
        // 1. choose the best location provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        bestProvider = locationManager.getBestProvider(criteria, true);
        Log.d("Test", "2");

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
        String icon = "R.drawable.cross";
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
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shovel1))
                            .anchor(.5f, .5f) // Anchors the marker on the bottom left
                            .position(new LatLng(latitude, longitude)));
                    Log.d("Test", "New marker added.");
                    counter = 0;
                    row++;
                }
                else if(row == 1) {
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.compass1))
                            .anchor(.5f, .5f) // Anchors the marker on the bottom left
                            .position(new LatLng(latitude, longitude)));
                    Log.d("Test", "New marker added.");
                    counter = 0;
                    row++;
                }
                else if(row == 2) {
                    mMap.addMarker(new MarkerOptions()
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
    }//end of addLocations

    public void UpdateLocation(Location location, String state){
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("Location", "** " + state + " ** - Lattitue = " + latitude + ", and Longitude = " + longitude);
        text1.setText("Location: " + state + " ** - Lattitue = " + latitude + ", and Longitude = " + longitude);
    }

    public boolean itemSearch(View v){
        boolean result = false;

        //check current lat long against min max

        return result;
    }

}//end