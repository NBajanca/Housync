package pt.nb_web.housync.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;

import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;
import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.nb_web.housync.R;
import pt.nb_web.housync.SignInServices.FacebookAccount;
import pt.nb_web.housync.SignInServices.GoogleAccount;
import pt.nb_web.housync.SignInServices.LogInAsyncTask;
import pt.nb_web.housync.SignInServices.SignInAccount;
import pt.nb_web.housync.SignInServices.UserLogIn;

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

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "pt.nb_web.housync",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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
            new LogInAsyncTask(this).execute((SignInAccount) googleAccount);
        }else{
            facebookAccount.onLogInActivityResult(requestCode, resultCode, data);
            new LogInAsyncTask(this).execute((SignInAccount) facebookAccount);
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
        UserLogIn userLogIn = new UserLogIn(this);

        if(userLogIn.checkIfLogedIn()){
            if((googleAccount.getGoogleSignInAccount() == null) && (facebookAccount.getAccessToken() == null)){
                userLogIn.clearUser();
            }
        }else{
            if(googleAccount.getGoogleSignInAccount() != null){
                new LogInAsyncTask(this).execute((SignInAccount) googleAccount);
            }else if(facebookAccount.getAccessToken() != null) {
                new LogInAsyncTask(this).execute((SignInAccount) facebookAccount);
            }
        }
    }

}
