package net.petersil98.spatula.model.league;

public class HyperRollLadderEntry {
    private String summonerId;
    private String summonerName;
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
