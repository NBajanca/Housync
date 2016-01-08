package nunobajanca.housync.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nunobajanca.housync.R;
import nunobajanca.housync.model.ShoppingListItem;
import nunobajanca.housync.service.ShoppingListService;

public class AddItemShoppingListActivity extends AppCompatActivity {
    ShoppingListService shoppingListService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_shopping_list_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shoppingListService = ShoppingListService.getInstance(this);
        handleClick();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleClick() {
        Button button = (Button) findViewById(R.id.button_new_item_shopping_list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.name_new_item_shopping_list);
                ShoppingListItem shoppingListNewItem = new ShoppingListItem(editText.getText().toString());
                shoppingListService.add(shoppingListNewItem);

                Toast.makeText(getBaseContext(), shoppingListNewItem.getName() + " " + getBaseContext().getResources().getString(R.string.shopping_list_item_added), Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }


}
