package com.breg.scavengerhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CollectionActivity extends AppCompatActivity {

    public static final String DATA = "com.breg.scavengerhunt";
    TextView shovelText, compassText, mapText;
    LinearLayout shovelLayout, compassLayout, mapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        Log.d("Test", "onCreate 1");

        shovelText = (TextView) findViewById(R.id.textShovel);
        compassText = (TextView) findViewById(R.id.textCompass);
        mapText = (TextView) findViewById(R.id.textMap);

        shovelLayout = (LinearLayout) findViewById(R.id.layoutShovel);
        compassLayout = (LinearLayout) findViewById(R.id.layoutCompass);
        mapLayout = (LinearLayout) findViewById(R.id.layoutMap);

        Log.d("Test", "onCreate 2");
        checkItems();
        Log.d("Test", "onCreate 3");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_scavenge(View v){

        //Intent newActivity1 = new Intent(CollectionActivity.this, MapsActivity.class);
        //startActivity(newActivity1);
        finish();
    }

    public void checkItems(){

        Log.d("Test", "checkItems 1");
        SharedPreferences sp = getSharedPreferences(DATA, MODE_PRIVATE);

        boolean shovel, compass, map;
        shovel = sp.getBoolean("shovelFound", false);
        compass = sp.getBoolean("compassFound", false);
        map = sp.getBoolean("mapFound", false);
        Log.d("Test", "checkItems 2");
        if(shovel){
            shovelText.setText("You have the shovel!");
            shovelLayout.setBackgroundColor(Color.parseColor("#981CFF00"));
        }
        if(compass){
            compassText.setText("You have the compass!");
            compassLayout.setBackgroundColor(Color.parseColor("#981CFF00"));
        }
        if(map){
            mapText.setText("You have the map!");
            mapLayout.setBackgroundColor(Color.parseColor("#981CFF00"));
        }
    }
}
