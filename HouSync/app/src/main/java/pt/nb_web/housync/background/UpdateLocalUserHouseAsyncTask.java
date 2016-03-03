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
import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.exception.UserNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.SignInAccount;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateLocalUserHouseAsyncTask extends AsyncTask<Integer, Void, Void> {
    private static final String TAG = "UpdateLUserHouseAsync";
    private static MyApi myApiService = null;
    private HouseService houseService;
    private UserLogIn userLogIn;

    public UpdateLocalUserHouseAsyncTask(Context context) {
        houseService = HouseService.getInstance(context);
        userLogIn = UserLogIn.getInstance(context);
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int houseId= params[0];
        List<Integer> usersToAdd = new ArrayList<>();

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            List<HouSyncUser> usersOnlineList = new ArrayList<>(myApiService.getHouseUsers(houseId).execute().getItems());
            List<User> usersLocalList = new ArrayList<>(houseService.getUsers(houseId));

            OUTER_LOOP:
            for (HouSyncUser userOnline: usersOnlineList) {
                if(userOnline.getUserId() == userLogIn.getUserId())
                    continue;
                for (User userLocal: usersLocalList) {
                    if(userOnline.getUserId() == userLocal.getUserId()) {
                        usersLocalList.remove(userLocal);
                        continue OUTER_LOOP;
                    }
                }
                usersToAdd.add(userOnline.getUserId());
            }

            for (int userToAdd: usersToAdd) {
                User user = getUser(userToAdd);
                if (user != null)
                    houseService.insertUserOnline(houseId, user);

            }

            for (User userToRemove : usersLocalList) {
                houseService.deleteUserOnline(houseId, userToRemove.getUserId());
            }
            HouSyncHouse houseOnline = myApiService.getHouseData(houseId).execute();
            House houseLocal = houseService.getOnlineHouse(houseId);
            houseService.updateHouseSnapshotUser(houseLocal.getHouseLocalId(), houseOnline.getSnapShotUser());
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

    private User getUser(int userId) throws IOException {
        User user;
        try {
            user = houseService.getUser(userId);
        } catch (UserNotFoundException e) {
            HouSyncUser responseUser = myApiService.getUser(userId).execute();
            if (responseUser.getUserId() <= 0){
                if (Commons.DEBUG)
                    Log.d(TAG, "Error in API: " + responseUser.getUserName());
                return null;
            }
            user = User.getUserFromHouSyncUser(responseUser);
        }
        return user;
    }

}
