package net.petersil98.spatula.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Trait {

    @JsonProperty("apiName")
    private String id;
    private String desc;
    private List<Effect> effects;
    private String name;
    @JsonProperty("icon")
    private String image;

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
