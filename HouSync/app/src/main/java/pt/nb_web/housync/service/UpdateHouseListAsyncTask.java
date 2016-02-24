package pt.nb_web.housync.service;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouseCollection;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.fragments.HouseManagerActivityFragment;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.sign_in.SignInAccount;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.ProgressDialogHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateHouseListAsyncTask  extends AsyncTask<List<House>, Void, List<House>> {
    private final int userId;
    private final View view;
    private static MyApi myApiService = null;
    private List<House> localHouseList;

    public UpdateHouseListAsyncTask(View view, int userId) {
        this.view = view;
        this.userId = userId;
    }

    @Override
    protected List<House> doInBackground(List<House>... params) {
        localHouseList = params[0];
        List<House> differences = new ArrayList<House>();

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            List<HouSyncHouse> onlineHousesList = myApiService.getAllHouses(userId).execute().getItems();
            for (HouSyncHouse onlineHouse: onlineHousesList) {
                boolean exists = false;
                for (House localHouse: localHouseList) {
                    if (localHouse.getHouseId() == onlineHouse.getHouseId()){
                        exists = true;
                        break;
                    }
                }

                if (!exists){
                    House newHouse = House.getHouseFromHouSyncHouse(onlineHouse);
                    differences.add(newHouse);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return differences;
    }

    @Override
    protected void onPostExecute(List<House> result) {
        super.onPostExecute(result);
        HouseService houseService = HouseService.getInstance(view.getContext());
        HouseRecyclerAdapter houseRecyclerAdapter = (HouseRecyclerAdapter)
                ((RecyclerView) view.findViewById(R.id.house_manager_view)).getAdapter();

        int housesAdded = 0;

        for (House house: result) {
            houseService.add(house);
            housesAdded ++;
        }
        if (housesAdded != 0) {
            List<House> housesList = houseService.getAllItems();
            houseRecyclerAdapter.updateList(housesList);
            houseRecyclerAdapter.notifyItemRangeInserted(localHouseList.size(), housesAdded);
            Toast.makeText(view.getContext(), "Ended Sync: " + Integer.toString(housesAdded) + " Houses Added",Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(view.getContext(), "Ended Sync",Toast.LENGTH_SHORT).show();

        ProgressDialogHelper.hide();
    }
}
