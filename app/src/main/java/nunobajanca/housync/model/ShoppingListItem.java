package nunobajanca.housync.model;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.equals;

/**
 * Created by Nuno on 06/01/2016.
 */
public class ShoppingListItem {
    private String name;
    private boolean checked;

    public ShoppingListItem(String itemName, Boolean itemChecked) {
        this.name = itemName;
        this.checked = itemChecked;
    }

    public ShoppingListItem(String itemName){
        this.name = itemName;
        this.checked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object object){
        if(object instanceof ShoppingListItem){
            ShoppingListItem item = (ShoppingListItem)object;
            return Objects.equals(this.getName(),item.getName());
        }
        return false;
    }

    public static List<ShoppingListItem> ShoppingList(){

        List<ShoppingListItem> ShoppingList = new ArrayList<>();
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça vamos experimentar uma coisa gigante para ver se muda de linha ou se fica tudo na mesma"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));
        ShoppingList.add(new ShoppingListItem("Detergente Roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça"));
        ShoppingList.add(new ShoppingListItem("Leite"));
        ShoppingList.add(new ShoppingListItem("Jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço"));

        return ShoppingList;

    }
}
