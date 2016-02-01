package com.example.pushkar.guardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserMarkerMap;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    // MAP VARIABLES
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String firebaseURL = "https://resplendent-inferno-4484.firebaseio.com";
    private boolean mInitialFocus = false;
    private String mUID;
    private HashMap<String, Object> mLocations = new HashMap<>();
//    private HashMap<String, Marker> mMarkers = new HashMap<>();

    private UserMarkerMap mUserMarkerMap = new UserMarkerMap();
    private HashMap<String, User> mAllUsers = new HashMap<>();

    // OTHER VARIABLES
    private SharedPreferences mSharedPrefs;
    private ChildEventListener mChildEventListener;
    private Firebase mUsersRef;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean allChildrenAdded = false;

    static final double METERS_TO_MILES = 0.000621371;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
        mUID = mSharedPrefs.getString("uid", "false");

        Log.d("oncreate:", "ONCREATE");

        setNavDrawer(toolbar);
        setMap();
        setRecyclerList();
    }

    private void setNavDrawer(Toolbar toolbar) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TextView navHeaderName = (TextView) findViewById(R.id.nav_header_user_name);
        navHeaderName.setText(mSharedPrefs.getString("name", "DNE"));
        TextView navHeaderEmail = (TextView) findViewById(R.id.nav_header_email);
        navHeaderEmail.setText(mSharedPrefs.getString("email", "DNE"));

        CircleImageView navHeaderImage = (CircleImageView) findViewById(R.id.nav_header_image);
        String imageURL = mSharedPrefs.getString("imageURL", "DNE");
        if(!imageURL.equals("DNE")) {
            Picasso.with(this).load(imageURL).noFade().into(navHeaderImage);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        RelativeLayout mapsLayout = (RelativeLayout) findViewById(R.id.maps_layout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.display_map_btn) {
            mapsLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else if(id == R.id.display_list_btn) {
            mapsLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

            mSharedPrefs.edit().clear().apply();

            Firebase ref = new Firebase(firebaseURL).child("users").child(mUID).child("loggedIn");
            ref.setValue(false);

            // MAYBE I SHOULD ALSO DELETE USER ID IN APPBASE?????

            mUsersRef.removeEventListener(mChildEventListener);

            startActivity(new Intent(DrawerActivity.this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    // MAP STUFF BELOW




    // mimics old onCreate
    private void setMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        setOnClickListeners();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("LOOK: ", "Placeeeeee: " + place.getName());

                LatLng dest = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(dest).title("Marker in " + place.getName()).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
            }

            @Override
            public void onError(Status status) {
                Log.d("LOOK: ", "An error occurred: " + status);
            }
        });

        setEmergencyCallButton();
    }

    private void setEmergencyCallButton() {
        final String phoneNumber = "111";

        Button button = (Button) findViewById(R.id.police_call_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            }
        });
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
        mMap.setPadding(0,0,0,100);
        mMap.getUiSettings().setCompassEnabled(false);
        // disable toolbar buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);

        enableMyLocation();

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        mMap.setOnMarkerClickListener(this);

        Log.d("onMap", "ON MAP");
    }

    private void enableMyLocation() {

        try {
            mMap.setMyLocationEnabled(true);
            Log.d("req", "first");
        } catch (SecurityException e) {}

        repositionLocationButton();

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

//    @Override
//    protected void onStop() {
//        // Disconnecting the client invalidates it.
//        mGoogleApiClient.disconnect();
//        Log.d("onStop", "ON STOP");
//        super.onStop();
//    }

    @Override
    protected void onPause() {
        Log.d("onPause", "ON PAUSE");
        super.onPause();
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

        mLocations.put(mUID +"/latitude", location.getLatitude());
        mLocations.put(mUID + "/longitude", location.getLongitude());
        mLocations.put("dummy/latitude", location.getLatitude() + 0.0005);
        mLocations.put("dummy/longitude", location.getLongitude() + 0.0005);
        mLocations.put("random2/latitude", location.getLatitude() - 0.0015);
        mLocations.put("random2/longitude", location.getLongitude() - 0.0015);
        mLocations.put("random3/latitude", location.getLatitude() + 0.0025);
        mLocations.put("random3/longitude", location.getLongitude() - 0.0025);
        mLocations.put("random4/latitude", location.getLatitude() - 0.0035);
        mLocations.put("random4/longitude", location.getLongitude() + 0.0035);
        mLocations.put("random5/latitude", location.getLatitude() - 0.0045);
        mLocations.put("random5/longitude", location.getLongitude() - 0.0045);

        Firebase usersRef = new Firebase(firebaseURL + "/users");

        usersRef.updateChildren(mLocations);
    }


    private void setOnClickListeners() {
        mUsersRef = new Firebase(firebaseURL + "/users");

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("CHILD ADDED", " ADDED");

                User user = dataSnapshot.getValue(User.class);
                Log.d(user.getName() + ": ", user.getLatitude() + ", " + user.getLongitude());

                LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
                String key = dataSnapshot.getKey();     // key is the user's UID

                if(!key.equals(mUID)) {
                    mUserMarkerMap.put(key, (mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_person_marker)).position(latLng).title(user.getName()).draggable(true))));
                }
//                else {
//                    mUserMarkerMap.put(key, (mMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).draggable(true))));
//                }

                mAllUsers.put(key, user);
                allChildrenAdded = false;

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // TODO: Optimize the adding and changing of items to recyclerview. Shouldn't have to call new ListAdapter every time.
                // Ideally, try getting the position of the user being changed from the adapter, using getter/setter.
                // Once you have the position, just use notifyDataSetChanged
                if(!allChildrenAdded) {
                    allChildrenAdded = true;
                    ArrayList<User> list = new ArrayList<>(mAllUsers.values());
                    list.remove(mAllUsers.get(mUID));
                    mAdapter = new UserListAdapter(list, DrawerActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }

                User user = dataSnapshot.getValue(User.class);
                Log.d(user.getName() + ": ", user.getLatitude() + ", " + user.getLongitude());

                LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());
                String key = dataSnapshot.getKey();     // key is the user's UID

                if(mUserMarkerMap.getMarker(key) != null) {
                    mUserMarkerMap.getMarker(key).setPosition(latLng);
                } else if(!key.equals(mUID)) {
                    mUserMarkerMap.put(key, (mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_person_marker)).position(latLng).title(user.getName()).draggable(true))));
                }
//                else {
//                    mUserMarkerMap.put(key, (mMap.addMarker(new MarkerOptions().position(latLng).title(user.getName()).draggable(true))));
//                }

                mAllUsers.put(key, user);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                mUserMarkerMap.getMarker(key).remove();
                mUserMarkerMap.remove(key);
                mAllUsers.remove(key);
                allChildrenAdded = false;
//                mAdapter.notifyDataSetChanged();
//                dataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };

        mUsersRef.addChildEventListener(mChildEventListener);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        String markerUID = mUserMarkerMap.getUserUID(marker);

        if(markerUID != null) {
            Log.d("Clicked UID: ", markerUID);

            User markerUser = mAllUsers.get(markerUID);

            Bundle bundle = new Bundle();
            bundle.putParcelable("user", markerUser);

            String distanceStr = getDistance(markerUser);
            bundle.putString("distance", distanceStr);

            DialogFragment markerDialog = new MarkerDialogFragment();
            markerDialog.setArguments(bundle);

            markerDialog.show(getSupportFragmentManager(), "missiles");
        }

        return true;
    }

    public String getDistance(User otherUser) {

        User myUser = mAllUsers.get(mUID);

        float[] results = new float[2];
        Location.distanceBetween(myUser.getLatitude(), myUser.getLongitude(), otherUser.getLatitude(), otherUser.getLongitude(), results);

        double distance = (((double) results[0]) * METERS_TO_MILES);
        Log.d("LOOK DISTANCE", distance + "");
        String distanceStr = new DecimalFormat("#.##").format(distance);

        return distanceStr;
    }

    private void setRecyclerList() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
    }

//    @UiThread
//    private void dataSetChanged() {
//        mAdapter.notifyDataSetChanged();
//    }

}
