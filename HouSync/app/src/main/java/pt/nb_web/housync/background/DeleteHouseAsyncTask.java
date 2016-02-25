package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.NetworkHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class DeleteHouseAsyncTask extends AsyncTask<House, Void, Void> {
    private final Context context;
    private static MyApi myApiService = null;
    private HouseService houseService;

    public DeleteHouseAsyncTask(Context context) {
        this.context = context;
        houseService = HouseService.getInstance(context);
    }

    @Override
    protected Void doInBackground(House... params) {
        House houseToDelete = params[0];

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            if (deleteHouse(context, myApiService, houseToDelete.getHouseId()))
                houseService.setDeleted(houseToDelete.getHouseId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    protected static boolean deleteHouse(Context context, MyApi myApiService, int houseId) throws IOException {
        if (NetworkHelper.isOnline(context)) {
            UserLogIn userLoginService = UserLogIn.getInstance(context);
            if (userLoginService.checkIfLogedIn()) {
                myApiService.deleteHouse(houseId).execute();
                return true;
            }
        }
        return false;
    }

}
