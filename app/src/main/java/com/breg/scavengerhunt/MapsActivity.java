package com.breg.scavengerhunt;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
}