package nunobajanca.housync.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nunobajanca.housync.R;

/**
 * Created by Nuno on 15/12/2015.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        String title = getResources().getString(R.string.nav_home);

        getActivity().setTitle(title);
        return rootView;
    }
}
