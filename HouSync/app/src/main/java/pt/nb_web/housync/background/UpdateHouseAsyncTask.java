package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.NetworkHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateHouseAsyncTask extends AsyncTask<Pair<House, String>, Void, Void> {
    private final Context context;
    private static MyApi myApiService = null;
    private HouseService houseService;

    public UpdateHouseAsyncTask(Context context) {
        this.context = context;
        houseService = HouseService.getInstance(context);
    }

    @Override
    protected Void doInBackground(Pair<House, String>... params) {
        Pair houseAndFieldToUpdate = params[0];
        House houseToUpdate = params[0].first;
        String fieldToUpdate = params[0].second;

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            String snapshot = updateHouse(context, myApiService, houseAndFieldToUpdate);
            if (snapshot != null)
                houseService.setUpdated(houseAndFieldToUpdate, snapshot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    //// TODO: 26/02/2016 Implement invalid response
    private String updateHouse(Context context, MyApi myApiService, Pair<House, String> houseAndFieldToUpdate) throws IOException{
        if (NetworkHelper.isOnline(context)) {
            UserLogIn userLoginService = UserLogIn.getInstance(context);
            if (userLoginService.checkIfLogedIn()) {
                House houseToUpdate = houseAndFieldToUpdate.first;
                String fieldToUpdate = houseAndFieldToUpdate.second;
                String newValue = null;

                switch (fieldToUpdate){
                    case(HouseDBContract.HouseEntry.COLUMN_NAME_NAME):
                        newValue = houseToUpdate.getHouseName();
                        break;
                }

                if(fieldToUpdate != null) {
                    HouSyncHouse houseUpdated = myApiService.updateHouseData(houseToUpdate.getHouseId()
                            , fieldToUpdate, newValue).execute();
                    if(houseUpdated.getErrorCode() == 0)
                        return houseUpdated.getSnapShot();
                }

            }
        }
        return null;
    }
}
