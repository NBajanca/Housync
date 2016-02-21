package pt.nb_web.housync.service.sign_in;

import android.content.Context;
import android.os.AsyncTask;

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

        signInAccount = params[0];
        if (signInAccount == null) return null;

        UserLogIn userLogIn = new UserLogIn(context);

            if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

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
                //return myApiService.signInFacebook(facebookAccount.getAccessToken().getUserId(), userLogIn.getUserId()).execute();
            }
        } catch (IOException e) {
            e.getMessage();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(HouSyncUser result) {
        super.onPostExecute(result);

        if (result != null && result.getErrorCode() == 0){
            UserLogIn userLogIn = new UserLogIn(context);
            userLogIn.setUserName(result.getUserName());
            userLogIn.setUserId(result.getUserId());
        }
    }
}