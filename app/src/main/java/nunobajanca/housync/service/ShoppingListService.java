package nunobajanca.housync.service;

import android.content.Context;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

import nunobajanca.housync.R;
import nunobajanca.housync.data.ShoppingListCursor;
import nunobajanca.housync.data.ShoppingListSQLiteRepository;
import nunobajanca.housync.model.ShoppingListItem;

/**
 * Created by Nuno on 15/12/2015.
 */
public class ShoppingListService {

    //private static final String baseUrl = "http://housync.appspot.com";

    private static ShoppingListService instance;
    private ShoppingListSQLiteRepository repository;
    //private ShoppingListRestInterface restInterface;

    private ShoppingListService(Context context) {

        repository = new ShoppingListSQLiteRepository(context);

        fillWithDefault(context);
    }

    public static synchronized ShoppingListService getInstance(Context context) {
        if (instance == null)
            instance = new ShoppingListService(context);
        return instance;
    }

    public List<ShoppingListItem> getAllItems() {

        ShoppingListCursor cursor = repository.findAll();
        final int count = cursor.getCount();

        final List<ShoppingListItem> ShoppingList = new ArrayList<ShoppingListItem>();

        while (cursor.moveToNext()) {

            String ItemName = cursor.getName();
            Boolean ItemChecked = (cursor.getChecked() == 1)? true : false ;

            ShoppingListItem item = new ShoppingListItem(ItemName, ItemChecked);
            ShoppingList.add(item);
            if (ShoppingList.size() == count)
                break;
        }

        cursor.close();
        return ShoppingList;
    }

    private void fillWithDefault(Context context){
        if(repository.findAll().getCount() == 0){
            TypedArray ItemsNames = context.getResources()
                    .obtainTypedArray(R.array.default_shopping_list_items_names);

            for(int i = 0; i < ItemsNames.length(); i++){
                repository.add(
                        new ShoppingListItem(ItemsNames.getString(i)));
            }
        }
    }

    public void add(ShoppingListItem item){ repository.add(item);}

    public void delete(ShoppingListItem item){ repository.delete(item);}

    public void updateChecked(ShoppingListItem item) { repository.updateChecked(item);}
}
