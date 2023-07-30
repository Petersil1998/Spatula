package net.petersil98.spatula.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.petersil98.spatula.model.Deserializers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonDeserialize(using = Deserializers.TraitDeserializer.class)
public class Trait {

    @JsonProperty("apiName")
    private final String id;
    private final String desc;
    private final List<Effect> effects;
    private final String name;
    private final String image;

    public Trait(String id, String desc, List<Effect> effects, String name, String image) {
        this.id = id;
        this.desc = desc;
        this.effects = effects;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public static class Effect {

        private int maxUnits;
        private int minUnits;
        private int style;
        private Map<String, Float> variables;

        public int getMaxUnits() {
            return maxUnits;
        }

        public int getMinUnits() {
            return minUnits;
        }

        public int getStyle() {
            return style;
        }

        public Map<String, Float> getVariables() {
            return variables;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trait trait = (Trait) o;
        return Objects.equals(id, trait.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
