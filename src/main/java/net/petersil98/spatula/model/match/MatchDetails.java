package net.petersil98.spatula.model.match;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.core.constant.Region;
import net.petersil98.core.http.RiotAPI;
import net.petersil98.spatula.data.QueueType;
import net.petersil98.spatula.model.Deserializers;
import net.petersil98.spatula.util.Util;

import java.util.List;

@JsonDeserialize(using = Deserializers.MatchDetailsDeserializer.class)
public class MatchDetails {

    private final long gameDatetime;
    private final int gameLength;
    private final String gameVariation;
    private final String gameVersion;
    private final List<Participant> participants;
    private final QueueType queueType;
    private final String tftGameType;
    private final int tftSetNumber;

    public MatchDetails(long gameDatetime, int gameLength, String gameVariation, String gameVersion, List<Participant> participants, QueueType queueType, String tftGameType, int tftSetNumber) {
        this.gameDatetime = gameDatetime;
        this.gameLength = gameLength;
        this.gameVariation = gameVariation;
        this.gameVersion = gameVersion;
        this.participants = participants;
        this.queueType = queueType;
        this.tftGameType = tftGameType;
        this.tftSetNumber = tftSetNumber;
    }

    public static MatchDetails getMatchDetails(String matchId, Region region) {
        return RiotAPI.requestTftMatchEndpoint("matches/", matchId, region, MatchDetails.class);
    }

    public static List<MatchDetails> getMatchHistory(String puuid, Region region, java.util.Map<String, String> filter) {
        Util.validateFilter(filter);
        List<String> matchIds = RiotAPI.requestTftMatchEndpoint("matches/by-puuid/", puuid + "/ids", region, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class), filter);
        return matchIds.stream().map(matchId -> getMatchDetails(matchId, region)).toList();
    }

    public long getGameDatetime() {
        return gameDatetime;
    }

    public int getGameLength() {
        return gameLength;
    }

    public String getGameVariation() {
        return gameVariation;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public QueueType getQueueType() {
        return queueType;
    }

    public String getTftGameType() {
        return tftGameType;
    }

    public int getTftSetNumber() {
        return tftSetNumber;
    }
}
