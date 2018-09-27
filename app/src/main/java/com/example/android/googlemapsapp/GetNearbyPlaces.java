package com.example.android.googlemapsapp;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    InputStream is;
    BufferedReader bufferedReader;
    StringBuffer stringBuffer;
    String data;
    HttpURLConnection httpURLConnection;

    public GetNearbyPlaces(MapActivity mapActivity) {
    }

    public GetNearbyPlaces(OnCompleteListener onCompleteListener) {
    }

    @Override
        protected String doInBackground (Object...objects){
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            try {
                URL myurl = new URL(url);
              httpURLConnection = (HttpURLConnection) myurl.openConnection();
                httpURLConnection.connect();
                is = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                stringBuffer = new StringBuffer();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                data = stringBuffer.toString();
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpURLConnection.disconnect();

            }
            return data;
        }

        @Override
        protected void onPostExecute (String s){

            try {
                JSONObject parentObject = new JSONObject(s);
                JSONArray resultArray = parentObject.getJSONArray("results");

                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject jsonObject = resultArray.getJSONObject(i);
                    JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                    String latitude = location.getString("lat");
                    String longitude = location.getString("lng");

                    JSONObject nameObject = resultArray.getJSONObject(i);
                    String name_place = nameObject.getString("name");
                    String vicinity = nameObject.getString("vicinity");

                    LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(vicinity);
                    markerOptions.position(latlng);

                    mMap.addMarker(markerOptions);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

}
