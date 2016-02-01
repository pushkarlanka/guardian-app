package com.example.pushkar.guardian;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;


import java.util.HashMap;

import models.GetTokenTask;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    private SharedPreferences mSharedPrefs;

    private String mEmail;
//    private Account mAccount;

    private String fireBaseURL = "https://resplendent-inferno-4484.firebaseio.com";

    private String sharedPrefsUIDKey = "uid";
    private String sharedPrefsDefVal = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        final Intent mapIntent = new Intent(MainActivity.this, DrawerActivity.class);
        String uid = mSharedPrefs.getString(sharedPrefsUIDKey, sharedPrefsDefVal);
        Log.d("shared prefs loggedIn: ", mSharedPrefs.getBoolean("loggedIn", false) + "");
        Log.d("shared prefs UID: ", uid);
        Log.d("shared prefs NAME: ", mSharedPrefs.getString("name", "false"));
        Log.d("shared prefs EMAIL: ", mSharedPrefs.getString("email", "false"));

        if(!uid.equals(sharedPrefsDefVal)) {
//            AppBase appBase = ((AppBase) getApplicationContext());
//            appBase.setUserID(uid);
            startActivity(mapIntent);
        }

//        Button map_btn = (Button) findViewById(R.id.map_btn);
//        map_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(mapIntent);
//            }
//        });

        LinearLayout userAuthButton = (LinearLayout) findViewById(R.id.user_auth_btn);
        userAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] accountTypes = new String[]{"com.google"};
                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                        accountTypes, false, null, null, null, null);
                startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//                mAccount = new Account(AccountManager.KEY_ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_TYPE);
                // With the account name acquired, go get the auth token
                fetchTokenFromAccount();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.

                Toast.makeText(this, "Fool, pick something", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            fetchTokenFromAccount();
        }
    }

    private void fetchTokenFromAccount() {
        if(networkConnected()) {
            String scope = "oauth2:https://www.googleapis.com/auth/plus.login";
            new GetTokenTask(MainActivity.this, mEmail, scope).execute();
        } else {
            Toast.makeText(this, "Fool, you're not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTokenReceived(String token) {
        Log.d("Ay", "Ay");
        final Firebase ref = new Firebase(fireBaseURL);
        ref.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // the Google user is now authenticated with your Firebase app

//                Log.d("USER DETAILS: ", authData.getProviderData().get("displayName") + "");
//                Log.d("USER DETAILS: ", authData.getProviderData().get("id") + "");
//                Log.d("USER DETAILS: ", authData.getProviderData().get("email") + "");
//                Log.d("USER DETAILS: ", authData.getProviderData().get("profileImageURL") + "");

                HashMap<String, Object> userDetailsMap = new HashMap<>();
                if(authData.getProviderData().containsKey("displayName")) {
                    userDetailsMap.put("name", authData.getProviderData().get("displayName"));
                    userDetailsMap.put("loggedIn", true);
                    userDetailsMap.put("email", mEmail);
                }

                Firebase usersRef = new Firebase(fireBaseURL).child("users").child(authData.getUid());
                usersRef.updateChildren(userDetailsMap);

                Toast.makeText(MainActivity.this, "Authenticated!!!", Toast.LENGTH_SHORT).show();

//                AppBase appBase = (AppBase) getApplicationContext();
//                appBase.setUserID(authData.getUid());
//                appBase.setUserName(userDetailsMap.get("name").toString());
//                appBase.setUserEmail(userDetailsMap.get("email").toString());

                mSharedPrefs.edit().putString(sharedPrefsUIDKey, authData.getUid()).putString("name", userDetailsMap.get("name").toString()).
                        putString("email", userDetailsMap.get("email").toString()).apply();

                startActivity(new Intent(MainActivity.this, DrawerActivity.class));
//                startActivity(new Intent(MainActivity.this, MapsActivity.class));

            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
            }
        });
    }

    private boolean networkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }
}
