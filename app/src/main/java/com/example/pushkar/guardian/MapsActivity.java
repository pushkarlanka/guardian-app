package com.example.pushkar.guardian;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import models.User;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

//    private Marker myUserMarker;
//    private Marker mAbhinavMarker;

    private String firebaseURL = "https://resplendent-inferno-4484.firebaseio.com";

    private double distance = 0.005;

    private boolean mInitialFocus = false;

    private String mUID;

    private HashMap<String, Object> mLocations = new HashMap<>();

    private HashMap<String, Marker> mMarkers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mUID = ((AppBase) getApplication()).getUserID();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        setOnClickListeners();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("LOOK: ", "Placeeeeee: " + place.getName());

                LatLng dest = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(dest).title("Marker in " + place.getName()).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("LOOK: ", "An error occurred: " + status);
            }
        });

        Log.d("testing: ", (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) + "");



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        // disable toolbar buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        enableMyLocation();

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

    }

    private void enableMyLocation() {

        try {
            mMap.setMyLocationEnabled(true);
            Log.d("req", "first");
        } catch (SecurityException e) {}

        repositionLocationButton();
//        Location findme = mMap.getMyLocation();
//        double latitude = findme.getLatitude();
//        double longitude = findme.getLongitude();
//        LatLng latLng = new LatLng(latitude, longitude);

//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        Location myLocation = null;
//        try {
//            myLocation = locationManager.getLastKnownLocation(provider);
//            Log.d("spongebob, ", "squarepants");
//        } catch (SecurityException e) {
//            Log.d("Error occurred: ", e.toString() );
//            return;
//        }
//
//        if (myLocation != null) {
//            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//            mMap.addMarker(new MarkerOptions().position(latLng).title("Your location!!!!!").draggable(true));
//        }

    }

    // position location button on bottom right
    private void repositionLocationButton() {
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {}

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG: ", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TAG: ", "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(!mInitialFocus) {
            mInitialFocus = true;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

//        if(myUserMarker != null) {
//            myUserMarker.remove();
//        }
//
//        myUserMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(location.getLatitude() + ", " + location.getLongitude()).draggable(true));



        mLocations.put(mUID +"/latitude", location.getLatitude());
        mLocations.put("dummy/latitude", location.getLatitude() + distance);
        mLocations.put(mUID + "/longitude", location.getLongitude());
        mLocations.put("dummy/longitude", location.getLongitude() + distance);

        distance += 0.05;

        Firebase usersRef = new Firebase(firebaseURL + "/users");

        usersRef.updateChildren(mLocations);
    }


    private void setOnClickListeners() {
        Firebase usersRef = new Firebase(firebaseURL + "/users");

//        usersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
//                    User user = childSnapshot.getValue(User.class);
//                    Log.d(user.getName() + ": ", user.getLatitude() + ", " + user.getLongitude());
//
//                    if(childSnapshot.getKey().equals(mUID)) {
//                        LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
//                        if(myUserMarker != null) {
//                            myUserMarker.remove();
//                        }
//                        myUserMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).draggable(true));
//                    } else if(childSnapshot.getKey().equals("dummy")) {
//                        LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
//
//                        if(mAbhinavMarker != null) {
//                            mAbhinavMarker.remove();
//                        }
//                        mAbhinavMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).draggable(true));
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {

                User user = dataSnapshot.getValue(User.class);
                Log.d(user.getName() + ": ", user.getLatitude() + ", " + user.getLongitude());

                LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
                String key = dataSnapshot.getKey();     // key is the user's UID

                if(mMarkers.get(key) != null) {
                    mMarkers.get(key).remove();
                }

                if(!key.equals(mUID)) {
                    mMarkers.put(key, (mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_person_marker)).position(latLng).title(user.getName()).draggable(true))));
                } else {
                    mMarkers.put(key, (mMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).draggable(true))));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



}
