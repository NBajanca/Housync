package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.NetworkHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateLocalHouseAsyncTask extends AsyncTask<House, Void, Void> {
    private static final String TAG = "UpdateLocalHouseAsync";
    private final Context context;
    private static MyApi myApiService = null;
    private HouseService houseService;

    public UpdateLocalHouseAsyncTask(Context context) {
        this.context = context;
        houseService = HouseService.getInstance(context);
    }

    @Override
    protected Void doInBackground(House... params) {
        House house = params[0];

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            updateHouse(context, myApiService, house);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    private void updateHouse(Context context, MyApi myApiService, House houseToUpdate) throws IOException{
        if (NetworkHelper.isOnline(context)) {
            UserLogIn userLoginService = UserLogIn.getInstance(context);
            if (userLoginService.checkIfLogedIn()) {
                HouSyncHouse houseUpdated = myApiService.getHouseData(houseToUpdate.getHouseId()).execute();
                if(houseUpdated.getErrorCode() == 0){
                    House updatedHouse =  House.getHouseFromHouSyncHouse(houseUpdated);
                    updateFields(houseToUpdate, updatedHouse);
                }else if(Commons.DEBUG){
                    Log.d(TAG, "Error in API request house: " + houseUpdated.getHouseName());
                }
            }
        }
    }

    // Todo: Insert change in Admin as well.
    private void updateFields(House houseToUpdate, House updatedHouse) {
        updatedHouse.setHouseLocalId(houseToUpdate.getHouseLocalId());
        if (houseToUpdate.getHouseName() != updatedHouse.getHouseName()){
            houseService.updateName(updatedHouse, Commons.ONLINE_UPDATE);
        }
    }
}
