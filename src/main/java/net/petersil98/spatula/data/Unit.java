package net.petersil98.spatula.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class Unit {

    private final Ability ability;
    private final String id;
    private final int cost;
    private final String image;
    private final String name;
    private final Stats stats;
    private final List<Trait> traits;

    public Unit(Ability ability, String id, int cost, String image, String name, Stats stats, List<Trait> traits) {
        this.ability = ability;
        this.id = id;
        this.cost = cost;
        this.image = image;
        this.name = name;
        this.stats = stats;
        this.traits = traits;
    }

    public Ability getAbility() {
        return ability;
    }

    public String getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Stats getStats() {
        return stats;
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public static class Ability {
        private String desc;
        @JsonProperty("icon")
        private String image;
        private String name;
        private List<Variable> variables;

        public String getDesc() {
            return desc;
        }

        public String getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

        public List<Variable> getVariables() {
            return variables;
        }
    }

    public static class Variable {
        private String name;
        @JsonProperty("value")
        private List<Float> values;

        public String getName() {
            return name;
        }

        public List<Float> getValues() {
            return values;
        }
    }

    public static class Stats {
        private int armor;
        private float attackSpeed;
        private float critChance;
        private float critMultiplier;
        private int damage;
        private int hp;
        private int initialMana;
        private int magicResist;
        private int mana;
        private int range;

        public int getArmor() {
            return armor;
        }

        public float getAttackSpeed() {
            return attackSpeed;
        }

        public float getCritChance() {
            return critChance;
        }

        public float getCritMultiplier() {
            return critMultiplier;
        }

        public int getDamage() {
            return damage;
        }

        public int getHp() {
            return hp;
        }

        public int getInitialMana() {
            return initialMana;
        }

        public int getMagicResist() {
            return magicResist;
        }

        public int getMana() {
            return mana;
        }

        public int getRange() {
            return range;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(id, unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
