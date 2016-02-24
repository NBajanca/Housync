package pt.nb_web.housync.service.sign_in;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by Nuno on 20/02/2016.
 */
public class LogInAsyncTask extends AsyncTask<SignInAccount, Void, HouSyncUser> {

    private Context context;
    private static MyApi myApiService = null;

    private SignInAccount signInAccount;

    public LogInAsyncTask (Context context){
        this.context = context;
    }

    @Override
    protected HouSyncUser doInBackground(SignInAccount... params) {
        Log.d("LogInAsync", "Entered");
        signInAccount = params[0];
        if (signInAccount == null) return null;

        UserLogIn userLogIn = new UserLogIn(context);

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        Log.d("LogInAsync", "API obtained");
        try {
            HouSyncUser houSyncUser = new HouSyncUser();
            if (signInAccount instanceof GoogleAccount){
                GoogleAccount googleAccount = (GoogleAccount) signInAccount;
                houSyncUser.setUserId(userLogIn.getUserId());
                houSyncUser.setUserName(googleAccount.getGoogleSignInAccount().getDisplayName());
                houSyncUser.setEmail(googleAccount.getGoogleSignInAccount().getEmail());
                return myApiService.signInGoogle(googleAccount.getGoogleSignInAccount().getId(), houSyncUser).execute();
            }else{
                FacebookAccount facebookAccount = (FacebookAccount) signInAccount;
                houSyncUser.setUserId(userLogIn.getUserId());
                houSyncUser.setUserName(facebookAccount.getUserName());
                houSyncUser.setEmail(facebookAccount.getEmail());
                Log.d("LogInAsync", "ID: "+ facebookAccount.getAccessToken().getUserId());
                return myApiService.signInFacebook(facebookAccount.getAccessToken().getUserId(), houSyncUser).execute();
            }
        } catch (IOException e) {
            Log.d("LogInAsync", "IOException");
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(HouSyncUser result) {
        super.onPostExecute(result);

        Log.d("LogInAsync", "Result code:" + result.getErrorCode());

        if (result != null && result.getErrorCode() == 0){
            UserLogIn userLogIn = new UserLogIn(context);
            userLogIn.setUserName(result.getUserName());
            userLogIn.setUserId(result.getUserId());
        }else{
            Toast.makeText(context, result.getUserName(), Toast.LENGTH_LONG).show();

        }
    }
}