package net.petersil98.spatula.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.spatula.http.TfTAPI;
import net.petersil98.spatula.model.league.HyperRollEntry;
import net.petersil98.stcommons.constants.LeaguePlatform;
import net.petersil98.stcommons.constants.RankedQueue;
import net.petersil98.stcommons.model.league.RankEntry;

import java.util.List;

public class PlayerRanks {

    private final String summonerId;
    private final LeaguePlatform platform;
    private RankEntry rankDoubleUp, rankTft;
    private HyperRollEntry rankHyperRoll;

    public PlayerRanks(String summonerId, LeaguePlatform platform) {
        this.summonerId = summonerId;
        this.platform = platform;
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
