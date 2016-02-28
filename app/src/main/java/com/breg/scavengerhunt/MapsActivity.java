package com.breg.scavengerhunt;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d("Test", "**************************************************" +
                "\n**************************************************");
    }

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
                 mMap.addMarker(new MarkerOptions()
                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.cross))
                         .anchor(.5f, .5f) // Anchors the marker on the bottom left
                         .position(new LatLng(latitude, longitude)));
                Log.d("Test", "New marker added.");
            }
            //thisRow = "";
            c.moveToNext();
        }
        c.close();
    }
}