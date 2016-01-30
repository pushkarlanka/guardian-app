package models;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by pushkar on 1/29/16.
 */
public class UserMarkerMap {

    private HashMap<String, Marker> mUsersMarkers;
    private HashMap<Marker, String> mMarkersUsers;

    public UserMarkerMap() {
        mUsersMarkers = new HashMap<>();
        mMarkersUsers = new HashMap<>();
    }

    public void put(String s, Marker marker) {
        mUsersMarkers.put(s, marker);
        mMarkersUsers.put(marker, s);
    }

    public Marker getMarker(String s) {
        return mUsersMarkers.get(s);
    }

    public String getUserUID(Marker m) {
        return mMarkersUsers.get(m);
    }

    public void remove(String s) {
        Marker m = mUsersMarkers.remove(s);
        mMarkersUsers.remove(m);
    }

}
