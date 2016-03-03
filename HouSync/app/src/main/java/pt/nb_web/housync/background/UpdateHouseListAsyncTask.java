package pt.nb_web.housync.background;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
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
import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.ProgressDialogHelper;

/**
 * Created by Nuno on 23/02/2016.
 */
public class UpdateHouseListAsyncTask extends AsyncTask<List<House>, Void, Void> {

    private static final String TAG = "UpdateHouseListAsync";
    private final int userId;
    private final View view;
    private final Context appContext;

    private static MyApi myApiService = null;
    private HouseService houseService;

    private List<House> localHouses;
    private ArrayList<House> localHousesNotOnline;
    private ArrayList<House> deletedHouses;
    private ArrayList<House> housesToUpdateOnline;
    private ArrayList<Integer> housesToUpdateUsersOnline;
    private ArrayList<House> housesToAddOnline;

    private ArrayList<Pair<Integer, String>> housesFieldsToUpdateOnline;
    private ArrayList<Pair<Integer, Integer>> houseUsersToAddOnline;
    private ArrayList<Pair<Integer, Integer>> houseUsersToRemoveOnline;

    private List<Integer> housesDeletedIds;
    int housesAdded = 0;


    public UpdateHouseListAsyncTask(View view, int userId) {
        this.view = view;
        this.appContext = view.getContext().getApplicationContext();
        this.userId = userId;
        houseService = HouseService.getInstance(view.getContext());
        housesToAddOnline = new ArrayList<>();
        deletedHouses = new ArrayList<>();
        housesToUpdateOnline = new ArrayList<>();
        housesToUpdateUsersOnline = new ArrayList<>();
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
                    houseService.setLastSync(localHouse.getHouseLocalId());
                }else {
                    addHouseLocally(onlineHouse);
                    housesToUpdateUsersOnline.add(onlineHouse.getHouseId());
                }

            }
            processLocalHousesNotOnline();
            processLocalUpdates();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);


        int housesDeleted = deletedHouses.size();

        if (!isCancelled()){
            updateHouseUI();
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

        for (Pair<Integer, String> houseAndFieldToUpdate: housesFieldsToUpdateOnline){
            new UpdateOnlineHouseAsyncTask(appContext).execute(houseAndFieldToUpdate);
        }

        for (House house: housesToUpdateOnline){
            new UpdateLocalHouseAsyncTask(appContext).execute(house);
        }

        for (Pair<Integer, Integer> houseAndUserToAdd: houseUsersToAddOnline) {
            String houseId = Integer.toString(houseAndUserToAdd.first);
            String userId = Integer.toString(houseAndUserToAdd.second);
            String action = HouseDBContract.ACTION_ADDED;
            new UpdateOnlineUserHouseAsyncTask(appContext).execute(houseId, userId, action);
        }

        for (Pair<Integer, Integer> houseAndUserToRemove: houseUsersToRemoveOnline) {
            String houseId = Integer.toString(houseAndUserToRemove.first);
            String userId = Integer.toString(houseAndUserToRemove.second);
            String action = HouseDBContract.ACTION_DELETED;
            new UpdateOnlineUserHouseAsyncTask(appContext).execute(houseId, userId, action);
        }

        for (int houseId :housesToUpdateUsersOnline){
            new UpdateLocalUserHouseAsyncTask(appContext).execute(houseId);
        }
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
        if (!onlineHouse.getSnapShotUser().equals(localHouse.getSnapShotUser())){
            if(Commons.DEBUG)
                Log.d(TAG, "House " + Integer.toString(localHouse.getHouseId()) + " added to housesToUpdateUsersOnline");
            housesToUpdateUsersOnline.add(localHouse.getHouseId());
        }
    }

    private void checkIfHouseIsUpdated(HouSyncHouse onlineHouse, House localHouse) {
        if (!onlineHouse.getSnapShot().equals(localHouse.getSnapShot())){
            if(Commons.DEBUG)
                Log.d(TAG, "House " + Integer.toString(localHouse.getHouseId()) + " added to housesToUpdateOnline");
            housesToUpdateOnline.add(localHouse);
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

    private void processLocalUpdates() {
        housesFieldsToUpdateOnline = new ArrayList<>(houseService.getAllUpdated());
        houseUsersToAddOnline = new ArrayList<>(houseService.getAllUsersUpdated(HouseDBContract.ACTION_ADDED));
        houseUsersToRemoveOnline = new ArrayList<>(houseService.getAllUsersUpdated(HouseDBContract.ACTION_DELETED));
        if(Commons.DEBUG){
            Log.d(TAG, "# Fields to update: " + Integer.toString(housesFieldsToUpdateOnline.size())
                    +" # Users to add: " + Integer.toString(houseUsersToAddOnline.size())
                    +" # Users to remove: " + Integer.toString(houseUsersToRemoveOnline.size()));
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

    private void updateHouseUI() {
        ProgressDialogHelper.show(view.getContext(), "Updating Houses...");
        HouseRecyclerAdapter houseRecyclerAdapter = (HouseRecyclerAdapter)
                ((RecyclerView) view.findViewById(R.id.house_manager_view)).getAdapter();

        for (House deletedHouse: deletedHouses){
            houseRecyclerAdapter.removeItem(deletedHouse);
        }

        List<House> updatedList = houseService.getAllHouses();
        houseRecyclerAdapter.updateList(updatedList);
        houseRecyclerAdapter.notifyDataSetChanged();

        ProgressDialogHelper.hide();
    }


}
