package pt.nb_web.housync.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.nb_web.housync.R;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.ProgressDialogHelper;

public class AddHouseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button add = (Button) findViewById(R.id.add_house_button);
        add.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {

        EditText nameView = (EditText) findViewById(R.id.new_house_name);
        String name = nameView.getText().toString();

        UserLogIn userLogIn = UserLogIn.getInstance(this);
        HouseService service = HouseService.getInstance(this);

        int adminId = userLogIn.getUserId();
        House newHouse = new House(name, adminId);

        service.add(newHouse);
        finish();
    }
}
