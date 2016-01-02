package nunobajanca.housync.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nunobajanca.housync.R;
import nunobajanca.housync.model.ShoppingListItem;

/**
 * Created by Nuno on 02/01/2016.
 */
public class ShoppingListRecyclerAdapter  extends RecyclerView.Adapter<ShoppingListRecyclerAdapter.ShoppingListViewHolder> {

    private List<ShoppingListItem> shoppingList;
    private Context context;

    public ShoppingListRecyclerAdapter(List<ShoppingListItem> shoppingList){
        this.shoppingList =  new ArrayList<>(shoppingList);
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.shopping_list_item, parent, false);

        return new ShoppingListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, int position) {
        ShoppingListItem item = shoppingList.get(position);
        holder.vName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public ShoppingListItem get(int position){
        return shoppingList.get(position);
    }

    public static class ShoppingListViewHolder extends RecyclerView.ViewHolder{

        protected TextView vName;

        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.shopping_list_item_name);
        }
    }
}
