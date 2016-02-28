package pt.nb_web.housync.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.nb_web.housync.R;
import pt.nb_web.housync.background.AddHouseAsyncTask;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.ProgressDialogHelper;


public class AddHouseActivity extends AppCompatActivity implements View.OnClickListener {

    UserLogIn userLogIn;
    HouseService houseService;

    EditText nameView;

    /**
     * @see AppCompatActivity
     *
     * Adds a OnClickListener to the <code>add_house_button</code>
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setView();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setView() {
        userLogIn = UserLogIn.getInstance(this);
        houseService = HouseService.getInstance(this);

        Button add = (Button) findViewById(R.id.add_house_button);
        add.setOnClickListener(this);

        nameView = (EditText) findViewById(R.id.new_house_name);
    }

    @Override
    public void onClick(View v) {
        if (!fieldsVerification()) return;

        String name = nameView.getText().toString();
        House newHouse = null;

        if(userLogIn.checkIfLogedIn()){
            int adminId = userLogIn.getUserId();
            newHouse = new House(name, adminId);
        }else{
            newHouse = new House(name);
        }

        int houseLocalId = houseService.addNew(newHouse);

        if(userLogIn.checkIfLogedIn()){
            try {
                House house = houseService.getHouse(houseLocalId);
                new AddHouseAsyncTask(getBaseContext()).execute(house);
            } catch (HouseNotFoundException e) {
                Log.d("HouseAddActivity", "House not found: " + houseLocalId);
                e.printStackTrace();
            }
        }

        Intent data = new Intent();
        data.putExtra(Commons.HOUSE_ADD_ACTIVIY_PARAMETER, Commons.HOUSE_ADD_ACTIVIY_RESULT_ADD);
        data.putExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, houseLocalId);

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    private boolean fieldsVerification() {
        if( nameView.getText().toString().trim().equals("")){
            nameView.setError(getString(R.string.house_name_mandatory));
            return false;
        }
        return true;
    }
}
