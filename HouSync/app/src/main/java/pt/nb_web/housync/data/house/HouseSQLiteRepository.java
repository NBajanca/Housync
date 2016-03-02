package pt.nb_web.housync.data.house;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;

import static pt.nb_web.housync.data.house.HouseDBContract.COMMA_SEP;
import static pt.nb_web.housync.data.house.HouseDBContract.CURRENT_TIMESTAMP;
import static pt.nb_web.housync.data.house.HouseDBContract.HouseEntry;
import static pt.nb_web.housync.data.house.HouseDBContract.getReadableDatabase;
import static pt.nb_web.housync.data.house.HouseDBContract.getWritableDatabase;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseSQLiteRepository {
    private SQLiteDatabase db;

    static final public String READ_PERMISSION = "read";
    static final public String WRITE_PERMISSION = "write";


    public HouseSQLiteRepository(Context context, String permissions) {
        if (permissions == WRITE_PERMISSION)
            db = getWritableDatabase(context);
        else
            db = getReadableDatabase(context);
    }


    /**
     * Adds a new Local house to the DB.
     * Sets the Name and the admin and returns the localId
     *
     * @param house (name; adminID)
     * @return localId
     */
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
        int localId = houseCursor.getLocalId();
        houseCursor.close();

        return localId;
    }

    /**
     * Adds a house, that already exists online, to the DB.
     *
     * @param house
     * @return localId
     */
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
        int localId = houseCursor.getLocalId();
        houseCursor.close();

        return localId;
    }


    /**
     * Deletes the House from the DB
     *
     * @param houseLocalId
     */
    public void delete(int houseLocalId) {
        db.execSQL("DELETE FROM " + HouseEntry.HOUSE_TABLE_NAME + " WHERE "
                        + HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{houseLocalId});
    }


    /**
     * Gets the house from the DB
     *
     * @param houseLocalId
     * @return HouseCursor
     */
    public HouseCursor getHouse(int houseLocalId) {
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.HOUSE_TABLE_NAME
                                +" WHERE " + HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                        new String[]{Integer.toString(houseLocalId)}));
    }

    /**
     * Returns all houses
     *
     * @return HouseCursor
     */
    public HouseCursor findAll() {
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.HOUSE_TABLE_NAME,
                        null));
    }

    /**
     * Updates the lastSync Value to the current time
     *
     * @param houseLocalId
     */
    public void setLastSync(int houseLocalId){
        db.execSQL("UPDATE " +
                        HouseEntry.HOUSE_TABLE_NAME + " SET "
                        +HouseEntry.COLUMN_NAME_LAST_SYNC + " = "
                        +CURRENT_TIMESTAMP
                        +" WHERE " +
                        HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{houseLocalId});
    }

    /**
     * Updates all the values of the house
     *
     * @param house
     */
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

    /**
     * Updates the Name of the house
     *
     * @param house
     */
    public void updateName(House house) {
        db.execSQL("UPDATE " +
                        HouseEntry.HOUSE_TABLE_NAME + " SET " +
                        HouseEntry.COLUMN_NAME_NAME + " = ?" +
                        " WHERE " +
                        HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{house.getHouseName(), house.getHouseLocalId()});


    }

    /**
     * Updates the snapshot of the house.
     *
     * @param houseLocalId
     * @param snapshop
     */
    public void updateSnapshot(int houseLocalId, String snapshop) {
        db.execSQL("UPDATE " +
                        HouseEntry.HOUSE_TABLE_NAME + " SET "
                        +HouseEntry.COLUMN_NAME_SNAPSHOT + " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_LAST_SYNC + " = "
                        +CURRENT_TIMESTAMP
                        +" WHERE " +
                        HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{snapshop, houseLocalId});
    }

    /**
     * Updates the snapshot_user of the house
     *
     * @param houseLocalId
     * @param snapshopUser
     */
    public void updateSnapshotUser(int houseLocalId, String snapshopUser) {
        db.execSQL("UPDATE " +
                        HouseEntry.HOUSE_TABLE_NAME + " SET "
                        +HouseEntry.COLUMN_NAME_SNAPSHOT_USER + " = ?"+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_LAST_SYNC + " = "
                        +CURRENT_TIMESTAMP
                        +" WHERE " +
                        HouseEntry.COLUMN_NAME_LOCAL_ID + " = ?",
                new Object[]{snapshopUser, houseLocalId});
    }

    /**
     * Gets all the users of a particular house
     *
     * @param houseId
     * @return HouseCursor
     */
    public HouseCursor getUsers(int houseId){
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.USER_HOUSE_TABLE_NAME
                                +" JOIN " + HouseEntry.USER_TABLE_NAME
                                +" ON " +HouseEntry.COLUMN_NAME_USER_ID + " = "
                                +HouseEntry.COLUMN_NAME_ID
                                +" WHERE " + HouseEntry.COLUMN_NAME_HOUSE_ID + " = ?",
                        new String[]{Integer.toString(houseId)}));


    }

    /**
     * Inserts a user in a house
     *
     * @param houseId
     * @param userId
     */
    public void insertUser(int houseId, int userId) {
        db.execSQL("INSERT INTO " +
                        HouseEntry.USER_HOUSE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_HOUSE_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_USER_ID
                        + ") " +
                        "VALUES(?, ?)",
                new Object[]{houseId, userId});
    }

    /**
     * Deletes a user from a house
     *
     * @param houseId
     * @param userID
     */
    public void deleteUser (int houseId, int userID){
        db.execSQL("DELETE FROM " + HouseEntry.USER_HOUSE_TABLE_NAME + " WHERE "
                        + HouseEntry.COLUMN_NAME_HOUSE_ID +" = ? "+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_USER_ID+" = ? ",
                new Object[]{houseId, userID});
    }

    /**
     * Deletes all the users of a certain house
     *
     * @param houseId
     */
    public void deleteAllUsers(int houseId){
        db.execSQL("DELETE FROM " + HouseEntry.USER_HOUSE_TABLE_NAME + " WHERE "
                        + HouseEntry.COLUMN_NAME_HOUSE_ID + " = ?",
                new Object[]{houseId});
    }

    /**
     * Inserts a house in the Deleted List
     *
     * @param houseId
     */
    public void insertDeleted(int houseId){
        db.execSQL("INSERT INTO " +
                        HouseEntry.DELETE_HOUSE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_ID
                        + ") " +
                        "VALUES(?)",
                new Object[]{houseId});
    }

    /**
     * Removes a house from the Deleted List
     *
     * @param houseId
     */
    public void setDeleted(int houseId){
        db.execSQL("DELETE FROM " + HouseEntry.DELETE_HOUSE_TABLE_NAME + " WHERE "
                        + HouseEntry.COLUMN_NAME_ID + " = ?",
                new Object[]{houseId});
    }

    /**
     * Gets all houses that are in the Deleted List
     *
     * @return
     */
    public HouseCursor getAllDeleted(){
        return new HouseCursor(
                db.rawQuery("SELECT "+ HouseEntry.COLUMN_NAME_ID
                                +" FROM " + HouseEntry.DELETE_HOUSE_TABLE_NAME,
                        null));
    }

    /**
     * Adds a (house, field) to the updates list
     *
     * @param houseId
     * @param field
     */
    public void insertUpdated(int houseId, String field){
        db.execSQL("INSERT OR IGNORE INTO " +
                        HouseEntry.UPDATE_HOUSE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_FIELD
                        + ") " +
                        "VALUES(?,?)",
                new Object[]{houseId, field});
    }

    /**
     * Removes a (house,field) from the updates list
     *
     * @param houseId
     * @param field
     */
    public void setUpdated(int houseId, String field){
        db.execSQL("DELETE FROM " +
                        HouseEntry.UPDATE_HOUSE_TABLE_NAME + " WHERE "
                        +HouseEntry.COLUMN_NAME_ID + " = ? AND "
                        +HouseEntry.COLUMN_NAME_FIELD + " = ?;",
                new Object[]{houseId, field});
    }

    /**
     * Returns the list of updates done
     *
     * @return HouseCursor
     */
    public HouseCursor getAllUpdated(){
        return new HouseCursor(
                db.rawQuery("SELECT "+ HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                                +HouseEntry.COLUMN_NAME_FIELD
                                +" FROM " + HouseEntry.UPDATE_HOUSE_TABLE_NAME,
                        null));
    }

    /**
     * Adds a user to the updated list.
     * @param houseId
     * @param userId
     * @param action
     */
    public void insertUserUpdated(int houseId, int userId, String action){
        db.execSQL("INSERT OR REPLACE INTO " +
                        HouseEntry.USER_HOUSE_UPDATE_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_USER_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_ACTION
                        + ") " +
                        "VALUES(?,?,?)",
                new Object[]{houseId, userId, action});
    }


    /**
     * Removes a user from the updated list.
     *
     * @param houseId
     * @param userId
     */
    public void setUserUpdated(int houseId, int userId){
        db.execSQL("DELETE FROM " +
                        HouseEntry.USER_HOUSE_UPDATE_TABLE_NAME + " WHERE "
                        +HouseEntry.COLUMN_NAME_ID + " = ? AND "
                        +HouseEntry.COLUMN_NAME_USER_ID + " = ?;",
                new Object[]{houseId, userId});
    }

    /**
     * Returns the list of user updates done
     *
     * @return
     */
    public HouseCursor getAllUsersUpdated(){
        return new HouseCursor(
                db.rawQuery("SELECT "+ HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                                +HouseEntry.COLUMN_NAME_USER_ID+COMMA_SEP
                                +HouseEntry.COLUMN_NAME_ACTION
                                +" FROM " + HouseEntry.USER_HOUSE_UPDATE_TABLE_NAME,
                        null));
    }

    public void addUser(User user){
        db.execSQL("INSERT OR REPLACE INTO " +
                        HouseEntry.USER_TABLE_NAME + "("
                        +HouseEntry.COLUMN_NAME_ID+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_NAME+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_EMAIL+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_PHONE+COMMA_SEP
                        +HouseEntry.COLUMN_NAME_SNAPSHOT
                        + ") " +
                        "VALUES(?,?,?,?,?)",
                new Object[]{user.getUserId(), user.getName(), user.getEmail()
                        ,user.getPhone(), user.getSnapshot()});
    }

    public void deleteUser(int userId){
        db.execSQL("DELETE FROM " +
                        HouseEntry.USER_TABLE_NAME + " WHERE "
                        +HouseEntry.COLUMN_NAME_ID + " = ?;",
                new Object[]{userId});
    }

    public HouseCursor getUser(int userId){
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.USER_TABLE_NAME
                                +" WHERE " + HouseEntry.COLUMN_NAME_ID + " = ?",
                        new String[]{Integer.toString(userId)}));

    }

    public HouseCursor getAllUsers() {
        return new HouseCursor(
                db.rawQuery("SELECT * FROM " + HouseEntry.USER_TABLE_NAME,
                        null));
    }

}


