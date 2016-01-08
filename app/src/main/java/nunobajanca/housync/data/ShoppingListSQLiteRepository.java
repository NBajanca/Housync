package nunobajanca.housync.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import nunobajanca.housync.model.ShoppingListItem;

import static nunobajanca.housync.data.ShoppingListDBContract.COMMA_SEP;
import static nunobajanca.housync.data.ShoppingListDBContract.ItemEntry;
import static nunobajanca.housync.data.ShoppingListDBContract.getWritableDatabase;

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
                        ItemEntry.COLUMN_NAME_ITEM_NAME + COMMA_SEP +
                        ItemEntry.COLUMN_NAME_ITEM_CHECKED + ") " +
                        "VALUES(?" + COMMA_SEP + "?)",
                new Object[]{item.getName(), 0});
    }

    public void delete(ShoppingListItem item) {
        db.execSQL("DELETE FROM " + ItemEntry.TABLE_NAME + " WHERE "
                        + ItemEntry.COLUMN_NAME_ITEM_NAME + " = ?",
                new Object[]{item.getName()});
    }

    public void updateChecked(ShoppingListItem item) {
        db.execSQL("UPDATE " +
                        ItemEntry.TABLE_NAME + " SET " +
                        ItemEntry.COLUMN_NAME_ITEM_CHECKED + " = ?" +
                        " WHERE " +
                        ItemEntry.COLUMN_NAME_ITEM_NAME + " = ?",
                new Object[]{(item.getChecked()) ? 1 : 0, item.getName()});
    }

    public ShoppingListCursor findAll() {
        return new ShoppingListCursor(
                db.rawQuery("SELECT * FROM " + ItemEntry.TABLE_NAME,
                        null));
    }
}
