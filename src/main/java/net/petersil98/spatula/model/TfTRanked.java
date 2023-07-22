package net.petersil98.spatula.model;

import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.spatula.http.TfTAPI;
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

    public static PlayerRanks getTfTRanksOfSummoner(String summonerId, LeaguePlatform platform) {
        return new PlayerRanks(summonerId, platform);
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
}
