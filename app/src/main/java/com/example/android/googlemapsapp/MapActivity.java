package com.example.android.googlemapsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final String FIne_loc = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_loc = Manifest.permission.ACCESS_COARSE_LOCATION;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationpermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float default_zoom = 15f;
    private GoogleMap mMap;
    GoogleApiClient client;
    LatLng latLngCurrent;
    LocationRequest request;

    private EditText mSearchText;
    private TextView mGotxt;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map works correctly", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationpermissionGranted) {
            getDevicelocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d(TAG, "Locationperm granted in OnMapReady");
            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            init();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGotxt = (TextView) findViewById(R.id.go_txt_view);
        getLocationPermission();
    }

    private void init() {
        Log.d(TAG, "init is getting called");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "search text is " + mSearchText);
                    geoLocate();
                }

                mGotxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        geoLocate();
                    }
                });
                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "Geolocating");
        String searchstring = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchstring, 1);
        } catch (IOException e) {
            Log.e(TAG, "geolocate error msg " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "found location" + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), default_zoom, address.getAddressLine(0));
        }
    }

    private void getDevicelocation() {
        Log.d(TAG, "getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationpermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onCompletion Locationfound");
                            Location currentlocation = (Location) task.getResult();
                            double latitude = currentlocation.getLatitude();
                            double longitude = currentlocation.getLongitude();
                            moveCamera(new LatLng(latitude, longitude), default_zoom, "My Location");
                            StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                            stringBuilder.append("location=" + latitude + "," + longitude);
                            stringBuilder.append("&radius=" + 1000);
                            stringBuilder.append("&key=" + getResources().getString(R.string.google_places_API_key));

                            String url = stringBuilder.toString();

                            Object datatransfer[] = new Object[2];
                            datatransfer[0] = mMap;
                            datatransfer[1] = url;

                            GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(this);
                            getNearbyPlaces.execute(datatransfer);


                        } else {
                            Log.d(TAG, "onComplet current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception" + e.getMessage());
        }

    }

    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.d(TAG, "move Camera" + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latlng)
                .title(title);
        mMap.addMarker(options);

    }

    private void initMap() {
        Log.d(TAG, "Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

//        datatransfer[0]= mMap;
//        GetNearbyplaces getNearbyplaces = new GetNearbyplaces();
//        getNearbyplaces.execute(datatransfer);

    }

    private void getLocationPermission() {
        Log.d(TAG, "Getting loctaion permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FIne_loc) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Coarse_loc) == PackageManager.PERMISSION_GRANTED) {
                mLocationpermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onrequestpermissionResult: called");
        mLocationpermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onrequetspermission failed in the loop");
                            mLocationpermissionGranted = false;
                            return;
                        }
                    }
                    mLocationpermissionGranted = true;
                    Log.d(TAG, "LocationPermissionGranted");
                    //initialize our map
                    initMap();
                }
            }
        }
    }
}





