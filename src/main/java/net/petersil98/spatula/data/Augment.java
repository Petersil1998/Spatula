package net.petersil98.spatula.data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Augment {
    private final String id;
    private final String name;
    private final List<Trait> associatedTraits;
    private final String desc;
    private final Map<String, Float> effects;
    private final String image;

    public Augment(String id, String name, List<Trait> associatedTraits, String desc, Map<String, Float> effects, String image) {
        this.id = id;
        this.name = name;
        this.associatedTraits = associatedTraits;
        this.desc = desc;
        this.effects = effects;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Trait> getAssociatedTraits() {
        return associatedTraits;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Augment augment = (Augment) o;
        return Objects.equals(id, augment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
