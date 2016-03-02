package pt.nb_web.housync.model;

import com.example.nuno.myapplication.housync_backend.myApi.model.HouSyncUser;

/**
 * Created by Nuno on 29/02/2016.
 */
public class User {
    int userId = 0;
    String name = null;
    String email = null;
    String phone = null;
    String snapshot = null;

    public User(int userId) {
        this.userId = userId;
    }

    public User() {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return getUserId() == user.getUserId();

    }

    @Override
    public int hashCode() {
        return getUserId();
    }

    public static User getUserFromHouSyncUser(HouSyncUser houSyncUser) {
        User user = new User(houSyncUser.getUserId());
        user.setName(houSyncUser.getUserName());
        user.setEmail(houSyncUser.getEmail());
        user.setPhone(houSyncUser.getPhone());
        user.setSnapshot(houSyncUser.getSnapshot());

        return user;
    }
}
