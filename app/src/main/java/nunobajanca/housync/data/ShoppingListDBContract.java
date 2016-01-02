package nunobajanca.housync.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Nuno on 26/12/2015.
 */
public class ShoppingListDBContract {

    public static final String DATABASE_NAME = "Housync.db";
    public static final int DATABASE_VERSION = 1;

    public static final String VAR_CHAR_TYPE = " varchar";
    public static final String TEXT_TYPE = " text";

    public static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_MOVIE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ItemEntry.TABLE_NAME+" ("+
                    ItemEntry.COLUMN_NAME_ITEM_ID+VAR_CHAR_TYPE
                    +" PRIMARY KEY"+COMMA_SEP+
                    ItemEntry.COLUMN_NAME_ITEM_NAME+TEXT_TYPE+" )";


    public static SQLiteDatabase getWritableDatabase(Context context){
        return new ShoppingListDBHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context){
        return new ShoppingListDBHelper(context).getReadableDatabase();
    }

    public static abstract class ItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "shopping_list";
        public static final String COLUMN_NAME_ITEM_ID = "item_id";
        public static final String COLUMN_NAME_ITEM_NAME = "item_name";

    }

    private static class ShoppingListDBHelper extends SQLiteOpenHelper {


        public ShoppingListDBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_MOVIE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
