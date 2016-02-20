package pt.nb_web.housync.SignInServices;

import android.content.Intent;

import pt.nb_web.housync.Activities.LogInActivity;

/**
 * Created by Nuno on 20/02/2016.
 */
public interface SignInAccount {

    public void onStart();
    public void onLogInActivityResult(int requestCode, int resultCode, Intent data);

}
