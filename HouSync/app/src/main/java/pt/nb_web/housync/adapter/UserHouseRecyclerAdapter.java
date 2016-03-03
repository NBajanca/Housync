package pt.nb_web.housync.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.nb_web.housync.R;
import pt.nb_web.housync.model.User;

/**
 * Created by Nuno on 21/02/2016.
 */
public class UserHouseRecyclerAdapter extends RecyclerView.Adapter<UserHouseRecyclerAdapter.UserHouseRecyclerViewHolder>{
    private List<User> usersList;
    private Listener listener;

    public UserHouseRecyclerAdapter(List<User> usersList, UserHouseRecyclerAdapter.Listener activity){
        updateList(usersList);
        listener = activity;
    }


    public void updateList(List<User> usersList){
        this.usersList =  new ArrayList<>(usersList);
        notifyDataSetChanged();
    }

    public void removeItem(User item) {
        int position = getItemPosition(item);
        usersList.remove(position);

        if (position == 0 && usersList.size() == 0) {
            notifyDataSetChanged();
        }else
            notifyItemRemoved(position);
    }

    public void addItem(User item){
        usersList.add(item);

        notifyItemInserted(usersList.size());
    }

    public void updateItem(User item) {
        int position = getItemPosition(item);
        usersList.remove(position);
        usersList.add(position, item);

        notifyItemChanged(position);
    }

    public User getItem(int position) {
        return usersList.get(position);
    }

    public int getItemPosition(User item){
        for (User user: usersList) {
            if (user.equals(item)){
                return usersList.indexOf(user);
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @Override
    public UserHouseRecyclerAdapter.UserHouseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.recycler_item_user_house, parent, false);

        return new UserHouseRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserHouseRecyclerAdapter.UserHouseRecyclerViewHolder holder, final int position) {
        User item = getItem(position);
        final int userId = item.getUserId();

        holder.vName.setText(item.getName());
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserDeleted(userId);
            }
        });

    }


    public static class UserHouseRecyclerViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected ImageView vDelete;

        public UserHouseRecyclerViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.house_host_name);
            vDelete = (ImageView) itemView.findViewById(R.id.delete_house_host);
        }


    }

    public interface Listener {
        public void onUserDeleted(int userID);
    }

}