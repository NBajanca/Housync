package pt.nb_web.housync.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.utils.RecyclerItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class HouseManagerActivityFragment extends Fragment {
    private HouseService houseService;
    private HouseRecyclerAdapter houseRecyclerAdapter;

    private Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_house_manager, container, false);

        setupHouseManagerView(getContext(), view);
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        listener = (Listener) activity;
    }

    private void setupHouseManagerView(final Context context, View view) {

        houseService = HouseService.getInstance(context);
        List<House> housesList = houseService.getAllItems();
        setupRecycleView(housesList, view);

    }

    private void setupRecycleView(final List<House> houseList, View view){
        RecyclerView houseManagerRecyclerView = (RecyclerView) view.findViewById(R.id.house_manager_view);
        houseManagerRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());

        houseManagerRecyclerView.setLayoutManager(llm);
        houseRecyclerAdapter = new HouseRecyclerAdapter(houseList);
        houseManagerRecyclerView.setAdapter(houseRecyclerAdapter);

        houseManagerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                House house = houseRecyclerAdapter.getItem(position);
                listener.onItemSelected(house.getHouseLocalId());
            }
        }));
    }

    public interface Listener {
        public void onItemSelected(int id);
    }
}
