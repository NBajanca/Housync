package pt.nb_web.housync.background;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import pt.nb_web.housync.R;
import pt.nb_web.housync.activities.EditHouseActivity;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.adapter.UserHouseRecyclerAdapter;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.exception.UserNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.NetworkHelper;
import pt.nb_web.housync.utils.ProgressDialogHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class AddUserHouseAsyncTask extends AsyncTask<Pair<Integer, Integer>, Void, House> {
    private final Context context;
    private final EditHouseActivity activityInstance;
    private static MyApi myApiService = null;
    private HouseService houseService = null;
    private User localUser;

    public AddUserHouseAsyncTask(EditHouseActivity activityInstance) {
        this.context = activityInstance.getBaseContext();
        this.activityInstance = activityInstance;
        houseService = HouseService.getInstance(context);
    }

    @Override
    protected House doInBackground(Pair<Integer, Integer>... params) {
        int houseId= params[0].first;
        int userId = params[0].second;
        House response = null;

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            response = addUserHouse(context, myApiService, houseId, userId);
        } catch (IOException e) {
            e.printStackTrace();
            response = new House (Commons.ERROR_IN_API);
        } catch (HouseNotFoundException e) {
            e.printStackTrace();
            response = new House (Commons.HOUSE_NOT_FOUND);
        }
        return response;
    }

    private House addUserHouse(Context context, MyApi myApiService, int houseId, int userId) throws IOException, HouseNotFoundException {
        House houseUpdated = new House();
        if (NetworkHelper.isOnline(context)) {
            UserLogIn userLoginService = UserLogIn.getInstance(context);
            if (userLoginService.checkIfLogedIn()) {
                if (getUser(userId)){
                    HouSyncHouse response = myApiService.addUserInHouse(houseId, userId).execute();
                    if (response.getHouseId() > 0){
                        houseUpdated = House.getHouseFromHouSyncHouse(response);
                        int houseLocalId = houseService.getOnlineHouse(houseId).getHouseLocalId();
                        houseService.updateHouseSnapshotUser(houseLocalId, houseUpdated.getSnapShotUser());
                        houseUpdated.setHouseLocalId(houseLocalId);
                    }else houseUpdated.setHouseId(Commons.ERROR_IN_API);
                }else houseUpdated.setHouseId(Commons.USER_NOT_FOUND);
            }else houseUpdated.setHouseId(Commons.NOT_SIGNED_IN);
        }else houseUpdated.setHouseId(Commons.NO_INTERNET);

        return houseUpdated;
    }



    private boolean getUser(int userId) throws IOException {
        HouSyncUser responseUser = myApiService.getUser(userId).execute();
        if (responseUser.getUserId() <= 0) return false;
        User user = User.getUserFromHouSyncUser(responseUser);

        try {
            localUser = houseService.getUser(userId);
            if (localUser.getSnapshot() != responseUser.getSnapshot()){
                //Todo: Add call to update user
            }
        } catch (UserNotFoundException e) {
            houseService.addUser(user);
            localUser = user;
        }
        return true;
    }

    @Override
    protected void onPostExecute(House result)    {
        super.onPostExecute(result);

        if(!isCancelled()){
            if(result.getHouseId() <= 0){
                activityInstance.setAddUserError(result.getHouseId());
            }else{
                houseService.insertUser(result.getHouseId(), localUser);
                houseService.setUserUpdated(result, localUser.getUserId());
                activityInstance.usersAdded.add(localUser.getUserId());

                UserHouseRecyclerAdapter userHouseRecyclerAdapter = (UserHouseRecyclerAdapter)
                        ((RecyclerView) activityInstance.findViewById(R.id.users_edit_house_view)).getAdapter();
                userHouseRecyclerAdapter.addItem(localUser);
            }
        }
        ProgressDialogHelper.hide();
    }

}
