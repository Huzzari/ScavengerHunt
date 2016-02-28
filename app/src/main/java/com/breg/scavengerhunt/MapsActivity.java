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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.HttpURLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //LatLng min1, min2, min3, max1, max2, max3, mid1, mid2, mid3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Doing a test data pull. This also adds the data to the local db.
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();

        // Pull data if local db has no rows.
        if(dbAdapter.getAllRows().getCount() == 0)
            new PullJSON(this).execute();

        dbAdapter.close();




        /*
            Data is now stored locally.
            To pull data, we use dbAdapter.getAllRows() and dbAdapter.getRow(rowId)
            A cursor will be returned for both of those methods.

            To deal with the curson, we will create a cursor object.

            Cursor c = dbAdapter.getAllRows();

            We then just get each data point.



         */

        DBAdapter testAdapter = new DBAdapter(this);
        testAdapter.open();
        Cursor c = testAdapter.getAllRows();
        testAdapter.close();
        Log.d("test", "Row count: " + c.getCount());
        String thisRow = "";

        //push values into variables

        while(!c.isAfterLast()){
            thisRow += "TITLE: " + c.getString(c.getColumnIndex("title")) + "\n";
            thisRow += "DESC: " + c.getString(c.getColumnIndex("desc")) + "\n";
            thisRow += "DATE: " + c.getString(c.getColumnIndex("date")) + "\n";
            thisRow += "ACTION: " + c.getString(c.getColumnIndex("action")) + "\n";
            thisRow += "LATITUDE: " + c.getDouble(c.getColumnIndex("latitude")) + "\n";
            thisRow += "LONGITUDE: " + c.getDouble(c.getColumnIndex("longitude")) + "\n";
            thisRow += "TIME: " + c.getDouble(c.getColumnIndex("time")) + "\n";
            thisRow += "SCORE: " + c.getDouble(c.getColumnIndex("score")) + "\n";
            thisRow += "***************************************************************\n";
            Log.d("Row", thisRow);
            thisRow = "";
            c.moveToNext();
        }
        c.close();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng mid1 = new LatLng(50.67046254, -120.3623406);
        LatLng OM = new LatLng(50.670983, -120.362951);
        mMap.addMarker(new MarkerOptions().position(mid1).title("Item 1"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(OM));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null);
    }

    public void onClick_collection(View v){

        Intent newActivity1 = new Intent(MapsActivity.this, CollectionActivity.class);
        startActivity(newActivity1);

    }

    public void onClick_TTS(View v){



    }
}