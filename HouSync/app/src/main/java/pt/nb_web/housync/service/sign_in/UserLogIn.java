package pt.nb_web.housync.service.sign_in;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import pt.nb_web.housync.R;
import pt.nb_web.housync.exception.UserNotSignInException;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.HouseService;

/**
 * Created by Nuno on 20/02/2016.
 */
public class UserLogIn {
    private final Context context;
    private SharedPreferences userSharedPref;

    private static UserLogIn instance;

    public static synchronized UserLogIn getInstance(Context context) {
        if (instance == null)
            instance = new UserLogIn(context);
        return instance;
    }

    private UserLogIn(Context context){
        this.context = context;
        userSharedPref = context.getSharedPreferences(
                context.getString(R.string.user_file_key), Context.MODE_PRIVATE);
    }

    public boolean checkIfLogedIn(){
        if(getUserId() == 0)
            return false;
        else
            return true;
    }

    public String getUserName(){
        return userSharedPref.getString(context.getString(R.string.houSyncUserName), null);
    }

    public void setUserName(String userName){
        if(userName != getUserName()) {
            SharedPreferences.Editor editor = userSharedPref.edit();
            editor.putString(context.getString(R.string.houSyncUserName), userName);
            editor.commit();

            User user = new User(getUserId());
            user.setName(userName);
        }
    }

    public int getUserId(){
        return userSharedPref.getInt(context.getString(R.string.houSyncUserId), 0);
    }

    public void setUserId(int userId){
        if (userId != getUserId()) {
            SharedPreferences.Editor editor = userSharedPref.edit();
            editor.putInt(context.getString(R.string.houSyncUserId), userId);
            editor.commit();

            Toast.makeText(context, "Signed In", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearUser(){
        SharedPreferences.Editor editor = userSharedPref.edit();
        editor.clear();
        editor.commit();

        Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show();
    }

    public User getUser() throws UserNotSignInException {
        if (!checkIfLogedIn()) throw new UserNotSignInException();
        int id = getUserId();
        String name = getUserName();

        User user = new User(id);
        user.setName(name);

        return user;
    }
}
