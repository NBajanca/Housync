package nunobajanca.housync.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nunobajanca.housync.R;
import nunobajanca.housync.activities.MainActivity;
import nunobajanca.housync.activities.ShoppingListActivity;
import nunobajanca.housync.model.ShoppingListItem;
import nunobajanca.housync.service.ShoppingListService;

/**
 * Created by Nuno on 06/01/2016.
 */
public class ShoppingListRecyclerAdapter extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ShoppingListViewHolder> {
    private List<ShoppingListItem> shoppingList;
    private Context context;
    public ShoppingListViewHolder viewHolder;
    ShoppingListService shoppingListService;

    public ShoppingListRecyclerAdapter(List<ShoppingListItem> shoppingList){
        this.shoppingList =  new ArrayList<>(shoppingList);
    }



    private void addItem(ShoppingListItem item, int position) {
        shoppingList.add(position, item);
        shoppingListService.add(item);
        notifyItemInserted(position);
    }

    public void updateList(List<ShoppingListItem> shoppingList){
        this.shoppingList =  new ArrayList<>(shoppingList);
    }

    private void removeItem(ShoppingListItem item) {
        int position = getItemPosition(item);
        shoppingList.remove(position);
        shoppingListService.delete(item);

        if (position == 0 && shoppingList.size() == 0) {
            notifyDataSetChanged();
        }else
            notifyItemRemoved(position);
    }

    private void updateItem(ShoppingListItem item){
        shoppingListService.updateChecked(item);
    }

    private void onCheckBoxClick(ShoppingListViewHolder viewHolder, CompoundButton buttonView){
        ShoppingListItem item = (ShoppingListItem) viewHolder.vCheckBox.getTag();
        if (buttonView.isChecked())
            Toast.makeText(context, item.getName() + " " + context.getResources().getString(R.string.shopping_list_item_checked), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, item.getName() + " " + context.getResources().getString(R.string.shopping_list_item_unchecked), Toast.LENGTH_SHORT).show();

        item.setChecked(buttonView.isChecked());
        updateItem(item);
    }

    public ShoppingListItem getItem(int position) {
        return shoppingList.get(position);
    }

    public int getItemPosition(ShoppingListItem item){
        return shoppingList.indexOf(item);
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        shoppingListService = ShoppingListService.getInstance(context);

        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.item_shopping_list, parent, false);

        return new ShoppingListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShoppingListViewHolder holder, final int position) {
        ShoppingListItem item = shoppingList.get(position);
        viewHolder = holder;

        holder.vCheckBox.setChecked(item.getChecked());
        holder.vCheckBox.setTag(item);
        holder.vCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (buttonView.isPressed())
                            onCheckBoxClick(holder, buttonView);
                    }
                });

        holder.vName.setText(item.getName());

        holder.vImage.setTag(item);
        holder.vImage
                .setOnClickListener(new CompoundButton.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        ShoppingListItem item = (ShoppingListItem) holder.vCheckBox.getTag();
                        Toast.makeText(context, item.getName() + " Removed", Toast.LENGTH_SHORT).show();
                        removeItem(item);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }



    public ShoppingListItem get(int position){
        return shoppingList.get(position);
    }

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder{
        protected CheckBox vCheckBox;
        protected TextView vName;
        protected ImageView vImage;

        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            vCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_item_shopping_list);
            vName = (TextView) itemView.findViewById(R.id.name_item_shopping_list);
            vImage = (ImageView) itemView.findViewById(R.id.delete_item_shopping_list);
        }
    }

}
