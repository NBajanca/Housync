package pt.nb_web.housync.SignInServices;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import pt.nb_web.housync.Activities.LogInActivity;
import pt.nb_web.housync.R;

/**
 * Created by Nuno on 20/02/2016.
 */
public class FacebookAccount implements SignInAccount{

    private static LogInActivity activity = null;

    private static final String TAG = "FacebookLogInActivity";

    private TextView statusTextView;

    LoginButton loginButton;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    private String userName;
    private String email;


    public FacebookAccount(LogInActivity logInActivity) {
        setActivity(logInActivity);

        // Views
        statusTextView = (TextView) getActivity().findViewById(R.id.facebook_status);


        //Facebook buttons
        loginButton = (LoginButton) getActivity().findViewById(R.id.facebook_sign_in_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookSignInResult();
            }

            @Override
            public void onCancel() {
                statusTextView.setText(R.string.signing_in_canceled);
            }

            @Override
            public void onError(FacebookException exception) {
                statusTextView.setText(R.string.signing_in_error);
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    statusTextView.setText(R.string.signed_out);
                }
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
    }

    @Override
    public void onStart() {
        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null){
            handleFacebookSignInResult();
        }
    }

    public void onDestroy(){
        accessTokenTracker.stopTracking();
    }

    public static LogInActivity getActivity() {
        return activity;
    }

    public static void setActivity(LogInActivity activity) {
        FacebookAccount.activity = activity;
    }

    public AccessToken getAccessToken(){
        return AccessToken.getCurrentAccessToken();
    }

    @Override
    public void onLogInActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookSignInResult() {
        Log.d(TAG, "handleFacebookSignInResult: success");
        getUserInfo();
    }

    public void getUserInfo(){
        if(AccessToken.getCurrentAccessToken() == null)
            return;

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                            GraphResponse response) {

                        try {
                            userName= object.getString ("name");
                            email = object.getString("email");
                            statusTextView.setText(getActivity().getString(R.string.signed_in_fmt, userName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
