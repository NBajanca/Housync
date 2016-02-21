package pt.nb_web.housync.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.nb_web.housync.R;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;

/**
 * A placeholder fragment containing a simple view.
 */
public class HouseDetailsActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_house_details, container, false);

        int houseLocalId = getHouseLocalId();
        if(houseLocalId >= 1){
            HouseService houseService = HouseService.getInstance(view.getContext());

            House house = new House(houseLocalId);
            house = houseService.getHouse(house);

            TextView nameTextView = (TextView) view.findViewById(R.id.house_fragment_house_name);
            nameTextView.setText(house.getHouseName());
        }


        return view;
    }

    public int getHouseLocalId() {
        Bundle bundle = getArguments();
        if(getArguments() != null){
            return getArguments().getInt(HouseRecyclerAdapter.EXTRA_HOUSE, 0);
        }
        return -1;
    }
}
