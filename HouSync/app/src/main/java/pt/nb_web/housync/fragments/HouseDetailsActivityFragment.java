package pt.nb_web.housync.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.nb_web.housync.R;
import pt.nb_web.housync.activities.HouseDetailsActivity;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.exception.HouseNotFoundException;
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

        View view;
        final int houseLocalId = getHouseLocalId();

        if(houseLocalId >= 1){
            view = inflater.inflate(R.layout.fragment_house_details, container, false);
            try {
                setViewFields(view, houseLocalId);

                view.findViewById(R.id.delete_house).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onHouseDeleted(houseLocalId);
                    }
                });
            } catch (HouseNotFoundException e) {
                Log.d("HouseDetailsFragment", "House not found: " + Integer.toString(houseLocalId));
                e.printStackTrace();
                return view;
            }

        }else{
            view = inflater.inflate(R.layout.fragment_no_house_details, container, false);
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

    private void setViewFields(View view, final int houseLocalId) throws HouseNotFoundException {
        TextView nameTextView = (TextView) view.findViewById(R.id.house_fragment_house_name);
        HouseService houseService = HouseService.getInstance(this.getContext());

        house = houseService.getHouse(houseLocalId);

        if (getActivity() instanceof HouseDetailsActivity){
            getActivity().setTitle(house.getHouseName());
            if (nameTextView != null)
                ((ViewGroup) nameTextView.getParent()).removeView(nameTextView);
        }else{
            nameTextView.setText(house.getHouseName());
        }
    }

    public void updateHouse(int houseLocalId) {
        try {
            setViewFields(getView(), houseLocalId);
        } catch (HouseNotFoundException e) {
            Log.d("HouseDetailsFragmentUpd", "House not found: " + Integer.toString(houseLocalId));
            e.printStackTrace();
        }
    }



    public interface Listener {
        public void onHouseDeleted(int houseLocalId);
    }
}
