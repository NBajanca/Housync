package pt.nb_web.housync.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import pt.nb_web.housync.R;

public class LogInActivity extends AppCompatActivity/* implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener*/{

    private static final String TAG = "LogInActivity";

    private GoogleAccount googleAccount;

    private TextView mStatusFacebookTextView;

    LoginButton loginButton;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        googleAccount = new GoogleAccount(this);
        googleAccount.onCreateLogInActivity();

        // Views
        mStatusFacebookTextView = (TextView) findViewById(R.id.facebook_status);


        //Facebook buttons
        loginButton = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        loginButton.setReadPermissions("user_friends");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                    handleFacebookSignInResult();
            }

            @Override
            public void onCancel() {
                mStatusFacebookTextView.setText(R.string.signing_in_canceled);
            }

            @Override
            public void onError(FacebookException exception) {
                mStatusFacebookTextView.setText(R.string.signing_in_error);
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                    if (currentAccessToken == null){
                        mStatusFacebookTextView.setText(R.string.signed_out);
                    }
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
    }


    public void onStart() {
        super.onStart();
        googleAccount.onStart();

        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null){
            handleFacebookSignInResult();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == googleAccount.getRcSignIn()) {
            googleAccount.onLogInActivityResult(data);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    private void handleFacebookSignInResult() {
        Log.d(TAG, "handleFacebookSignInResult: success");
        // Signed in successfully, show authenticated UI.
        //ToDo: Receive login information
        mStatusFacebookTextView.setText(getString(R.string.signed_in_fmt, AccessToken.getCurrentAccessToken().getUserId()));
    }

}
