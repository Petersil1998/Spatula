package net.petersil98.spatula;

import net.petersil98.core.util.EncryptionUtil;
import net.petersil98.core.util.Loader;
import net.petersil98.core.util.settings.Language;
import net.petersil98.core.util.settings.Settings;
import net.petersil98.spatula.collection.Tacticians;
import net.petersil98.spatula.data.Tactician;
import net.petersil98.spatula.model.PlayerRanks;
import net.petersil98.spatula.model.TfTRanked;
import net.petersil98.spatula.model.match.MatchDetails;
import net.petersil98.spatula.util.TfTLoader;
import net.petersil98.stcommons.constants.LeaguePlatform;
import net.petersil98.stcommons.constants.LeagueRegion;
import net.petersil98.stcommons.model.Summoner;
import net.petersil98.stcommons.model.league.League;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class Spatula {

    public static final Logger LOGGER = LogManager.getLogger(Spatula.class);

    public static void main(String[] args) {
        Settings.setAPIKey(() -> EncryptionUtil.encrypt(System.getenv("API_KEY")));
        Settings.setDecryptor(EncryptionUtil::decrypt);
        Settings.setLanguage(Language.EN_US);
        Loader.addLoader(new TfTLoader());
        Loader.init();
        Summoner me = Summoner.getSummonerByName("meapok", LeaguePlatform.EUW);
        PlayerRanks myRanks = TfTRanked.getTfTRanksOfSummoner(me.getId(), LeaguePlatform.EUW);
        League masters = TfTRanked.getMasterLeague(LeaguePlatform.EUNE);
        Tactician tactician = Tacticians.getTactician(9016);
        List<MatchDetails> details = MatchDetails.getMatchHistory(me.getPuuid(), LeagueRegion.EUROPE, Map.of());
        System.out.println(me);
    }
}
