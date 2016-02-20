package pt.nb_web.housync.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Nuno on 20/02/2016.
 */
public class LogInAsyncTask extends AsyncTask<Object, Void, GoogleAccount> {

    private Context context;

    public LogInAsyncTask (Context context){
        this.context = context;
    }

    @Override
    protected GoogleAccount doInBackground(Object... params) {
        GoogleAccount googleAccount = (GoogleAccount) params[0];
        googleAccount.onStart();
        return googleAccount;
    }

    @Override
    protected void onPostExecute(GoogleAccount result) {
        super.onPostExecute(result);

        if (result.getGoogleSignInAccount() != null) {
            Toast toast = Toast.makeText(context, result.getGoogleSignInAccount().getDisplayName() + " is loged in", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}