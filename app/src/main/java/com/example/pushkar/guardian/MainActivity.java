package com.example.pushkar.guardian;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        Button map_btn = (Button) findViewById(R.id.map_btn);
        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
//        com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                textView.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
//            }
//        };
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                textView.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.d("Latitude ", "status");
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.d("Latitude ", "enabled");
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.d("Latitude ", "disabled");
//            }
//        };
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        try {
////            mMap.setMyLocationEnabled(true);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
////            locationManager.re
//
//            Log.d("req", "first");
//        } catch (SecurityException e) {}


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Connect the client.
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        // Disconnecting the client invalidates it.
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000); // Update location every second
//
//        try {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        } catch (SecurityException e) {}
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d("TAG: ", "GoogleApiClient connection has been suspend");
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d("TAG: ", "GoogleApiClient connection has failed");
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mTextView.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
//    }
}
