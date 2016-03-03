package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.exception.UserNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.NetworkHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateOnlineUserHouseAsyncTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "UpdateoUserHouseAsync";
    private static MyApi myApiService = null;
    private HouseService houseService = null;

    public UpdateOnlineUserHouseAsyncTask(Context context) {
        houseService = HouseService.getInstance(context);
    }

    @Override
    protected Void doInBackground(String... params) {
        int houseId= Integer.parseInt(params[0]);
        int userId = Integer.parseInt(params[1]);
        String action = params[2];
        HouSyncHouse response;

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            if (action.equals(HouseDBContract.ACTION_ADDED))
                response = myApiService.addUserInHouse(houseId, userId).execute();
            else
                response = myApiService.removeUserFromHouse(houseId, userId).execute();

            if (response.getHouseId() > 0){
                House house = House.getHouseFromHouSyncHouse(response);
                int houseLocalId = houseService.getOnlineHouse(houseId).getHouseLocalId();
                house.setHouseLocalId(houseLocalId);
                houseService.setUserUpdated(house, userId);
            }else if (Commons.DEBUG)
                Log.d(TAG, "Error in API: " + response.getHouseName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HouseNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result)    {
        super.onPostExecute(result);
    }

}
