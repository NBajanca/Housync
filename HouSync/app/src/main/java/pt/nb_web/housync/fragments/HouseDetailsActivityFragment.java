package pt.nb_web.housync.fragments;

import android.content.Context;
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
import pt.nb_web.housync.utils.Commons;

/**
 * A placeholder fragment containing a simple view.
 */
public class HouseDetailsActivityFragment extends Fragment {
    private House house;
    private Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_house_details, container, false);

        TextView nameTextView = (TextView) view.findViewById(R.id.house_fragment_house_name);

        final int houseLocalId = getHouseLocalId();
        if(houseLocalId >= 1){
            HouseService houseService = HouseService.getInstance(view.getContext());

            house = new House(houseLocalId);
            house = houseService.getHouse(house);

            nameTextView.setText(house.getHouseName());

            view.findViewById(R.id.delete_house).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHouseDeleted(house);
                }
            });

        }else{
            nameTextView.setText("Select a House");
        }

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

    public int getHouseLocalId() {
        Bundle bundle = getArguments();
        if(getArguments() != null){
            return getArguments().getInt(Commons.HOUSE_LOCAL_ID_PARAMETER, 0);
        }
        return -1;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public interface Listener {
        public void onHouseDeleted(House house);
    }
}
