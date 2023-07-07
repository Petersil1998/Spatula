package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Trait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Traits {

    private static Map<String, Trait> traits;

    public static Trait getTrait(String id){
        return traits.get(id);
    }

    public static Trait getTraitByIdOrName(String id) {
        return getTraits().stream().filter(trait -> trait.getId().equals(id)).findAny().orElseGet(() -> getTraitByName(id));
    }

    public static Trait getTraitByName(String name) {
        return getTraits().stream().filter(trait -> trait.getName().equals(name)).findAny().orElse(null);
    }

    public static List<Trait> getTraits() {
        return new ArrayList<>(traits.values());
    }
}
