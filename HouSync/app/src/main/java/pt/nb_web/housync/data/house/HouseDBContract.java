package pt.nb_web.housync.data.house;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseDBContract {
    public static final String DATABASE_NAME = "HouSync.db";
    public static final int DATABASE_VERSION = 1;

    public static final String VAR_CHAR_TYPE = " varchar";
    public static final String INT_CHAR_TYPE = " INTEGER";
    public static final String TIMESTAMP_CHAR_TYPE = " TIMESTAMP";

    public static final String NOT_NULL = " NOT NULL";
    public static final String PRIMARY_KEY = "  PRIMARY KEY";
    public static final String AUTO_INCREMENT = " AUTOINCREMENT";
    public static final String DEFAULT = " DEFAULT";
    public static final String CURRENT_TIMESTAMP = " CURRENT_TIMESTAMP";

    public static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_HOUSE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ HouseEntry.HOUSE_TABLE_NAME+" ("
                    +HouseEntry.COLUMN_NAME_LOCAL_ID+INT_CHAR_TYPE+PRIMARY_KEY+AUTO_INCREMENT+NOT_NULL+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_ID+INT_CHAR_TYPE+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_NAME+VAR_CHAR_TYPE+NOT_NULL+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_ID_ADMIN+INT_CHAR_TYPE+NOT_NULL+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_SNAPSHOT+VAR_CHAR_TYPE+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_SNAPSHOT_USER+VAR_CHAR_TYPE+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_CREATE_TIME+TIMESTAMP_CHAR_TYPE+DEFAULT+CURRENT_TIMESTAMP+NOT_NULL+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_LAST_SYNC+TIMESTAMP_CHAR_TYPE+" )";

    private static final String SQL_CREATE_USER_HOUSE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ HouseEntry.USER_HOUSE_TABLE_NAME+" ("
                    +HouseEntry.COLUMN_NAME_HOUSE_ID+INT_CHAR_TYPE+PRIMARY_KEY+NOT_NULL+COMMA_SEP
                    +HouseEntry.COLUMN_NAME_USER_ID+INT_CHAR_TYPE+PRIMARY_KEY+NOT_NULL+" )";


    public static SQLiteDatabase getWritableDatabase(Context context){
        return new HouseDBHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context){
        return new HouseDBHelper(context).getReadableDatabase();
    }

    public static abstract class HouseEntry implements BaseColumns {

        public static final String HOUSE_TABLE_NAME = "house";
        public static final String COLUMN_NAME_LOCAL_ID = "local_id";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ID_ADMIN= "id_admin";
        public static final String COLUMN_NAME_SNAPSHOT= "snapshot";
        public static final String COLUMN_NAME_SNAPSHOT_USER= "snapshot_user";
        public static final String COLUMN_NAME_CREATE_TIME= "create_time";
        public static final String COLUMN_NAME_LAST_SYNC= "last_sync";

        public static final String USER_HOUSE_TABLE_NAME = "house";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_HOUSE_ID = "house_id";

    }

    private static class HouseDBHelper extends SQLiteOpenHelper {


        public HouseDBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_HOUSE_TABLE);
            db.execSQL(SQL_CREATE_USER_HOUSE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
