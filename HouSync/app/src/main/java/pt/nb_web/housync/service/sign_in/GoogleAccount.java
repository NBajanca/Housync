package pt.nb_web.housync.service.sign_in;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import pt.nb_web.housync.activities.LogInActivity;
import pt.nb_web.housync.R;

/**
 * Created by Nuno on 20/02/2016.
 */
public class GoogleAccount implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        SignInAccount{

    private static LogInActivity activity = null;

    private GoogleSignInAccount googleSignInAccount;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;

    private TextView statusTextView;
    private ProgressDialog progressDialog;

    private static final String TAG = "GoogleLogInActivity";
    private static final int RC_SIGN_IN = 9001;


    public GoogleAccount(LogInActivity activity){
        setActivity(activity);

        // Configure SignIn
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        // Build Client
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //View
        statusTextView = (TextView) getActivity().findViewById(R.id.google_status);

        // Button listeners
        getActivity().findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        getActivity().findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        getActivity().findViewById(R.id.google_disconnect_button).setOnClickListener(this);

        // Customize Button
        SignInButton signInButton = (SignInButton) getActivity().findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(googleSignInOptions.getScopeArray());

    }

    @Override
    public void onStart(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // User's cached credentials are valid
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }

    }

    public static int getRcSignIn() {
        return RC_SIGN_IN;
    }

    private static LogInActivity getActivity() {
        return activity;
    }

    private static void setActivity(LogInActivity activity) {
        GoogleAccount.activity = activity;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    private void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
    }

    @Override
    public void onLogInActivityResult(int requestCode, int resultCode, Intent data) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            setGoogleSignInAccount(result.getSignInAccount());
            updateGoogleUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            setGoogleSignInAccount(null);
            updateGoogleUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateGoogleUI(false);
                        setGoogleSignInAccount(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateGoogleUI(false);
                        setGoogleSignInAccount(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getActivity().getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void updateGoogleUI(boolean signedIn) {
        if (signedIn) {
            statusTextView.setText(getActivity().getString(R.string.signed_in_fmt, getGoogleSignInAccount().getDisplayName()));
            getActivity().findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            statusTextView.setText(R.string.signed_out);
            getActivity().findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.google_sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.google_sign_out_button:
                signOut();
                break;
            case R.id.google_disconnect_button:
                revokeAccess();
                break;
        }
    }
}
