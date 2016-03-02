package pt.nb_web.housync.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nuno on 21/02/2016.
 */
public class House extends HouSyncHouse {
    private int houseLocalId;
    private List<Integer> usersIdList;

    public House(int localId, String name) {
        setHouseLocalId(localId);
        setHouseName(name);
    }

    public House(String name, int id_admin) {
        setHouseName(name);
        setAdminId(id_admin);
    }

    public House(String name) {
        setHouseName(name);
        setAdminId(0);
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

    public List<Integer> getUsersIdList() {
        return usersIdList;
    }

    public void setUsersIdList(List<Integer> usersIdList) {
        this.usersIdList = new ArrayList<>(usersIdList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        House house = (House) o;

        return getHouseLocalId() == house.getHouseLocalId();

    }

    @Override
    public int hashCode() {
        return getHouseLocalId();
    }

    public static House getHouseFromHouSyncHouse(com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse house) {
        House response = new House(house.getHouseName(), house.getAdminId());
        response.setHouseId(house.getHouseId());
        response.setSnapShot(house.getSnapShot());
        response.setSnapShotUser(house.getSnapShotUser());
        response.setCreateTime(house.getCreateTime());
        response.setLastSync(house.getLastSync());

        return response;
    }

    public com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse getHouSyncHouseFromHouse(House house) {
        com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse response = new
                com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncHouse();

        response.setHouseName(house.getHouseName());
        response.setAdminId(house.getAdminId());
        response.setCreateTime(house.getCreateTime());
        return response;
    }


}
