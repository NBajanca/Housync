package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
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
public class AddHouseAsyncTask extends AsyncTask<House, Void, Void> {
    private final Context context;
    private static MyApi myApiService = null;

    public AddHouseAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(House... params) {
        House houseToAdd = params[0];

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            createHouse(context, myApiService, houseToAdd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result)    {
        super.onPostExecute(result);
    }

    protected static boolean createHouse(Context context, MyApi myApiService, House houseToAdd) throws IOException {
        if (NetworkHelper.isOnline(context)) {
            UserLogIn userLoginService = UserLogIn.getInstance(context);
            if (userLoginService.checkIfLogedIn()) {
                int houseLocalId = houseToAdd.getHouseLocalId();
                HouseService houseService = HouseService.getInstance(context);
                HouSyncHouse response = myApiService.createHouse(houseToAdd.getHouSyncHouseFromHouse(houseToAdd)).execute();
                houseToAdd = House.getHouseFromHouSyncHouse(response);
                houseToAdd.setHouseLocalId(houseLocalId);
                houseService.createOnline(houseToAdd, context);
                return true;
            }
        }
        return false;
    }

}
