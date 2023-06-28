package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Item;

import java.util.List;

public class Items {

    private static List<Item> items;

    public static Item getItem(String id){
        return items.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<Item> getItems() {
        return items;
    }
}
