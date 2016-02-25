package pt.nb_web.housync.service;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.data.house.HouseCursor;
import pt.nb_web.housync.data.house.HouseSQLiteRepository;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.utils.Commons;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseService {
    //private static final String baseUrl = "http://housync.appspot.com";

    private static HouseService instance;
    private HouseSQLiteRepository repository;
    //private ShoppingListRestInterface restInterface;

    private HouseService(Context context) {

        repository = new HouseSQLiteRepository(context);
    }

    public static synchronized HouseService getInstance(Context context) {
        if (instance == null)
            instance = new HouseService(context);
        return instance;
    }

    public List<House> getAllItems() {

        HouseCursor cursor = repository.findAll();
        final int count = cursor.getCount();
        Log.d("HouseService", "# of Houses: " + Integer.toString(count));

        final List<House> housesList = new ArrayList<House>();

        while (cursor.moveToNext()) {
            housesList.add(cursor.getHouse());

            if (housesList.size() == count)
                break;
        }

        cursor.close();
        return housesList;
    }

    public House getHouse(int houseLocalID) throws HouseNotFoundException {
        HouseCursor cursor = repository.getHouse(houseLocalID);
        if(cursor.moveToFirst()){
            return cursor.getHouse();
        }else throw new HouseNotFoundException();
    }

    public int addNew(House item){
        return repository.addNew(item);
    }

    public void add(House item){ repository.add(item);}

    public void delete(House item){ repository.delete(item);}

    public void setDeleted(int houseID){
        repository.setDeleted(houseID);
    }

    public List<Integer> getAllDeleted(){
        List<Integer> deletedHousesId = new ArrayList<>();
        HouseCursor cursor = repository.getAllDeleted();

        while (cursor.moveToNext()) {
            deletedHousesId.add(cursor.getId());
        }

        cursor.close();
        return deletedHousesId;
    }

    public void createOnline (House house) {
        repository.update(house);
        insertUser(house.getHouseLocalId(), house.getAdminId());
    }

    public void update(House house) {
        repository.update(house);
    }

    public void insertUser(int houseID, int userID){
        repository.insertUser(houseID, userID);
    }
}
