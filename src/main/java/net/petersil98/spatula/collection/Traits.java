package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Trait;

import java.util.List;

public class Traits {

    private static List<Trait> traits;

    public static Trait getTrait(String id){
        return traits.stream().filter(trait -> trait.getId().equals(id)).findAny().orElse(null);
    }

    public static Trait getTraitByIdOrName(String id) {
        return traits.stream().filter(trait -> trait.getId().equals(id)).findAny().orElseGet(() -> getTraitByName(id));
    }

    public static Trait getTraitByName(String name) {
        return traits.stream().filter(trait -> trait.getName().equals(name)).findAny().orElse(null);
    }

    public static List<Trait> getTraits() {
        return traits;
    }
}
