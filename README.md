# Spatula

Spatula is an Object-Oriented Java Library, which takes over the Communication with the Teamfights Tactics API. It supports In-Memory caching and uses a (blocking) Rate Limiter. It makes retrieving Summoner Data, Match History,
etc. much easier.

Other Projects:
- [Thresh](https://github.com/Petersil1998/Thresh) for League of Legends
- [Scuttlegeist](https://github.com/Petersil1998/Scuttlegeist) for Legends of Runeterra
- [Fade](https://github.com/Petersil1998/Fade) for Valorant

## Usage

Spatula can be included like this using **Gradle**:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
  ...
  implementation 'com.github.Petersil1998:Core:v1.3'
  implementation 'com.github.Petersil1998:STCommons:v1.3'
  implementation 'com.github.Petersil1998:Spatula:v1.1'
}
```

or using **Maven**:

```XML
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.Petersil1998</groupId>
  <artifactId>Core</artifactId>
  <version>v1.3</version>
</dependency>
<dependency>
  <groupId>com.github.Petersil1998</groupId>
  <artifactId>STCommons</artifactId>
  <version>v1.3</version>
  </dependency>
<dependency>
  <groupId>com.github.Petersil1998</groupId>
  <artifactId>Spatula</artifactId>
  <version>v1.1</version>
</dependency>
```

## Setup

In Order for Spatula to work properly there are a few things you need to set up
at the beginning of your application.

```JAVA
public class Example {
    public static void main(String[] args) {
        // First we need to provide our Riot API Key. Ideally the API Key is encrypted
        Settings.setAPIKey(() -> EncryptionUtil.encrypt(System.getenv("API_KEY")));
        // If the provided API Key is encrypted, we need to provide a function to decrypt the API Key
        Settings.setDecryptor(EncryptionUtil::decrypt);
        // We also need to provide a language. The language is used to static Data like Champions, Item, etc.
        Settings.setLanguage(Language.EN_US);
        // If we want to use caching we can enable it in the Settings. Caching is disabled by default
        Settings.useCache(true);
        // We also need to add the Loader for the static TfT Data
        Loader.addLoader(new TfTLoader());
        // Lastly we need to initialize the static Data
        Loader.init();
    }
}
```

Now Spatula is ready and set up!

## Examples

- **Summoner and Account Data**

    ```JAVA
    public class Example {
        public static void main(String[] args) {
            // Setup code...
            
            Summoner faker = Summoner.getSummonerByName("Faker", LeaguePlatform.KR);
            int summonerLevel = faker.getSummonerLevel();
            // Get the URL for the profile Icon
            String profileIconURL = Util.getProfileIconURL(faker.getProfileIcon());
            // Get Account
            Account account = Account.getAccountByPuuid(faker.getPuuid(), LeagueRegion.ASIA);
            // Get the Tag (e.g. Faker#KR1)
            String tag = account.toString();
        }
    } 
    ```

- **Rank and Leagues**

    ```JAVA
    public class Example {
        public static void main(String[] args) {
            // Setup code...
            
            Summoner faker = Summoner.getSummonerByName("Faker", LeaguePlatform.KR);
            // Get TfT, Double Up and Hyper Roll Ranks
            PlayerRanks ranked = TfTRanked.getTfTRanksOfSummoner(faker.getId(), LeaguePlatform.KR);
            RankEntry tft = ranked.getRankTft();
            int lp = tft.getLeaguePoints();
            RankEntry doubleUp = ranked.getRankDoubleUp();
            HyperRollEntry hyperRoll = ranked.getRankHyperRoll();

            // Get Challenger TfT Players
            League challengers = TfTRanked.getChallengerLeague(LeaguePlatform.EUW);
            for(LeagueEntry leagueEntry: challengers.getEntries()) {
                // Get all players and their LP
                Summoner player = Summoner.getSummonerByID(leagueEntry.getSummonerId(), LeaguePlatform.EUW);
                int playerLp = leagueEntry.getLeaguePoints();
            }

            // Get a list of Gold 1 TfT Players
            List<RankEntry> firstPage = TfTRanked.getTftRankEntries(RankedDivision.I, RankedTier.GOLD, LeaguePlatform.NA);
            List<RankEntry> secondPage = TfTRanked.getTftRankEntries(RankedDivision.I, RankedTier.GOLD, LeaguePlatform.NA, 2);
        }
    } 
    ```

- **Match History**

    ```JAVA
    public class Example {
        public static void main(String[] args) {
            // Setup code...
            
            Summoner faker = Summoner.getSummonerByName("Faker", LeaguePlatform.NA);
            // Get the Player's Match History. The Seconds Parameter is a Filter.
            List<MatchDetails> matchHistory = MatchDetails.getMatchHistory(faker.getId(), LeagueRegion.ASIA, Map.of());
            MatchDetails latestGame = matchHistory.get(0);
            int duration = latestGame.getGameLength();
            // Get Set Number
            int setNumber = latestGame.getTftSetNumber();
            // Get all the Players
            List<Participant> participants = latestGame.getParticipants();
            for(Participant p: participants) {
                // Get the Companion used by the Player
                Companion companion = p.getCompanion();
                // Get the in-game Level at the end of the game
                int level = p.getLevel();
                // Get Number of Players eliminated
                int playersEliminated = p.getPlayerEliminated();
                // Get Units and Traits
                List<Participant.UnitData> units = p.getUnits();
                List<Participant.TraitData> traits = p.getTraits();
            }
        } 
    } 
    ```
  The Match History Filter can have the following values:

  | Key       | Description                                                                                                                                                        | Type   |
  |-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------|
  | startTime | Unix Timestamp in seconds. Only start Times after June 16th 2021 will have an effect. [More](https://developer.riotgames.com/apis#match-v5/GET_getMatchIdsByPUUID) | long   | 
  | endTime   | Unix Timestamp in seconds.                                                                                                                                         | long   |
  | start     | The offset of the first Match entry                                                                                                                                | int    |
  | count     | The Amount of Matches entries to return. Has to be between positive.                                                                                               | int    |

  **Note**: *All values need to be passed as **Strings** in the filter*


- **Collections**

    The package [collection](https://github.com/Petersil1998/Spatula/blob/main/src/main/java/net/petersil98/spatula/collection/) contains a bunch of Collections for static Data including:
  
    - Augments
    - Items
    - Tacticians
    - Traits
    - Units
    - QueueTypes

### Feel free to give Feedback and add suggestions on how this library can be improved. <br>Thank you for using Spatula, you're awesome!
