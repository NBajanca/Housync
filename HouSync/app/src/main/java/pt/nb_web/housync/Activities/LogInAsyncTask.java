package pt.nb_web.housync.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.nb_web.housync.R;

/**
 * Created by Nuno on 20/02/2016.
 */
public class LogInAsyncTask extends AsyncTask<Object, Void, GoogleAccount> {

    private Context context;
    private View headerView;

    public LogInAsyncTask (Context context){
        this.context = context;
    }


    @Override
    protected GoogleAccount doInBackground(Object... params) {
        GoogleAccount googleAccount = (GoogleAccount) params[0];
        googleAccount.onStart();

        headerView = (View) params [1];

        return googleAccount;
    }

    @Override
    protected void onPostExecute(GoogleAccount result) {
        super.onPostExecute(result);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.user_name);

        if (result.getGoogleSignInAccount() != null) {
            userNameTextView.setText(result.getGoogleSignInAccount().getDisplayName());
        }
    }
}