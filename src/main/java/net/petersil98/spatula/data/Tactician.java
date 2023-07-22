package net.petersil98.spatula.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.petersil98.spatula.collection.Tacticians;
import net.petersil98.spatula.model.Deserializers;
import net.petersil98.spatula.util.TfTLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(using = Deserializers.TacticianDeserializer.class)
public class Tactician {

    private final int id;
    private final String contentId;
    private final int tier;
    private final String name;
    private final String image;
    private final String description;
    private final String speciesName;
    private final int speciesId;
    private final Rarity rarity;
    private final boolean isDefault;
    private final boolean tftOnly;
    private final List<Tactician> upgrades;

    public Tactician(int id, String contentId, int tier, String name, String image, String description, String speciesName, int speciesId, Rarity rarity, boolean isDefault, boolean tftOnly) {
        this.id = id;
        this.contentId = contentId;
        this.tier = tier;
        this.name = name;
        this.image = image;
        this.description = description;
        this.speciesName = speciesName;
        this.speciesId = speciesId;
        this.rarity = rarity;
        this.isDefault = isDefault;
        this.tftOnly = tftOnly;
        this.upgrades = new ArrayList<>();
    }

    public void postInit() {
        if(TfTLoader.TACTICIANS_UPGRADES.containsKey(this.contentId)) {
            TfTLoader.TACTICIANS_UPGRADES.get(this.contentId).forEach(contentId -> this.upgrades.add(Tacticians.getTacticianByContentId(contentId)));
        }
    }

    public int getId() {
        return id;
    }

    public String getContentId() {
        return contentId;
    }

    public int getTier() {
        return tier;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isTftOnly() {
        return tftOnly;
    }

    public List<Tactician> getUpgrades() {
        return upgrades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tactician tactician = (Tactician) o;
        return id == tactician.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum Rarity {
        @JsonProperty("Default")
        DEFAULT(0),
        @JsonProperty("Epic")
        EPIC(1),
        @JsonProperty("Legendary")
        LEGENDARY(2),
        @JsonProperty("Mythic")
        MYTHIC(3);

        final int value;

        Rarity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
