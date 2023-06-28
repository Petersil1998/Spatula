package net.petersil98.spatula.model;

import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.core.constant.RankedDivision;
import net.petersil98.core.constant.RankedQueue;
import net.petersil98.core.constant.RankedTier;
import net.petersil98.core.http.RiotAPIRequest;
import net.petersil98.spatula.model.league.HyperRollLadderEntry;
import net.petersil98.stcommons.model.league.AbstractEntry;
import net.petersil98.stcommons.model.league.HyperRollEntry;
import net.petersil98.stcommons.model.league.League;
import net.petersil98.stcommons.model.league.RankEntry;

import java.util.List;
import java.util.Map;

public class TfTRanked {

    private final String summonerId;
    private RankEntry rankDoubleUp, rankTft;
    private HyperRollEntry rankHyperRoll;

    private TfTRanked(String summonerId) {
        this.summonerId = summonerId;
    }

    public static TfTRanked getLoLRanksOfSummoner(String summonerId) {
        return new TfTRanked(summonerId);
    }

    public static List<HyperRollLadderEntry> getTopHyperRollPlayers() {
        return RiotAPIRequest.requestTftLeagueEndpoint(String.format("rated-ladders/%s/top", RankedQueue.HYPER_ROLL.getJsonPropertyValue()),
                TypeFactory.defaultInstance().constructCollectionType(List.class, HyperRollLadderEntry.class));
    }

    public static League getTftLeagueById(String id) {
        return RiotAPIRequest.requestTftLeagueEndpoint("leagues/" + id, League.class);
    }

    public static League getMasterLeague() {
        return RiotAPIRequest.requestTftLeagueEndpoint("master", League.class);
    }

    public static League getGrandmasterLeague() {
        return RiotAPIRequest.requestTftLeagueEndpoint("grandmaster", League.class);
    }

    public static League getChallengerLeague() {
        return RiotAPIRequest.requestTftLeagueEndpoint("challenger", League.class);
    }

    public static List<RankEntry> getTftRankEntries(RankedDivision division, RankedTier tier) {
        return getTftRankEntries(division, tier, 1);
    }

    public static List<RankEntry> getTftRankEntries(RankedDivision division, RankedTier tier, int pageNumber) {
        return RiotAPIRequest.requestTftLeagueEndpoint(String.format("entries/%s/%s", tier.name(), division.name()),
                TypeFactory.defaultInstance().constructCollectionType(List.class, RankEntry.class), Map.of("page", String.valueOf(pageNumber)));
    }

    private void initRanks() {
        if(this.rankTft == null || this.rankHyperRoll == null || this.rankDoubleUp == null) {
            List<AbstractEntry> ranks = RiotAPIRequest.requestTftLeagueEndpoint("entries/by-summoner/" + this.summonerId, TypeFactory.defaultInstance().constructCollectionType(List.class, AbstractEntry.class));
            ranks.addAll(RiotAPIRequest.requestLoLLeagueEndpoint("entries/by-summoner/" + this.summonerId, TypeFactory.defaultInstance().constructCollectionType(List.class, RankEntry.class)));
            this.rankTft = ranks.stream().filter(rank -> rank.getQueueType().equals(RankedQueue.TFT)).map(entry -> ((RankEntry) entry)).findFirst().orElse(RankEntry.UNRANKED);
            this.rankHyperRoll = ranks.stream().filter(rank -> rank.getQueueType().equals(RankedQueue.HYPER_ROLL)).map(entry -> ((HyperRollEntry) entry)).findFirst().orElse(HyperRollEntry.UNRANKED);
            this.rankDoubleUp = ranks.stream().filter(rank -> rank.getQueueType().equals(RankedQueue.DOUBLE_UP)).map(entry -> ((RankEntry) entry)).findFirst().orElse(RankEntry.UNRANKED);
        }
    }

    public RankEntry getRankTft() {
        this.initRanks();
        return this.rankTft;
    }

    public RankEntry getRankDoubleUp() {
        this.initRanks();
        return this.rankDoubleUp;
    }

    public HyperRollEntry getRankHyperRoll() {
        this.initRanks();
        return this.rankHyperRoll;
    }
}
