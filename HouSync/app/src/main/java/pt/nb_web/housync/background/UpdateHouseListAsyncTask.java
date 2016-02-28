package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nuno.myapplication.housync_backend.myApi.MyApi;
import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.ProgressDialogHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateHouseListAsyncTask extends AsyncTask<List<House>, Void, Void> {

    private final int userId;
    private final View view;
    private final Context appContext;

    private static MyApi myApiService = null;
    private HouseService houseService;

    private List<House> localHouses;
    private List<House> localHousesNotOnline;
    private List<House> onlineHousesNotLocal;
    private List<House> deletedHouses;
    private List<House> housesToUpdate;
    private List<House> housesToUpdateUsers;
    private List<House> housesToAddOnline;

    private List<Integer> housesDeletedIds;
    int housesAdded = 0;



    public UpdateHouseListAsyncTask(View view, int userId) {
        this.view = view;
        this.appContext = view.getContext().getApplicationContext();
        this.userId = userId;
        houseService = HouseService.getInstance(view.getContext());

        onlineHousesNotLocal = new ArrayList<>();
        housesToAddOnline = new ArrayList<>();
        deletedHouses = new ArrayList<>();
        housesToUpdate = new ArrayList<>();
        housesToUpdateUsers = new ArrayList<>();
        housesDeletedIds = houseService.getAllDeleted();

    }

    @Override
    protected Void doInBackground(List<House>... params) {
        localHouses = params[0];
        localHousesNotOnline = new ArrayList<>(localHouses);

        setAdminId(localHouses);

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://housync-android.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            List<HouSyncHouse> onlineHousesList = myApiService.getAllHouses(userId).execute().getItems();
            for (HouSyncHouse onlineHouse: onlineHousesList) {
                House localHouse = getHouseLocally(onlineHouse);
                if (localHouse != null){
                    checkIfHouseIsUpdated(onlineHouse, localHouse);
                    checkIfHouseUsersAreUpdated(onlineHouse, localHouse);
                }else {
                    addHouseLocally(onlineHouse);
                }

            }
            processLocalHousesNotOnline();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private House getHouseLocally(HouSyncHouse onlineHouse) {
        for (House localHouse: localHouses) {
            if (localHouse.getHouseId() == onlineHouse.getHouseId()){
                localHousesNotOnline.remove(localHouse);
                return localHouse;
            }
        }
        return null;
    }

    private void processLocalHousesNotOnline() {
        for (House house: localHousesNotOnline){
            //House existed online
            if(house.getHouseId() != 0) {
                houseService.delete(house);
                deletedHouses.add(house);
            }else
                housesToAddOnline.add(house);
        }
    }

    private void addHouseLocally(HouSyncHouse onlineHouse) throws IOException {
        onlineHouse = myApiService.getHouseData(onlineHouse.getHouseId()).execute();
        House newHouse = House.getHouseFromHouSyncHouse(onlineHouse);

        if (housesDeletedIds.size() != 0){
            for (int id :housesDeletedIds) {
                if (newHouse.getHouseId() == id){
                    return;
                }
            }
        }
        houseService.add(newHouse);
        housesAdded ++;
    }



    private void checkIfHouseUsersAreUpdated(HouSyncHouse onlineHouse, House localHouse) {
        if (onlineHouse.getSnapShotUser() != localHouse.getSnapShotUser()){
            housesToUpdateUsers.add(localHouse);
        }
    }

    private void checkIfHouseIsUpdated(HouSyncHouse onlineHouse, House localHouse) {
        if (onlineHouse.getSnapShot() != localHouse.getSnapShot()){
            housesToUpdate.add(localHouse);
        }
    }

    private void setAdminId(List<House> localHouses) {

        for (House house: localHouses) {
            if(house.getAdminId() == 0 ||
                    (house.getHouseId() == 0 && house.getAdminId() != userId)){
                house.setAdminId(userId);
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);


        int housesDeleted = deletedHouses.size();

        if (!isCancelled()){
            updateHouseUI(housesDeleted);
            presentChanges(housesDeleted);
        }else{
            Log.d("UpdateHouseListAsync", "View offline");
        }

        for (House house: housesToAddOnline) {
            new AddHouseAsyncTask(appContext).execute(house);
        }

        for (int id: housesDeletedIds){
            new DeleteHouseAsyncTask(appContext).execute(id);
        }


    }

    private void presentChanges(int housesDeleted) {
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

    private void updateHouseUI(int housesDeleted) {
        ProgressDialogHelper.show(view.getContext(), "Updating Houses...");
        HouseRecyclerAdapter houseRecyclerAdapter = (HouseRecyclerAdapter)
                ((RecyclerView) view.findViewById(R.id.house_manager_view)).getAdapter();

        for (House deletedHouse: deletedHouses){
            houseRecyclerAdapter.removeItem(deletedHouse);
        }

        if (housesAdded != 0) {
            List<House> housesList = houseService.getAllItems();
            houseRecyclerAdapter.updateList(housesList);
            houseRecyclerAdapter.notifyItemRangeInserted(localHouses.size() - housesDeleted, housesAdded);
        }

        ProgressDialogHelper.hide();
    }

}
