package com.breg.scavengerhunt;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by darkhobbo on 2/25/2016.
 */
public class PullJSON extends AsyncTask<String, String, String>{
    Context ctx;
    int id,Score;
    double  Latitude,Longitude;
    String Title,DESC,DATE,ACTION,Time;

    public PullJSON(Context newCtx){
        ctx = newCtx;
    }

    @Override
    protected String doInBackground(String... params){
        HttpURLConnection conn = null;
        String URL_LINK = "http://courseattendance.com/accessdb/accessdb_r_Instructor.php";
        try {
            //constants
            URL url = new URL(URL_LINK);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /*milliseconds*/);
            conn.setConnectTimeout(1000 /* milliseconds */);  // Temp. Fix for Issue 2. Reduce connection timeout
            conn.setUseCaches(false); // Temp. Fix for Issue 2. Clear Cache - Exception in Android L #79 - https://github.com/square/okio/issues/79
            conn.setRequestMethod("GET");
            conn.setAllowUserInteraction(false);
            //open
            conn.connect();

            int status = conn.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder("");
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    Log.d("JSON", "JSON = " + sb.toString());
                    String testnull = sb.toString();
                    if (testnull.equals("null")){
                        Log.d("JSON","** ==========  N O     D A T  A    F O U N D   ======== **");
                    }
                    else {
                        decodeJSON(sb.toString());
                    }
                    break;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public void decodeJSON(String objs) {
        try {
            DBAdapter dbAdapter = new DBAdapter(ctx);
            dbAdapter.open();
            Log.d("JSON", "** ========== R E A D I N G   J A S O N   A R R A Y ======== **");
            JSONArray jsonObjects = new JSONArray(objs);
            for (int x = 0; x < jsonObjects.length(); x++) {
                JSONObject oneObject = jsonObjects.getJSONObject(x);
                id = oneObject.getInt("KEY_ROWID");
                Title = oneObject.getString("TITLE");
                DESC = oneObject.getString("DESC");
                DATE = oneObject.getString("DATE");
                ACTION = oneObject.getString("ACTION");

                String Lat_temp = oneObject.getString("Latitude");
                if (Lat_temp == "" || Lat_temp == "null" || Lat_temp.isEmpty())
                    Latitude = 0.0;
                else
                    Latitude = Double.valueOf(Lat_temp);
                String Long_temp = oneObject.getString("Longitude");
                if (Long_temp == "" || Long_temp == "null" || Long_temp.isEmpty())
                    Longitude = 0.0;
                else
                    Longitude = Double.valueOf(Long_temp);

                Time = oneObject.getString("Time");
                String Score_temp = oneObject.getString("Score");
                if (Score_temp == "" || Score_temp == "null" || Score_temp.isEmpty())
                    Score = 0;
                else
                    Score = Integer.valueOf(Score_temp);

                Log.d("JSON", "** ID = " + String.valueOf(id));
                Log.d("JSON", "** Title = " + Title);
                Log.d("JSON", "** DESC = " + DESC);
                Log.d("JSON", "** DATE = " + DATE);
                Log.d("JSON", "** ACTION = " + ACTION);
                Log.d("JSON", "** Latitude = " + Latitude);
                Log.d("JSON", "** Longitude = " + Longitude);
                Log.d("JSON", "** Time = " + Time);
                Log.d("JSON", "** Score = " + Score);
                Log.d("JSON", "** ================== **");
                // Adding data to local SQLDB.
                dbAdapter.insertRow(Title, DESC, DATE, ACTION, Latitude, Longitude, Time, Score);
            }
            dbAdapter.close();
        }catch (Exception e){//JSONException e) {
            e.printStackTrace();
        }
    }
}
