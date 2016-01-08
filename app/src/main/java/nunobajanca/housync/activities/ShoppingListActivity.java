package nunobajanca.housync.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import nunobajanca.housync.R;
import nunobajanca.housync.adapters.ShoppingListRecyclerAdapter;
import nunobajanca.housync.model.ShoppingListItem;
import nunobajanca.housync.service.ShoppingListService;

public class ShoppingListActivity extends AppCompatActivity{
    private ShoppingListRecyclerAdapter shoppingListRecyclerAdapter;
    private LinearLayoutManager llm;
    ShoppingListService shoppingListService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);


        setupShoppingListView(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddItemShoppingListActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first

        List<ShoppingListItem> updatedShoppingList = shoppingListService.getAllItems();
        if (shoppingListRecyclerAdapter.getItemCount() != updatedShoppingList.size()){
            shoppingListRecyclerAdapter.updateList(updatedShoppingList);
            shoppingListRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void setupShoppingListView(final Context context) {

        shoppingListService = ShoppingListService.getInstance(context);
        List<ShoppingListItem> shoppingList = shoppingListService.getAllItems();
        setupRecycleView(shoppingList);

    }

    private void setupRecycleView(final List<ShoppingListItem> shoppingList){
        RecyclerView shoppingListRecyclerView = (RecyclerView) findViewById(R.id.shopping_list_view);
        shoppingListRecyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);

        shoppingListRecyclerView.setLayoutManager(llm);
        shoppingListRecyclerAdapter = new ShoppingListRecyclerAdapter(shoppingList);
        shoppingListRecyclerView.setAdapter(shoppingListRecyclerAdapter);
    }


}
