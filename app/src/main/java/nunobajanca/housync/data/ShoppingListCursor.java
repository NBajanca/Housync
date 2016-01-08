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

    public String getName(){
        return getString(
                getColumnIndex(ShoppingListDBContract.
                        ItemEntry.COLUMN_NAME_ITEM_NAME));
    }

    public int getChecked(){
        return getInt(
                getColumnIndex(ShoppingListDBContract.
                        ItemEntry.COLUMN_NAME_ITEM_CHECKED));
    }
}
