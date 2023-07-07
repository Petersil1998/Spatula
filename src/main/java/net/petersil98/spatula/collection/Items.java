package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items {

    private static Map<String, Item> items;

    public static Item getItem(String id){
        return items.get(id);
    }

    public static List<Item> getItems() {
        return new ArrayList<>(items.values());
    }
}
