package net.petersil98.spatula.model.league;

import net.petersil98.stcommons.constants.RankedQueue;

public class HyperRollEntry {

    public static final HyperRollEntry UNRANKED = new HyperRollEntry();

    private RankedQueue queueType;
    private String summonerId;
    private String summonerName;
    private int wins;
    private int losses;
    private RatedTier ratedTier;
    private int ratedRating;


    public RankedQueue getQueueType() {
        return queueType;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setRatedTier(RatedTier ratedTier) {
        this.ratedTier = ratedTier;
    }

    public void setRatedRating(int ratedRating) {
        this.ratedRating = ratedRating;
    }

    public RatedTier getRatedTier() {
        return ratedTier;
    }

    public int getRatedRating() {
        return ratedRating;
    }
}