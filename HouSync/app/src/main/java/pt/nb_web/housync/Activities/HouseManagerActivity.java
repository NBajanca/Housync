package pt.nb_web.housync.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;

import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.fragments.HouseDetailsActivityFragment;
import pt.nb_web.housync.fragments.HouseManagerActivityFragment;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.DrawerHelper;

public class HouseManagerActivity extends AppCompatActivity
        implements HouseManagerActivityFragment.Listener, HouseDetailsActivityFragment.Listener{
    private DrawerHelper drawerHelper;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddHouseActivity.class);
                startActivityForResult(intent, Commons.HOUSE_ADD_ACTIVIY_RESULT);
            }
        });


        if (findViewById(R.id.fragment_house_details) != null) {
            mTwoPane = true;
        }

        drawerHelper = new DrawerHelper(this,toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.house_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerHelper.updateNavHeader();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        HouseService houseService = HouseService.getInstance(this);
        HouseRecyclerAdapter houseRecyclerAdapter = (HouseRecyclerAdapter)
                ((RecyclerView)findViewById(R.id.house_manager_view)).getAdapter();

        List<House> housesList = houseService.getAllItems();

        if (requestCode == Commons.HOUSE_ADD_ACTIVIY_RESULT){
            if (housesList.size() != houseRecyclerAdapter.getItemCount()){
                houseRecyclerAdapter.updateList(housesList);
                houseRecyclerAdapter.notifyItemInserted(housesList.size());
            }
        }else if( requestCode == Commons.HOUSE_DETAILS_ACTIVIY_RESULT){
            if (housesList.size() != houseRecyclerAdapter.getItemCount()){
                houseRecyclerAdapter.updateList(housesList);
                houseRecyclerAdapter.notifyDataSetChanged();
            }
        }

    }


    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(Commons.HOUSE_LOCAL_ID_PARAMETER, id);

            HouseDetailsActivityFragment fragment = new HouseDetailsActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_house_details, fragment).commit();


        } else {
            Intent intent = new Intent(this , HouseDetailsActivity.class);
            intent.putExtra(Commons.HOUSE_LOCAL_ID_PARAMETER, id);
            startActivityForResult(intent, Commons.HOUSE_DETAILS_ACTIVIY_RESULT);
        }
    }

    @Override
    public void onHouseDeleted(House house) {
        HouseService houseService = HouseService.getInstance(this);
        HouseRecyclerAdapter houseRecyclerAdapter = (HouseRecyclerAdapter)
                ((RecyclerView)findViewById(R.id.house_manager_view)).getAdapter();

        int positionRecycler = houseRecyclerAdapter.getItemPosition(house);
        houseService.delete(house);

        List<House> housesList = houseService.getAllItems();
        houseRecyclerAdapter.updateList(housesList);

        houseRecyclerAdapter.notifyItemRemoved(positionRecycler);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_house_details, new HouseDetailsActivityFragment()).commit();
    }
}
