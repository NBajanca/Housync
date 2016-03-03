package pt.nb_web.housync.service;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.data.house.HouseCursor;
import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.data.house.HouseSQLiteRepository;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.exception.UserNotFoundException;
import pt.nb_web.housync.exception.UserNotSignInException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;

import static pt.nb_web.housync.data.house.HouseSQLiteRepository.WRITE_PERMISSION;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseService {
    private static HouseService instance;
    private HouseSQLiteRepository repository;
    private String TAG = "HouseService";

    /**
     * Creates a new SQLiteRepository Object to access the DB
     *
     * @param context
     */
    private HouseService(Context context) {
        repository = new HouseSQLiteRepository(context, WRITE_PERMISSION);

    }

    /**
     * On first call the object is created, after that an instance is returned
     *
     * @param context
     * @return HouseService instance
     */
    public static synchronized HouseService getInstance(Context context) {
        if (instance == null)
            instance = new HouseService(context);
        return instance;
    }

    /**
     * Adds a new local house to the DB.
     * This method should be only used if the house was just created
     * online.
     *
     * For Houses with more information (houses downloaded from the
     * server the {@link #add(House) add} method should be used
     *
     * @param item
     * @return
     */
    public int addNew(House item){
        return repository.addNew(item);
    }

    /**
     * Adds a house previously created (downloaded from the server) to
     * the local DB.
     *
     * For houses just created locally use the {@link #addNew(House) addNew}
     * method.
     *
     * @param item
     */
    public void add(House item){
        repository.add(item);
    }

    /**
     * Deletes a House from the local DB
     * If the house is also online, it is added to the list of houses
     * to delete and the user_house table entries for this house are
     * removed.
     *
     * @param item
     */
    public void delete(House item){
        repository.delete(item.getHouseLocalId());
        if (item.getHouseId() != 0){
            repository.setDeleted(item.getHouseId());
            repository.deleteAllUsers(item.getHouseId());
        }
    }

    /**
     * Gets a house from the DB
     *
     * @param houseLocalId
     * @return
     * @throws HouseNotFoundException
     */
    public House getHouse(int houseLocalId) throws HouseNotFoundException {
        HouseCursor cursor = repository.getHouse(houseLocalId);
        if(cursor.moveToFirst()){
            return cursor.getHouse();
        }else throw new HouseNotFoundException();
    }

    public House getOnlineHouse(int houseId) throws HouseNotFoundException{
        HouseCursor cursor = repository.getOnlineHouse(houseId);
        if(cursor.moveToFirst()){
            return cursor.getHouse();
        }else throw new HouseNotFoundException();
    }

    /**
     * Gets all houses in the DB
     *
     * @return
     */
    public List<House> getAllHouses() {
        HouseCursor cursor = repository.findAll();

        if (Commons.DEBUG){
            final int count = cursor.getCount();
            Log.d(TAG, "# of Houses: " + Integer.toString(count));
        }


        final List<House> housesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            housesList.add(cursor.getHouse());
        }

        cursor.close();
        return housesList;
    }

    /**
     * Sets the lastSync date as the current one
     *
     * @param houseLocalId
     */
    public void setLastSync(int houseLocalId){
        repository.setLastSync(houseLocalId);
    }

    /**
     * Updates a house that was already local and now is also online.
     * @param house
     */
    public void createOnline (House house) {
        repository.update(house);
        Log.d(TAG, "House created online");
    }

    /**
     * Updates the name of the house in the local db.
     * If the house is also online then the update referrence is added
     * to the updated list.
     *
     * @param house
     */
    public void updateName(House house, int step) {
        repository.updateName(house);

        if (step == Commons.ONLINE_UPDATE){
            repository.updateSnapshot(house.getHouseLocalId(), house.getSnapShot());
        }else if(house.getHouseId() > 0){
            repository.insertUpdated(house.getHouseId(), HouseDBContract.HouseEntry.COLUMN_NAME_NAME);
        }
    }


    /**
     * Associates a user to a house in the local DB.
     * Adds this change to the updates list
     *
     * If the user is not in the DB, it's added
     *
     * @param houseId
     * @param user
     */
    public void insertUser(int houseId, User user){
        int userId = user.getUserId();
        try {
            getUser(userId);
        } catch (UserNotFoundException e) {
            addUser(user);
        }

        repository.insertUser(houseId, userId);
        repository.insertUserUpdated(houseId, userId, HouseDBContract.ACTION_ADDED);
    }

    public void insertUserOnline(int houseId, User user){
        int userId = user.getUserId();
        try {
            getUser(userId);
        } catch (UserNotFoundException e) {
            addUser(user);
        }

        repository.insertUser(houseId, userId);
    }



    /**
     * Deletes a user from the local DB
     * Adds this change to the update list
     *
     * @param houseId
     * @param userId
     */
    public void deleteUser(int houseId,int userId){
        repository.deleteUser(houseId, userId);
        repository.insertUserUpdated(houseId, userId, HouseDBContract.ACTION_DELETED);
    }

    public void deleteUserOnline(int houseId,int userId){
        repository.deleteUser(houseId, userId);
    }

    /**
     * Gets all the users for the house
     *
     * @param houseId
     * @return
     */
    public List<User> getUsers(int houseId){
        List<User> usersList = new ArrayList<>();
        HouseCursor cursor = repository.getUsers(houseId);

        while (cursor.moveToNext()) {
            usersList.add(cursor.getUser());
        }

        cursor.close();
        return usersList;
    }

    /**
     * Defines the house, previously deleted locally, as deleted online.
     *
     * @param houseId
     */
    public void setDeleted(int houseId){
        repository.setDeleted(houseId);
    }

    /**
     * Gets all the houses that need to be deleted online
     *
     * @return
     */
    public List<Integer> getAllDeleted(){
        List<Integer> deletedHousesId = new ArrayList<>();
        HouseCursor cursor = repository.getAllDeleted();

        while (cursor.moveToNext()) {
            deletedHousesId.add(cursor.getId());
        }

        cursor.close();
        return deletedHousesId;
    }

    /**
     * Sets a house and field as updated online.
     * Removes the house of the updated list and updates the snapshot.
     *
     * @param houseAndFieldUpdated
     * @param snapshop
     */
    public void setUpdated(Pair<House, String> houseAndFieldUpdated, String snapshop){
        repository.setUpdated(houseAndFieldUpdated.first.getHouseId(), houseAndFieldUpdated.second);
        repository.updateSnapshot(houseAndFieldUpdated.first.getHouseLocalId(), snapshop);
    }

    /**
     * Returns all houses that have been updated locally but not online.
     *
     * @return
     */
    public List<Pair<Integer, String>> getAllUpdated(){
        List<Pair<Integer, String>> updatedHouses = new ArrayList<>();
        HouseCursor cursor = repository.getAllUpdated();

        while (cursor.moveToNext()) {
            updatedHouses.add(new Pair<Integer, String>(cursor.getId(), cursor.getField()));
        }

        cursor.close();
        return updatedHouses;
    }

    /**
     * Sets the user update as done online.
     * Updates the snapshot
     *
     * @param house
     * @param userId
     */
    public void setUserUpdated(House house, int userId){
        repository.setUserUpdated(house.getHouseId(), userId);
        repository.updateSnapshotUser(house.getHouseLocalId(), house.getSnapShotUser());
    }

    /**
     * Get all users that were updated.
     * Returns only users of a determined action ("added" or "deleted")
     *
     * @param action
     * @return
     */
    public List<Pair<Integer,Integer>> getAllUsersUpdated(String action){
        List<Pair<Integer, Integer>> updatedUsers = new ArrayList<>();
        HouseCursor cursor = repository.getAllUsersUpdated();

        while (cursor.moveToNext()) {
            if (action.equals(cursor.getAction()))
                updatedUsers.add(new Pair<Integer, Integer>(cursor.getId(), cursor.getUserId()));
        }

        cursor.close();
        return updatedUsers;
    }


    public void addUser(User user){
        repository.addUser(user);
    }

    public void deleteUser(int userId){
        repository.deleteUser(userId);
    }

    public User getUser(int userId) throws UserNotFoundException {
        HouseCursor cursor = repository.getUser(userId);

        if(cursor.moveToFirst()){
            return cursor.getUser();
        }else throw new UserNotFoundException();
    }

    public List<User> getAllUsers(){
        List<User> usersList = new ArrayList<>();
        HouseCursor cursor = repository.getAllUsers();

        while (cursor.moveToNext()) {
            usersList.add(cursor.getUser());
        }

        cursor.close();
        return usersList;

    }

    public void updateHouseSnapshot(int houseLocalId, String snapshot){
        repository.updateSnapshot(houseLocalId, snapshot);
    }

    public void updateHouseSnapshotUser(int houseLocalId, String snapshotUser){
        repository.updateSnapshotUser(houseLocalId, snapshotUser);
    }
}
