package pt.nb_web.housync.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.fragments.HouseDetailsActivityFragment;
import pt.nb_web.housync.fragments.HouseManagerActivityFragment;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.utils.Commons;

public class HouseDetailsActivity extends AppCompatActivity
        implements HouseDetailsActivityFragment.Listener{
    HouseDetailsActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            fragment = new HouseDetailsActivityFragment();
            fragment.setArguments(getIntent().getExtras());
            
            getSupportFragmentManager().beginTransaction().add(
                    R.id.house_details_fragment, fragment).commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.house_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteHouse();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteHouse(){
        Intent data = new Intent();
        data.putExtra(Commons.HOUSE_DETAILS_ACTIVIY_PARAMETER, Commons.HOUSE_DETAILS_ACTIVIY_RESULT_DELETE);
        data.putExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, fragment.getHouseLocalId());

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    @Override
    public void onHouseDeleted(int houseLocalId) {
        deleteHouse();
    }
}
