package net.petersil98.spatula.model.match;

import net.petersil98.spatula.data.Augment;
import net.petersil98.spatula.data.Item;
import net.petersil98.spatula.data.Trait;
import net.petersil98.spatula.data.Unit;
import net.petersil98.stcommons.model.Summoner;

import java.util.List;

public class Participant {
    private final List<Augment> augments;
    private final Companion companion;
    private final int goldLeft;
    private final int lastRound;
    private final int level;
    private final int placement;
    private final int partnerGroupId;
    private final int playerEliminated;
    private final String puuid;
    private Summoner summoner;
    private final float timeEliminated;
    private final int totalDamageToPlayers;
    private final List<TraitData> traits;
    private final List<UnitData> units;

    public Participant(List<Augment> augments, Companion companion, int goldLeft, int lastRound, int level, int placement, int partnerGroupId, int playerEliminated, String puuid, float timeEliminated, int totalDamageToPlayers, List<TraitData> traits, List<UnitData> units) {
        this.augments = augments;
        this.companion = companion;
        this.goldLeft = goldLeft;
        this.lastRound = lastRound;
        this.level = level;
        this.placement = placement;
        this.partnerGroupId = partnerGroupId;
        this.playerEliminated = playerEliminated;
        this.puuid = puuid;
        this.timeEliminated = timeEliminated;
        this.totalDamageToPlayers = totalDamageToPlayers;
        this.traits = traits;
        this.units = units;
    }

    public List<Augment> getAugments() {
        return augments;
    }

    public Companion getCompanion() {
        return companion;
    }

    public int getGoldLeft() {
        return goldLeft;
    }

    public int getLastRound() {
        return lastRound;
    }

    public int getLevel() {
        return level;
    }

    public int getPlacement() {
        return placement;
    }

    public int getPartnerGroupId() {
        return partnerGroupId;
    }

    public int getPlayerEliminated() {
        return playerEliminated;
    }

    public String getPuuid() {
        return puuid;
    }

    public Summoner getSummoner() {
        if(this.summoner == null) this.summoner = Summoner.getSummonerByPUUID(this.puuid);
        return summoner;
    }

    public float getTimeEliminated() {
        return timeEliminated;
    }

    public int getTotalDamageToPlayers() {
        return totalDamageToPlayers;
    }

    public List<TraitData> getTraits() {
        return traits;
    }

    public List<UnitData> getUnits() {
        return units;
    }

    public static class TraitData {
        private final Trait trait;
        private final int numUnits;
        private final Trait.Effect activeEffect;

        public TraitData(Trait trait, int numUnits, Trait.Effect activeEffect) {
            this.trait = trait;
            this.numUnits = numUnits;
            this.activeEffect = activeEffect;
        }

        public Trait getTrait() {
            return trait;
        }

        public int getNumUnits() {
            return numUnits;
        }

        public Trait.Effect getActiveEffect() {
            return activeEffect;
        }
    }

    public static class UnitData {

        private final Unit unit;
        private final List<Item> items;
        private final int rarity;
        private final int tier;

        public UnitData(Unit unit, List<Item> items, int rarity, int tier) {
            this.unit = unit;
            this.items = items;
            this.rarity = rarity;
            this.tier = tier;
        }

        public Unit getUnit() {
            return unit;
        }

        public List<Item> getItems() {
            return items;
        }

        public int getRarity() {
            return rarity;
        }

        public int getTier() {
            return tier;
        }
    }
}
