package pt.nb_web.housync.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.activities.HouseDetailsActivity;
import pt.nb_web.housync.adapter.HouseRecyclerAdapter;
import pt.nb_web.housync.adapter.UserHouseRecyclerAdapter;
import pt.nb_web.housync.background.UpdateHouseListAsyncTask;
import pt.nb_web.housync.data.house.HouseCursor;
import pt.nb_web.housync.data.house.HouseDBContract;
import pt.nb_web.housync.data.house.HouseSQLiteRepository;
import pt.nb_web.housync.exception.HouseNotFoundException;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.model.User;
import pt.nb_web.housync.service.HouseService;
import pt.nb_web.housync.service.sign_in.UserLogIn;
import pt.nb_web.housync.utils.Commons;
import pt.nb_web.housync.utils.NetworkHelper;
import pt.nb_web.housync.utils.RecyclerItemClickListener;

import static pt.nb_web.housync.data.house.HouseSQLiteRepository.READ_PERMISSION;

/**
 * A placeholder fragment containing a simple view.
 */
public class HouseDetailsActivityFragment extends Fragment {
    private static final String TAG ="HOUSE_DETAILS_FRAGMENT";

    private HouseService houseService;
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
        if (house != null) {
            return house.getHouseLocalId();
        }else if(getArguments() != null){
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
        TextView lastSyncTextView = (TextView) view.findViewById(R.id.last_sync_text_view);
        HouseService houseService = HouseService.getInstance(this.getContext());

        house = houseService.getHouse(houseLocalId);

        if (getActivity() instanceof HouseDetailsActivity){
            getActivity().setTitle(house.getHouseName());
            if (nameTextView != null)
                ((ViewGroup) nameTextView.getParent()).removeView(nameTextView);
        }else{
            nameTextView.setText(house.getHouseName());
        }
        lastSyncTextView.setText(house.getLastSync());

        List<String> usersList = new ArrayList<>();
        usersList.add(UserLogIn.getInstance(getContext()).getUserName());
        for (User user: houseService.getUsers(house.getHouseId())) {
            usersList.add(user.getName());
        }

        ListView listView = (ListView) view.findViewById(R.id.house_details_list_hosts);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        if (adapter != null){
            adapter.clear();
            adapter.addAll(usersList);
            adapter.notifyDataSetChanged();
        }else{
            adapter = new ArrayAdapter<>(this.getContext(), R.layout.item_user_house, R.id.item_user_name, usersList);
            listView.setAdapter(adapter);
        }
        setListViewHeightBasedOnChildren(listView);
    }

    public void updateHouse(int houseLocalId) {
        try {
            setViewFields(getView(), houseLocalId);
        } catch (HouseNotFoundException e) {
            Log.d("HouseDetailsFragmentUpd", "House not found: " + Integer.toString(houseLocalId));
            e.printStackTrace();
        }
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



    public interface Listener {
        public void onHouseDeleted(int houseLocalId);
    }
}
