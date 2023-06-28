package net.petersil98.spatula.model.league;

import net.petersil98.stcommons.model.Summoner;
import net.petersil98.stcommons.model.league.RatedTier;

public class HyperRollLadderEntry {
    private String summonerId;
    private String summonerName;
    private Summoner summoner;
    private int wins;
    private RatedTier ratedTier;
    private int ratedRating;
    private int previousUpdateLadderPosition;

    public String getSummonerId() {
        return summonerId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public Summoner getSummoner() {
        if(this.summoner == null) this.summoner = Summoner.getSummonerByID(this.getSummonerId());
        return this.summoner;
    }

    public int getWins() {
        return wins;
    }

    public RatedTier getRatedTier() {
        return ratedTier;
    }

    public int getRatedRating() {
        return ratedRating;
    }

    public int getPreviousUpdateLadderPosition() {
        return previousUpdateLadderPosition;
    }
}
