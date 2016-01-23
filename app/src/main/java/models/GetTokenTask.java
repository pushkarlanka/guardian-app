package models;

import android.os.AsyncTask;
import android.util.Log;

import com.example.pushkar.guardian.MainActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * Created by pushkar on 1/17/16.
 */
public class GetTokenTask extends AsyncTask<Void, Void, Void> {

    private MainActivity mActivity;
    private String mEmail;
    private String mScope;

    public GetTokenTask(MainActivity activity, String email, String scope) {
        mActivity = activity;
        mEmail = email;
        mScope = scope;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String token = fetchToken();
            Log.d("doi", "doi");
            if (token != null) {
                mActivity.onTokenReceived(token);
                // **Insert the good stuff here.**
                // Use the token to access the user's Google data.
            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
        }
        return null;
    }

    protected String fetchToken() throws IOException {
        try {
//            return GoogleAuthUtil.getToken(mActivity, mAccount, mScope);
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
        }
        return null;
    }


}
