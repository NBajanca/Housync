package pt.nb_web.housync.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.activities.HouseDetailsActivity;
import pt.nb_web.housync.model.House;
import pt.nb_web.housync.service.HouseService;

/**
 * Created by Nuno on 21/02/2016.
 */
public class HouseRecyclerAdapter extends RecyclerView.Adapter<HouseRecyclerAdapter.HouseRecyclerViewHolder>{
    private List<House> housesList;
    private HouseService houseService;

    public final static String EXTRA_HOUSE = "pt.nb_web.housync.HOUSE";

    public HouseRecyclerAdapter(List<House> housesList){
        this.housesList =  new ArrayList<>(housesList);
    }

    public void updateList(List<House> housesList){
        this.housesList =  new ArrayList<>(housesList);
    }

    public void removeItem(House item) {
        int position = getItemPosition(item);
        housesList.remove(position);
        houseService.delete(item);

        if (position == 0 && housesList.size() == 0) {
            notifyDataSetChanged();
        }else
            notifyItemRemoved(position);
    }

    public void updateItem(House item) {
        int position = getItemPosition(item);
        housesList.remove(position);
        housesList.add(position, item);

        notifyItemChanged(position);
    }

    public House getItem(int position) {
        return housesList.get(position);
    }

    public int getItemPosition(House item){
        for (House house:housesList) {
            if (house.equals(item)){
                return housesList.indexOf(house);
            }

        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return housesList.size();
    }

    @Override
    public HouseRecyclerAdapter.HouseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        houseService = HouseService.getInstance(context);

        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.recycler_item_house, parent, false);

        return new HouseRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HouseRecyclerAdapter.HouseRecyclerViewHolder holder, final int position) {
        House item = getItem(position);
        RecyclerView.ViewHolder viewHolder = holder;

        holder.vName.setText(item.getHouseName());

        if(item.getHouseId() != 0){
            holder.vLastSync.setText(item.getLastSync());
        }
    }



    public static class HouseRecyclerViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected TextView vLastSync;

        public HouseRecyclerViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.recycler_item_house_name);
            vLastSync = (TextView) itemView.findViewById(R.id.recycler_item_house_last_sync);
        }


    }

}