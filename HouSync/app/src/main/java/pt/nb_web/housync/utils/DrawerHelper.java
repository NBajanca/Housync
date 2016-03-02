package pt.nb_web.housync.utils;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import pt.nb_web.housync.R;
import pt.nb_web.housync.activities.HouseManagerActivity;
import pt.nb_web.housync.activities.LogInActivity;
import pt.nb_web.housync.activities.MainActivity;
import pt.nb_web.housync.service.sign_in.UserLogIn;

/**
 * Created by Nuno on 21/02/2016.
 */
public class DrawerHelper implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{

    private static final int NAV_HEADER = 0;
    private View headerView;

    private AppCompatActivity activity;

    public DrawerHelper(AppCompatActivity activity, Toolbar toolbar){
        this.activity = activity;

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity , drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (activity instanceof MainActivity){
            navigationView.getMenu().getItem(0).setChecked(true);
        }else if (activity instanceof HouseManagerActivity){
            navigationView.getMenu().getItem(1).setChecked(true);
        }

        headerView = navigationView.getHeaderView(NAV_HEADER);
        headerView.findViewById(R.id.user_name).setOnClickListener(this);
        headerView.findViewById(R.id.user_id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ((v.getId() == R.id.user_id) || (v.getId() == R.id.user_name)){
            Intent intent = new Intent(activity, LogInActivity.class);
            activity.startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_main && !(activity instanceof MainActivity)) {
            intent = new Intent(activity, MainActivity.class);
        } else if (id == R.id.nav_house_manager && !(activity instanceof HouseManagerActivity)) {
            intent = new Intent(activity, HouseManagerActivity.class);
        } else if (id == R.id.nav_notes) {

        } else if (id == R.id.nav_shopping_list) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if (intent != null){
            activity.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(Context context){
        TextView userIdTextView = (TextView) headerView.findViewById(R.id.user_id);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.user_name);

        UserLogIn userLogIn = UserLogIn.getInstance(context);
        if(userLogIn.checkIfLogedIn()){
            userIdTextView.setText(Integer.toString(userLogIn.getUserId()));
            userNameTextView.setText(userLogIn.getUserName());
        }else{
            userIdTextView.setText(R.string.manage_sign_in);
            userNameTextView.setText(R.string.no_login);
        }
    }
}
