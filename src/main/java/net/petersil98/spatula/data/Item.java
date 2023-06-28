package net.petersil98.spatula.data;

import net.petersil98.spatula.collection.Items;
import net.petersil98.spatula.util.TftLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Item {

    private final String id;
    private final List<Item> composition;
    private final String desc;
    private final Map<String, Float> effects;
    private final String image;
    private final List<Trait> incompatibleTraits;

    // FIXME: 18.06.2023 Some Traits arent in community dragon's JSON, so i just store the Strings in case the Trait isn't found
    private final List<String> incompatibleTraitsString;
    private final String name;
    private final boolean unique;

    public Item(String id, String desc, Map<String, Float> effects, String image, List<Trait> incompatibleTraits, List<String> incompatibleTraitsString, String name, boolean unique) {
        this.id = id;
        this.composition = new ArrayList<>();
        this.desc = desc;
        this.effects = effects;
        this.image = image;
        this.incompatibleTraits = incompatibleTraits;
        this.incompatibleTraitsString = incompatibleTraitsString;
        this.name = name;
        this.unique = unique;
    }

    public void postInit() {
        if(TftLoader.ITEMS_COMPOSITIONS.containsKey(this.id)) {
            TftLoader.ITEMS_COMPOSITIONS.get(this.id).forEach(id -> this.composition.add(Items.getItem(id)));
        }
    }

    public String getId() {
        return id;
    }

    public List<Item> getComposition() {
        return composition;
    }

    public String getDesc() {
        return desc;
    }

    public Map<String, Float> getEffects() {
        return effects;
    }

    public String getImage() {
        return image;
    }

    public List<Trait> getIncompatibleTraits() {
        return incompatibleTraits;
    }

    public List<String> getIncompatibleTraitsString() {
        return incompatibleTraitsString;
    }

    public String getName() {
        return name;
    }

    public boolean isUnique() {
        return unique;
    }
}
