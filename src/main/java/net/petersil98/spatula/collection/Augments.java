package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Augment;

import java.util.List;

public class Augments {

    private static List<Augment> augments;

    public static Augment getAugment(String id){
        return augments.stream().filter(augment -> augment.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<Augment> getAugments() {
        return augments;
    }
}
