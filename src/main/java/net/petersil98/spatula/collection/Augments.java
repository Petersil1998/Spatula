package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Augment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Augments {

    private static Map<Integer, Augment> augments;

    public static Augment getAugment(String id){
        return augments.get(id);
    }

    public static List<Augment> getAugments() {
        return new ArrayList<>(augments.values());
    }
}
