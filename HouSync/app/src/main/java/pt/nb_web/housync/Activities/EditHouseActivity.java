package pt.nb_web.housync.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.UserHouseRecyclerAdapter;
import pt.nb_web.housync.background.AddUserHouseAsyncTask;
import pt.nb_web.housync.background.UpdateOnlineHouseAsyncTask;
import pt.nb_web.housync.background.UpdateOnlineUserHouseAsyncTask;
import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.exception.UserNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.NetworkHelper;
import pt.nb_web.housync.utils.ProgressDialogHelper;

public class EditHouseActivity extends AppCompatActivity implements UserHouseRecyclerAdapter.Listener{

    private static final int INVALID_USER_ID = 0;
    private static final int USER_ALREADY_ADDED = 1;
    private static final int USER_IS_YOU = 2;
    private String TAG = "EditHouseActivity";

    private HouseService houseService;
    private UserHouseRecyclerAdapter userHouseRecyclerAdapter;
    private House house;

    private List<Integer> usersToAdd = new ArrayList<>();
    public List<Integer> usersAdded = new ArrayList<>();
    private List<Integer> usersToRemove = new ArrayList<>();

    private EditText editHouseNameField;
    private EditText addUserIdField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.house_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_discard){
            noChanges();
        }else if(id == R.id.action_save){
            saveChanges();
        }else if(id == R.id.action_delete){
            deleteHouse(house.getHouseLocalId());
        }

        return super.onOptionsItemSelected(item);
    }

    private void setView() {
        Intent intent = getIntent();
        int localId = intent.getIntExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, 0);
        if (localId == 0){
            Log.d(TAG, "No House Found - No extra");
            noChanges();
        }

        houseService = HouseService.getInstance(this);
        try {
            house = houseService.getHouse(localId);
            prepareFields();

            setTitle(getString(R.string.title_edit_house, house.getHouseName()));
            editHouseNameField.setText(house.getHouseName());

            setupUserHouseManagerView();
        } catch (HouseNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "No House Found for id: " + Integer.toString(localId));
            noChanges();
        }


    }

    private void prepareFields() {
        editHouseNameField = (EditText) findViewById(R.id.edited_house_name);
        addUserIdField = (EditText) findViewById(R.id.added_user_id);

        findViewById(R.id.discard_edit_house_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noChanges();
            }
        });

        findViewById(R.id.save_edit_house_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        findViewById(R.id.add_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

    }

    private void addUser() {
        addUserIdField.setEnabled(false);
        addUserIdField.setEnabled(true);

        int userId = getUserId();
        if (userId == 0) return;

        try {
            User user = houseService.getUser(userId);
            houseService.insertUser(house.getHouseId(), user);
            userHouseRecyclerAdapter.addItem(user);
            usersToAdd.add(userId);
        } catch (UserNotFoundException e) {
            if(!NetworkHelper.isOnline(this)){
                setAddUserError(Commons.NO_INTERNET);
            }else{
                ProgressDialogHelper.show(this, "Adding User");
                new AddUserHouseAsyncTask(this).execute(new Pair(house.getHouseId(), userId));
            }
        }
    }

    public void setAddUserError(int error) {
        String title, text;

        switch (error){
            case (INVALID_USER_ID):
                addUserIdField.setError(getString(R.string.invalid_user_id));
                break;
            case (USER_ALREADY_ADDED):
                addUserIdField.setError(getString(R.string.repeated_user_id));
                break;
            case (USER_IS_YOU):
                addUserIdField.setError(getString(R.string.self_user_id));
                break;
            case (Commons.NO_INTERNET):
                title = "No Internet Connection";
                text = "You need to be connected to the Internet to add Hosts that you don't share a house with.";
                setWarning(title, text);
                break;
            case (Commons.NOT_SIGNED_IN):
                title = "Not Signed In";
                setWarning(title, "");
                break;
            case(Commons.USER_NOT_FOUND):
                addUserIdField.setError(getString(R.string.incorrect_user_id));
                break;
        }


    }

    private void setWarning(String title, String text) {
        LinearLayout warningRootView = (LinearLayout) findViewById(R.id.warnings_view);
        CardView warningCardView= (CardView) getLayoutInflater().inflate(R.layout.warning_card, null);
        warningRootView.addView(warningCardView);
        TextView titleView = (TextView) findViewById(R.id.warning_card_title);
        TextView textView = (TextView) findViewById(R.id.warning_card_text);
        titleView.setText(title);
        textView.setText(text);
    }

    private int getUserId() {
        String userIdString = addUserIdField.getText().toString();
        if (userIdString != null && !userIdString.equals("")) {
            int userId = Integer.parseInt(userIdString);
            if (userId >= 0){
                UserLogIn userLogIn = UserLogIn.getInstance(this);
                if (userId == userLogIn.getUserId()){
                    setAddUserError(USER_IS_YOU);
                    return 0;
                }
                List<User> usersList = houseService.getUsers(house.getHouseId());
                for (User user: usersList ) {
                    if (userId == user.getUserId()){
                        setAddUserError(USER_ALREADY_ADDED);
                        return 0;
                    }
                }
                return userId;
            }
        }
        setAddUserError(INVALID_USER_ID);
        return 0;
    }

    private void saveChanges() {
        Intent data = new Intent();
        data.putExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, house.getHouseLocalId());

        String editedHouseName = editHouseNameField.getText().toString();
        if(editedHouseName != house.getHouseName()){
            house.setHouseName(editedHouseName);
            houseService.updateName(house, Commons.LOCAL_UPDATE);
            if (house.getHouseId() > 0){
                new UpdateOnlineHouseAsyncTask(this).execute(new Pair<>(house.getHouseId()
                        , HouseDBContract.HouseEntry.COLUMN_NAME_NAME));
            }
            data.putExtra(Commons.HOUSE_EDIT_ACTIVIY_PARAMETER, Commons.HOUSE_EDIT_ACTIVIY_RESULT_EDIT);
        }else{
            noChanges();
        }

        for (int userId : usersToAdd) {
            new UpdateOnlineUserHouseAsyncTask(this).execute(Integer.toString(house.getHouseId())
                    ,Integer.toString(userId), HouseDBContract.ACTION_ADDED);
        }

        for (int userId : usersToRemove) {
            new UpdateOnlineUserHouseAsyncTask(this).execute(Integer.toString(house.getHouseId())
                    ,Integer.toString(userId), HouseDBContract.ACTION_DELETED);
        }


        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    private void noChanges(){
        for (int userId : usersToAdd) {
            houseService.deleteUser(house.getHouseId(), userId);
            houseService.setUserUpdated(house, userId);
        }

        for (int userId : usersToRemove) {
            houseService.insertUser(house.getHouseId(), new User(userId));
            houseService.setUserUpdated(house, userId);
        }

        for (int userId: usersAdded) {
            houseService.deleteUser(house.getHouseId(), userId);
            new UpdateOnlineUserHouseAsyncTask(this).execute(Integer.toString(house.getHouseId())
                    ,Integer.toString(userId), HouseDBContract.ACTION_DELETED);
        }

        if (getParent() == null) {
            setResult(Activity.RESULT_CANCELED);
        } else {
            getParent().setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    private void deleteHouse(int houseLocalId){
        Intent data = new Intent();
        data.putExtra(Commons.HOUSE_EDIT_ACTIVIY_PARAMETER, Commons.HOUSE_EDIT_ACTIVIY_RESULT_DELETE);
        data.putExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, houseLocalId);

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    private void setupUserHouseManagerView() {
        houseService = HouseService.getInstance(this);
        List<User> usersList = houseService.getUsers(house.getHouseId());

        if (Commons.DEBUG){
            int nOfUsers = usersList.size();
            Log.d("HouseDetailsFragment", "# of users: " + Integer.toString(nOfUsers));
        }

        setupRecycleView(usersList);
    }

    private void setupRecycleView(final List<User> usersList){
        RecyclerView userHouseManagerRecyclerView = (RecyclerView) findViewById(R.id.users_edit_house_view);
        userHouseManagerRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        userHouseManagerRecyclerView.setLayoutManager(llm);
        userHouseRecyclerAdapter = new UserHouseRecyclerAdapter(usersList, this);

        userHouseManagerRecyclerView.setAdapter(userHouseRecyclerAdapter);
    }

    @Override
    public void onUserDeleted(int userID) {
        houseService.deleteUser(house.getHouseId(), userID);
        usersToRemove.add(userID);
        userHouseRecyclerAdapter.removeItem(new User(userID));
    }
}
