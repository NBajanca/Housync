package pt.nb_web.housync.service.sign_in;

import android.content.Intent;

/**
 * Created by Nuno on 20/02/2016.
 */
public interface SignInAccount {

    public void onStart();
    public void onLogInActivityResult(int requestCode, int resultCode, Intent data);

}
