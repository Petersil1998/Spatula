package net.petersil98.spatula.model;

import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.core.constant.Platform;
import net.petersil98.core.constant.RankedDivision;
import net.petersil98.core.constant.RankedQueue;
import net.petersil98.core.constant.RankedTier;
import net.petersil98.core.http.RiotAPI;
import net.petersil98.spatula.model.league.HyperRollLadderEntry;
import net.petersil98.stcommons.model.league.AbstractEntry;
import net.petersil98.stcommons.model.league.HyperRollEntry;
import net.petersil98.stcommons.model.league.League;
import net.petersil98.stcommons.model.league.RankEntry;

import java.util.List;
import java.util.Map;

public class TfTRanked {

    private final String summonerId;
    private final Platform platform;
    private RankEntry rankDoubleUp, rankTft;
    private HyperRollEntry rankHyperRoll;

    private TfTRanked(String summonerId, Platform platform) {
        this.summonerId = summonerId;
        this.platform = platform;
    }

    public static TfTRanked getLoLRanksOfSummoner(String summonerId, Platform platform) {
        return new TfTRanked(summonerId, platform);
    }

    public static List<HyperRollLadderEntry> getTopHyperRollPlayers(Platform platform) {
        return RiotAPI.requestTftLeagueEndpoint("rated-ladders/", String.format("%s/top", RankedQueue.HYPER_ROLL.getJsonPropertyValue()),
                platform, TypeFactory.defaultInstance().constructCollectionType(List.class, HyperRollLadderEntry.class));
    }

    public static League getTftLeagueById(String id, Platform platform) {
        return RiotAPI.requestTftLeagueEndpoint("leagues/", id, platform, League.class);
    }

    public static League getMasterLeague(Platform platform) {
        return RiotAPI.requestTftLeagueEndpoint("master", "", platform, League.class);
    }

    public static League getGrandmasterLeague(Platform platform) {
        return RiotAPI.requestTftLeagueEndpoint("grandmaster", "", platform, League.class);
    }

    public static League getChallengerLeague(Platform platform) {
        return RiotAPI.requestTftLeagueEndpoint("challenger", "", platform, League.class);
    }

    public static List<RankEntry> getTftRankEntries(RankedDivision division, RankedTier tier, Platform platform) {
        return getTftRankEntries(division, tier, platform, 1);
    }

    public static List<RankEntry> getTftRankEntries(RankedDivision division, RankedTier tier, Platform platform, int pageNumber) {
        return RiotAPI.requestTftLeagueEndpoint("entries/", String.format("%s/%s", tier.name(), division.name()), platform,
                TypeFactory.defaultInstance().constructCollectionType(List.class, RankEntry.class), Map.of("page", String.valueOf(pageNumber)));
    }

    private void initRanks() {
        if(this.rankTft == null || this.rankHyperRoll == null || this.rankDoubleUp == null) {
            List<AbstractEntry> ranks = RiotAPI.requestTftLeagueEndpoint("entries/by-summoner/", this.summonerId, this.platform, TypeFactory.defaultInstance().constructCollectionType(List.class, AbstractEntry.class));
            ranks.addAll(RiotAPI.requestLoLLeagueEndpoint("entries/by-summoner/", this.summonerId, this.platform, TypeFactory.defaultInstance().constructCollectionType(List.class, RankEntry.class)));
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
