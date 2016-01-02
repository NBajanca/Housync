package nunobajanca.housync.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nunobajanca.housync.R;
import nunobajanca.housync.adapters.ShoppingListRecyclerAdapter;
import nunobajanca.housync.model.ShoppingListItem;
import nunobajanca.housync.service.ShoppingListService;

/**
 * Created by Nuno on 15/12/2015.
 */
public class ShoppingListFragment extends Fragment {

    private ShoppingListService shoppingListService;
    private ShoppingListRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public ShoppingListFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        String title = getResources().getString(R.string.nav_shopping_card);

        getActivity().setTitle(title);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupShoppingListView(getActivity(), view);
    }

    private void setupShoppingListView(final Context context, final View view) {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.wait),
                getResources().getString(R.string.wait_shopping_list), true, false);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        shoppingListService = ShoppingListService.getInstance(context);
        List<ShoppingListItem> shoppingItems = shoppingListService.getAllItems();

        setupRecycleView(shoppingItems, view);
        progressDialog.dismiss();

    }

    private void setupRecycleView(final List<ShoppingListItem> shoppingItems, View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.shopping_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(llm);
        mAdapter = new ShoppingListRecyclerAdapter(shoppingItems);
        mRecyclerView.setAdapter(mAdapter);
    }
}