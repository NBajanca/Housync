package nunobajanca.housync.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by Nuno on 26/12/2015.
 */
public class ShoppingListCursor extends CursorWrapper {

    public ShoppingListCursor(Cursor cursor) {
        super(cursor);
    }

    public String getId(){
        return getString(
                getColumnIndex(ShoppingListDBContract.
                        ItemEntry.COLUMN_NAME_ITEM_ID));
    }

    public String getName(){
        return getString(
                getColumnIndex(ShoppingListDBContract.
                        ItemEntry.COLUMN_NAME_ITEM_NAME));
    }
}
