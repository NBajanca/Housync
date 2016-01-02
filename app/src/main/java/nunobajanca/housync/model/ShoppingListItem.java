package nunobajanca.housync.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nuno on 26/12/2015.
 */
public class ShoppingListItem {


    private String name;
    private String id;

    public ShoppingListItem(){}

    public ShoppingListItem(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof ShoppingListItem){
            ShoppingListItem item = (ShoppingListItem)object;
            return this.getId().equals(item.getId());
        }
        return false;
    }

    public static List<ShoppingListItem> ShoppingList(){

        List<ShoppingListItem> ShoppingList = new ArrayList<>();
        ShoppingList.add(new ShoppingListItem("Detergente Roupa", "detergente_roupa"));
        ShoppingList.add(new ShoppingListItem("Detergente Loiça", "detergente_loica"));
        ShoppingList.add(new ShoppingListItem("Leite", "leite"));
        ShoppingList.add(new ShoppingListItem("Jantar", "jantar"));
        ShoppingList.add(new ShoppingListItem("Almoço", "almoco"));

        return ShoppingList;

    }
}
