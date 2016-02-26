package pt.nb_web.housync.data.house;

import android.database.Cursor;
import android.database.CursorWrapper;

import pt.nb_web.housync.model.House;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseCursor extends CursorWrapper{

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public HouseCursor(Cursor cursor) {
        super(cursor);
    }

    public int getLocalId(){
        return getInt(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_LOCAL_ID));
    }

    public int getId(){
        return getInt(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_ID));
    }

    public String getName(){
        return getString(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_NAME));
    }

    public int getAdminId(){
        return getInt(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_ID_ADMIN));
    }

    public String getSnapshot(){
        return getString(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_SNAPSHOT));
    }

    public String getSnapshotUser(){
        return getString(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_SNAPSHOT_USER));
    }

    public String getCrateTime(){
        return getString(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_CREATE_TIME));
    }

    public String getLastSync(){
        return getString(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_LAST_SYNC));
    }

    public String getField() {
        return getString(
                getColumnIndex(HouseDBContract.
                        HouseEntry.COLUMN_NAME_FIELD));
    }

    public House getHouse(){
        int houseLocalId = getLocalId();
        int houseId = getId();
        String houseName = getName();
        String snapShot = getSnapshot();
        String snapShotUser = getSnapshotUser();
        int adminId = getAdminId();
        String createTime = getCrateTime();
        String lastSync = getLastSync();


        House response = new House(houseLocalId,houseName);
        response.setHouseId(houseId);
        response.setSnapShot(snapShot);
        response.setSnapShotUser(snapShotUser);
        response.setAdminId(adminId);
        response.setCreateTime(createTime);
        response.setLastSync(lastSync);

        return response;
    }



}
