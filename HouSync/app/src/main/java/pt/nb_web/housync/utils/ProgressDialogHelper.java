package pt.nb_web.housync.utils;

import android.content.Context;

/**
 * Created by Nuno on 22/02/2016.
 */
public class ProgressDialogHelper {

    public static android.app.ProgressDialog progressDialog;

    public static void show(Context context, String title, String message) {
        newProgressDialog(context);

        progressDialog.setTitle(title);
        show(context, message);
    }

    public static void show(Context context, String message) {
        newProgressDialog(context);

        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void hide() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    private static void newProgressDialog(Context context){
        if (progressDialog == null) {
            progressDialog = new android.app.ProgressDialog(context);
            progressDialog.setIndeterminate(true);
        }
    }
}
