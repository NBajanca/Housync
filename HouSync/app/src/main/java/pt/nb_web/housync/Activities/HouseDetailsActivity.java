package pt.nb_web.housync.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import pt.nb_web.housync.R;
import pt.nb_web.housync.fragments.HouseDetailsActivityFragment;

public class HouseDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            HouseDetailsActivityFragment fragment = new HouseDetailsActivityFragment();
            fragment.setArguments(getIntent().getExtras());
            
            getSupportFragmentManager().beginTransaction().add(
                    R.id.house_details_fragment, fragment).commit();
        }


    }

}
