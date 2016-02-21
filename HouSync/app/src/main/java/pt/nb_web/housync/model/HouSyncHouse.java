package pt.nb_web.housync.model;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouSyncHouse {
    private int houseId = 0;
    private String houseName = null;
    private String snapShot = null;
    private String snapShotUser = null;
    private int adminId = 0;
    private int errorCode = 0;
    private String createTime = null;
    private String lastSync = null;

    public HouSyncHouse(int houseId, String houseName, int adminId) {
        this.houseId = houseId;
        this.houseName = houseName;
        this.adminId = adminId;
    }

    public HouSyncHouse() {

    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getSnapShot() {
        return snapShot;
    }

    public void setSnapShot(String snapShot) {
        this.snapShot = snapShot;
    }

    public String getSnapShotUser() {
        return snapShotUser;
    }

    public void setSnapShotUser(String snapShotUser) {
        this.snapShotUser = snapShotUser;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }
}
