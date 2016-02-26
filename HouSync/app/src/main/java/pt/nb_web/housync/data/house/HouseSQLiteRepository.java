package pt.nb_web.housync.data.house;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import pt.nb_web.housync.model.House;

import static pt.nb_web.housync.data.house.HouseDBContract.COMMA_SEP;
import static pt.nb_web.housync.data.house.HouseDBContract.CURRENT_TIMESTAMP;
import static pt.nb_web.housync.data.house.HouseDBContract.HouseEntry;
import static pt.nb_web.housync.data.house.HouseDBContract.getWritableDatabase;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseSQLiteRepository {
    private SQLiteDatabase db;

    public HouseSQLiteRepository(Context context) {
        db = getWritableDatabase(context);
    }

    public int addNew(House house) {
        db.execSQL("INSERT INTO " +
                        HouseEntry.HOUSE_TABLE_NAME + "("
                        + HouseEntry.COLUMN_NAME_NAME + COMMA_SEP
                        + HouseEntry.COLUMN_NAME_ID_ADMIN
                        + ") " +
                        "VALUES(?, ?)",
                new Object[]{house.getHouseName(), house.getAdminId()});

        HouseCursor houseCursor = new HouseCursor(
                db.rawQuery("SELECT "+HouseEntry.COLUMN_NAME_LOCAL_ID +
                                " FROM " + HouseEntry.HOUSE_TABLE_NAME +
                                " WHERE " + HouseEntry.COLUMN_NAME_NAME + " = ? " +
                                " ORDER BY " +HouseEntry.COLUMN_NAME_LOCAL_ID + " DESC " +
                                "LIMIT 1",
                        new String[]{house.getHouseName()}));

        houseCursor.moveToFirst();
        int local_id = houseCursor.getLocalId();
        houseCursor.close();

        return local_id;
    }


    public int add(House house) {
        db.execSQL("INSERT INTO " +
                        HouseEntry.HOUSE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_NAME+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_ID_ADMIN+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_SNAPSHOT+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_SNAPSHOT_USER+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_CREATE_TIME+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_LAST_SYNC
                        + ") " +
                        "VALUES(?, ?, ?, ?, ?, ?, "+ CURRENT_TIMESTAMP +")",
                new Object[]{house.getHouseId(), house.getHouseName()
                        , house.getAdminId(), house.getSnapShot()
                        , house.getSnapShotUser(), house.getCreateTime()});

        HouseCursor houseCursor = new HouseCursor(
                db.rawQuery("SELECT "+HouseEntry.COLUMN_NAME_LOCAL_ID +
                                " FROM " + HouseEntry.HOUSE_TABLE_NAME +
                                " WHERE " + HouseEntry.COLUMN_NAME_ID + " = ?;",
                        new String[]{Integer.toString(house.getHouseId())}));

        houseCursor.moveToFirst();
        int local_id = houseCursor.getLocalId();
        houseCursor.close();

        return local_id;
    }

    public void delete(House house) {
        db.execSQL("DELETE FROM " + HouseEntry.HOUSE_TABLE_NAME + " WHERE "
                        + HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{house.getHouseLocalId()});

        if(house.getHouseId() != 0){
            db.execSQL("DELETE FROM " + HouseEntry.USER_HOUSE_TABLE_NAME + " WHERE "
                            + HouseEntry.COLUMN_NAME_HOUSE_ID + " = ?",
                    new Object[]{house.getHouseId()});

            db.execSQL("INSERT INTO " +
                            HouseEntry.DELETE_HOUSE_TABLE_NAME + "("
                            +HouseEntry.COLUMN_NAME_ID
                            + ") " +
                            "VALUES(?)",
                    new Object[]{house.getHouseId()});
        }
    }

    public HouseCursor findAll() {
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.HOUSE_TABLE_NAME,
                        null));
    }

    public HouseCursor getHouse(int houseLocalId) {
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.HOUSE_TABLE_NAME
                        +" WHERE " + HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                        new String[]{Integer.toString(houseLocalId)}));
    }

    public void update(House house) {
        db.execSQL("UPDATE " +HouseEntry.HOUSE_TABLE_NAME
                        + " SET "
                        +HouseEntry.COLUMN_NAME_ID+ " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_NAME+ " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_ID_ADMIN+ " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_SNAPSHOT+ " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_SNAPSHOT_USER+ " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_CREATE_TIME+ " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_LAST_SYNC+ " = "
                        +CURRENT_TIMESTAMP
                        + " WHERE "
                        +HouseEntry.COLUMN_NAME_LOCAL_ID+ " = ?;",
                new Object[]{house.getHouseId(), house.getHouseName()
                        , house.getAdminId(), house.getSnapShot()
                        , house.getSnapShotUser() ,house.getCreateTime()
                        , house.getHouseLocalId()});
    }

    public void updateName(House house) {
        db.execSQL("UPDATE " +
                        HouseEntry.HOUSE_TABLE_NAME + " SET " +
                        HouseEntry.COLUMN_NAME_NAME + " = ?" +
                        " WHERE " +
                        HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{house.getHouseName(), house.getHouseLocalId()});


    }

    public void insertUser(int houseId, int userId) {
        db.execSQL("INSERT INTO " +
                        HouseEntry.USER_HOUSE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_HOUSE_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_USER_ID
                        + ") " +
                        "VALUES(?, ?)",
                new Object[]{houseId, userId});
    }


    public void setDeleted(int houseId){
        db.execSQL("DELETE FROM " + HouseEntry.DELETE_HOUSE_TABLE_NAME + " WHERE "
                        + HouseEntry.COLUMN_NAME_ID + " = ?",
                new Object[]{houseId});
    }

    public HouseCursor getAllDeleted(){
        return new HouseCursor(
                db.rawQuery("SELECT "+ HouseEntry.COLUMN_NAME_ID
                                +" FROM " + HouseEntry.DELETE_HOUSE_TABLE_NAME,
                        null));
    }

    public void insertUpdated(int houseId, String field){
        db.execSQL("INSERT OR IGNORE INTO " +
                        HouseEntry.UPDATE_HOUSE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_FIELD
                        + ") " +
                        "VALUES(?,?)",
                new Object[]{houseId, field});
    }

    public void setUpdated(int houseId, String field){
        db.execSQL("DELETE FROM " +
                        HouseEntry.UPDATE_HOUSE_TABLE_NAME + " WHERE "
                        +HouseEntry.COLUMN_NAME_ID + " = ? AND "
                        +HouseEntry.COLUMN_NAME_FIELD + " = ?;",
                new Object[]{houseId, field});
    }

    public HouseCursor getAllUpdated(){
        return new HouseCursor(
                db.rawQuery("SELECT "+ HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                                +HouseEntry.COLUMN_NAME_FIELD
                                +" FROM " + HouseEntry.UPDATE_HOUSE_TABLE_NAME,
                        null));
    }


    public void updateSnapshot(int houseId, String snapshop) {
        db.execSQL("UPDATE " +
                        HouseEntry.HOUSE_TABLE_NAME + " SET "
                        +HouseEntry.COLUMN_NAME_SNAPSHOT + " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_LAST_SYNC + " = "
                        +CURRENT_TIMESTAMP
                        +" WHERE " +
                        HouseEntry.COLUMN_NAME_ID + " = ?",
                new Object[]{snapshop, houseId});
    }
}


