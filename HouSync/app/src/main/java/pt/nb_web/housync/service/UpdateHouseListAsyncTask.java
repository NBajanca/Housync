package pt.nb_web.housync.service;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private List<House> localHouses;
    private List<House> localHousesNotOnline;
    private HouseService houseService;
    private List<House> deletedHouses;

    public UpdateHouseListAsyncTask(View view, int userId) {
        this.view = view;
        this.userId = userId;
        houseService = HouseService.getInstance(view.getContext());

    }

    @Override
    protected List<House> doInBackground(List<House>... params) {
        localHouses = params[0];
        localHousesNotOnline = new ArrayList<>(localHouses);
        List<House> onlineHousesnotLocal = new ArrayList<>();
        List<House> housesToAddOnline = new ArrayList<>();
        deletedHouses = new ArrayList<>();

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            List<HouSyncHouse> onlineHousesList = myApiService.getAllHouses(userId).execute().getItems();
            for (HouSyncHouse onlineHouse: onlineHousesList) {
                boolean exists = false;
                for (House localHouse: localHouses) {
                    if (localHouse.getHouseId() == onlineHouse.getHouseId()){
                        localHousesNotOnline.remove(localHouse);
                        exists = true;
                        break;
                    }
                }

                if (!exists){
                    onlineHouse = myApiService.getHouseData(onlineHouse.getHouseId()).execute();
                    House newHouse = House.getHouseFromHouSyncHouse(onlineHouse);
                    onlineHousesnotLocal.add(newHouse);
                }
            }
            for (House house: localHousesNotOnline){
                //House existed online
                if(house.getHouseId() != 0) {
                    houseService.delete(house);
                    deletedHouses.add(house);
                }else
                    housesToAddOnline.add(house);
            }

            for (House house: housesToAddOnline){
                int houseLocalId = house.getHouseLocalId();
                HouSyncHouse response = myApiService.createHouse(house.getHouSyncHouseFromHouse(house)).execute();
                house = House.getHouseFromHouSyncHouse(response);
                house.setHouseLocalId(houseLocalId);
                houseService.update(house);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return onlineHousesnotLocal;
    }

    @Override
    protected void onPostExecute(List<House> result) {
        super.onPostExecute(result);

        int housesAdded = 0;
        int housesDeleted = deletedHouses.size();

        for (House house: result) {
            houseService.add(house);
            housesAdded ++;
        }

        if (!isCancelled()){
            ProgressDialogHelper.show(view.getContext(), "Updating Houses...");
            HouseRecyclerAdapter houseRecyclerAdapter = (HouseRecyclerAdapter)
                    ((RecyclerView) view.findViewById(R.id.house_manager_view)).getAdapter();

            if (housesDeleted >0){

                for (House deletedHouse: deletedHouses){
                    int position = houseRecyclerAdapter.getItemPosition(deletedHouse);
                    houseRecyclerAdapter.notifyItemRemoved(position);
                }
            }

            if (housesDeleted >0 || housesAdded >0) {
                List<House> housesList = houseService.getAllItems();
                houseRecyclerAdapter.updateList(housesList);
            }

            if (housesAdded != 0) {
                houseRecyclerAdapter.notifyItemRangeInserted(localHouses.size() - housesDeleted, housesAdded);
            }

            ProgressDialogHelper.hide();
        }else{
            Log.d("UpdateHouseListAsync", "View offline");
        }

        if(housesAdded >0  && housesDeleted>0){
            Toast.makeText(view.getContext(),
                    Integer.toString(housesAdded) + " House(s) Added and " +
                    Integer.toString(housesDeleted) + " House(s) Deleted",Toast.LENGTH_LONG).show();
        }else if(housesAdded >0){
            Toast.makeText(view.getContext(),
                    Integer.toString(housesAdded) + " House(s) Added",Toast.LENGTH_LONG).show();
        }else if(housesDeleted>0){
            Toast.makeText(view.getContext(),
                    Integer.toString(housesDeleted) + " House(s) Deleted",Toast.LENGTH_LONG).show();
        }
    }
}
