package nunobajanca.housync.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import nunobajanca.housync.model.ShoppingListItem;

import static nunobajanca.housync.data.ShoppingListDBContract.getWritableDatabase;
import static nunobajanca.housync.data.ShoppingListDBContract.ItemEntry;
import static nunobajanca.housync.data.ShoppingListDBContract.COMMA_SEP;

/**
 * Created by Nuno on 26/12/2015.
 */
public class ShoppingListSQLiteRepository {

    private SQLiteDatabase db;

    public ShoppingListSQLiteRepository(Context context) {
        db = getWritableDatabase(context);
    }

    public void add(ShoppingListItem item) {
        db.execSQL("INSERT OR REPLACE INTO " +
                        ItemEntry.TABLE_NAME + "(" +
                        ItemEntry.COLUMN_NAME_ITEM_ID + COMMA_SEP +
                        ItemEntry.COLUMN_NAME_ITEM_NAME + ") " +
                        "VALUES(?" + COMMA_SEP + "?)",
                new Object[]{item.getId(), item.getName()});
    }

    public void delete(ShoppingListItem item) {
        db.execSQL("DELETE FROM " + ItemEntry.TABLE_NAME + " WHERE "
                        + ItemEntry.COLUMN_NAME_ITEM_ID + " = ?",
                new Object[]{item.getId()});
    }

    public ShoppingListCursor findAll() {
        return new ShoppingListCursor(
                db.rawQuery("SELECT * FROM " + ItemEntry.TABLE_NAME,
                        null));
    }
}
