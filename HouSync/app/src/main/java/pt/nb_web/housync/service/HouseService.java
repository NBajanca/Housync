package pt.nb_web.housync.service;

import android.content.Context;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.data.house.HouseCursor;
import pt.nb_web.housync.data.house.HouseSQLiteRepository;
import pt.nb_web.housync.model.House;

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

        fillWithDefault(context);
    }

    public static synchronized HouseService getInstance(Context context) {
        if (instance == null)
            instance = new HouseService(context);
        return instance;
    }

    public List<House> getAllItems() {

        HouseCursor cursor = repository.findAll();
        final int count = cursor.getCount();

        final List<House> housesList = new ArrayList<House>();

        while (cursor.moveToNext()) {
            int itemLocalId = cursor.getLocalId();
            String itemName = cursor.getName();

            House item = new House(itemLocalId, itemName);
            housesList.add(item);
            if (housesList.size() == count)
                break;
        }

        cursor.close();
        return housesList;
    }

    private void fillWithDefault(Context context){
        if(repository.findAll().getCount() == 0){
            TypedArray ItemsNames = context.getResources()
                    .obtainTypedArray(R.array.default_houses_list_names);

            for(int i = 0; i < ItemsNames.length(); i++){
                repository.addNew(
                        new House(ItemsNames.getString(i), 0));
            }
        }
    }

    public House getHouse(House item){
        HouseCursor cursor = repository.getHouse(item);
        if(cursor.moveToFirst()){
            return cursor.getHouse();
        }else{
            item.setErrorCode(1);
            return item;
        }
    }

    public void add(House item){ repository.addNew(item);}

    public void delete(House item){ repository.delete(item);}

}
