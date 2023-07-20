package net.petersil98.spatula.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.spatula.http.TfTAPI;
import net.petersil98.spatula.model.league.HyperRollEntry;
import net.petersil98.spatula.model.league.HyperRollLadderEntry;
import net.petersil98.stcommons.constants.LeaguePlatform;
import net.petersil98.stcommons.constants.RankedDivision;
import net.petersil98.stcommons.constants.RankedQueue;
import net.petersil98.stcommons.constants.RankedTier;
import net.petersil98.stcommons.model.league.League;
import net.petersil98.stcommons.model.league.RankEntry;

import java.util.List;
import java.util.Map;

public class TfTRanked {

    private final String summonerId;
    private final LeaguePlatform platform;
    private RankEntry rankDoubleUp, rankTft;
    private HyperRollEntry rankHyperRoll;

    private TfTRanked(String summonerId, LeaguePlatform platform) {
        this.summonerId = summonerId;
        this.platform = platform;
    }

    public static TfTRanked getLoLRanksOfSummoner(String summonerId, LeaguePlatform platform) {
        return new TfTRanked(summonerId, platform);
    }

    public static List<HyperRollLadderEntry> getTopHyperRollPlayers(LeaguePlatform platform) {
        return TfTAPI.requestTftLeagueEndpoint("rated-ladders/", String.format("%s/top", RankedQueue.HYPER_ROLL.getJsonPropertyValue()),
                platform, TypeFactory.defaultInstance().constructCollectionType(List.class, HyperRollLadderEntry.class));
    }

    public static League getTftLeagueById(String id, LeaguePlatform platform) {
        return TfTAPI.requestTftLeagueEndpoint("leagues/", id, platform, League.class);
    }

    public static League getMasterLeague(LeaguePlatform platform) {
        return TfTAPI.requestTftLeagueEndpoint("master", "", platform, League.class);
    }

    public static League getGrandmasterLeague(LeaguePlatform platform) {
        return TfTAPI.requestTftLeagueEndpoint("grandmaster", "", platform, League.class);
    }

    public static League getChallengerLeague(LeaguePlatform platform) {
        return TfTAPI.requestTftLeagueEndpoint("challenger", "", platform, League.class);
    }

    public static List<RankEntry> getTftRankEntries(RankedDivision division, RankedTier tier, LeaguePlatform platform) {
        return getTftRankEntries(division, tier, platform, 1);
    }

    public static List<RankEntry> getTftRankEntries(RankedDivision division, RankedTier tier, LeaguePlatform platform, int pageNumber) {
        return TfTAPI.requestTftLeagueEndpoint("entries/", String.format("%s/%s", tier.name(), division.name()), platform,
                TypeFactory.defaultInstance().constructCollectionType(List.class, RankEntry.class), Map.of("page", String.valueOf(pageNumber)));
    }

    private void initRanks() {
        if(this.rankTft == null || this.rankHyperRoll == null || this.rankDoubleUp == null) {
            List<JsonNode> rankNodes = TfTAPI.requestTftLeagueEndpoint("entries/by-summoner/", this.summonerId, this.platform, TypeFactory.defaultInstance().constructCollectionType(List.class, JsonNode.class));
            this.rankTft = Deserializers.getTfTRankEntryFromEntries(rankNodes);
            this.rankHyperRoll = Deserializers.getHyperRollEntryFromEntries(rankNodes);
            List<RankEntry> lolRanks = TfTAPI.requestLoLLeagueEndpoint("entries/by-summoner/", this.summonerId, this.platform, TypeFactory.defaultInstance().constructCollectionType(List.class, RankEntry.class));
            this.rankDoubleUp = lolRanks.stream().filter(rank -> rank.getQueueType().equals(RankedQueue.DOUBLE_UP)).findFirst().orElse(RankEntry.UNRANKED);
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
