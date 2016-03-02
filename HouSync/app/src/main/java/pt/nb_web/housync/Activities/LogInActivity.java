package pt.nb_web.housync.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.FacebookSdk;

import pt.nb_web.housync.R;
import pt.nb_web.housync.service.sign_in.FacebookAccount;
import pt.nb_web.housync.service.sign_in.GoogleAccount;
import pt.nb_web.housync.background.LogInAsyncTask;
import pt.nb_web.housync.service.sign_in.SignInAccount;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.ProgressDialogHelper;

public class LogInActivity extends AppCompatActivity{

    private GoogleAccount googleAccount;
    private FacebookAccount facebookAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        googleAccount = new GoogleAccount(this);
        facebookAccount = new FacebookAccount(this);
    }


    public void onStart() {
        super.onStart();

        googleAccount.onStart();
        facebookAccount.onStart();
        updateLoginState();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == googleAccount.getRcSignIn()) {
            googleAccount.onLogInActivityResult(requestCode, resultCode, data);
            if (googleAccount.getGoogleSignInAccount() != null) {
                ProgressDialogHelper.show(this, getString(R.string.signing_in));
                new LogInAsyncTask(this).execute((SignInAccount) googleAccount);
            }
        }else{
            facebookAccount.onLogInActivityResult(requestCode, resultCode, data);
            if (facebookAccount.getAccessToken() != null) {
                ProgressDialogHelper.show(this, getString(R.string.signing_in));
                new LogInAsyncTask(this).execute((SignInAccount) facebookAccount);
            }
        }
    }

    public void onStop(){
        super.onStop();
        updateLoginState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facebookAccount.onDestroy();
    }

    private void updateLoginState(){
        UserLogIn userLogIn = UserLogIn.getInstance(this);

        if(userLogIn.checkIfLogedIn()){
            Log.d("updateLoginState", "Valid LogIn");
            if((googleAccount.getGoogleSignInAccount() == null) && (facebookAccount.getAccessToken() == null)){
                Log.d("updateLoginState", "LogIn Invalid, Loging out");
                userLogIn.clearUser();
            }
        }else{
            Log.d("updateLoginState", "No valid Login");
            if(googleAccount.getGoogleSignInAccount() != null){
                Log.d("updateLoginState", "Login in with Google");
                ProgressDialogHelper.show(this, getString(R.string.signing_in));
                new LogInAsyncTask(this).execute((SignInAccount) googleAccount);
            }else if(facebookAccount.getAccessToken() != null) {
                Log.d("updateLoginState", "Login in with Facebook");
                ProgressDialogHelper.show(this, getString(R.string.signing_in));
                new LogInAsyncTask(this).execute((SignInAccount) facebookAccount);
            }
        }
    }

}
