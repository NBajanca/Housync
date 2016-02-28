package pt.nb_web.housync.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.nb_web.housync.R;
import pt.nb_web.housync.background.DeleteHouseAsyncTask;
import pt.nb_web.housync.background.UpdateHouseAsyncTask;
import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.utils.Commons;

public class EditHouseActivity extends AppCompatActivity {

    private String TAG = "EditHouseActivity";

    private HouseService houseService;
    private House house;
    private EditText editHouseNameField;

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


        } catch (HouseNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "No House Found for id: " + Integer.toString(localId));
            noChanges();
        }
    }

    private void prepareFields() {
        editHouseNameField = (EditText) findViewById(R.id.edited_house_name);

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

    }

    private void saveChanges() {
        Intent data = new Intent();
        data.putExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, house.getHouseLocalId());

        String editedHouseName = editHouseNameField.getText().toString();
        if(editedHouseName != house.getHouseName()){
            house.setHouseName(editedHouseName);
            houseService.updateName(house);
            if (house.getHouseId() > 0){
                new UpdateHouseAsyncTask(getBaseContext()).execute(new Pair<>(house
                        , HouseDBContract.HouseEntry.COLUMN_NAME_NAME));
            }
            data.putExtra(Commons.HOUSE_EDIT_ACTIVIY_PARAMETER, Commons.HOUSE_EDIT_ACTIVIY_RESULT_EDIT);
        }else{
            noChanges();
        }

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    private void noChanges(){
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

}
