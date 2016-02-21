package pt.nb_web.housync.model;



/**
 * Created by Nuno on 21/02/2016.
 */
public class House extends HouSyncHouse {
    private int houseLocalId;

    public House(int localId, String name) {
        setHouseLocalId(localId);
        setHouseName(name);
    }

    public House(String name, int id_admin) {
        setHouseName(name);
        setAdminId(id_admin);
    }

    public House(int houseLocalId) {
        setHouseLocalId(houseLocalId);
    }

    public int getHouseLocalId() {
        return houseLocalId;
    }

    public void setHouseLocalId(int houseLocalId) {
        this.houseLocalId = houseLocalId;
    }

    public String getLocalIdString() {
        return Integer.toString(getHouseLocalId());
    }
}
