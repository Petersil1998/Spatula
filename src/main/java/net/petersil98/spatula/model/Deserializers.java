package net.petersil98.spatula.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.spatula.collection.*;
import net.petersil98.spatula.data.*;
import net.petersil98.spatula.model.league.HyperRollEntry;
import net.petersil98.spatula.model.match.Companion;
import net.petersil98.spatula.model.match.MatchDetails;
import net.petersil98.spatula.model.match.Participant;
import net.petersil98.stcommons.constants.RankedQueue;
import net.petersil98.stcommons.data.Sprite;
import net.petersil98.stcommons.model.league.RankEntry;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class Deserializers {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static class MatchDetailsDeserializer extends JsonDeserializer<MatchDetails> {

        @Override
        public MatchDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);
            JsonNode info = root.get("info");
            List<Participant> participants = MAPPER.readerForListOf(Participant.class).readValue(info.get("participants"));
            return new MatchDetails(info.get("game_datetime").asLong(), info.get("game_length").asInt(),
                    info.has("game_variation") ? info.get("game_variation").asText() : null,
                    info.get("game_version").asText(), participants, QueueTypes.getQueueType(info.get("queue_id").asInt()),
                    info.get("tft_game_type").asText(), info.get("tft_set_number").asInt());
        }
    }

    public static class ParticipantDeserializer extends JsonDeserializer<Participant> {

        @Override
        public Participant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            List<Augment> augments = StreamSupport.stream(root.get("augments").spliterator(), false)
                    .map(JsonNode::asText).map(Augments::getAugment).toList();
            List<Participant.TraitData> traitData = MAPPER.readerForListOf(Participant.TraitData.class).readValue(root.get("traits"));
            List<Participant.UnitData> unitData = MAPPER.readerForListOf(Participant.UnitData.class).readValue(root.get("units"));
            return new Participant(augments,
                    new Companion(Tacticians.getTacticianByContentId(root.get("companion").get("content_ID").asText()),
                            root.get("companion").get("skin_ID").asInt()),
                    root.get("gold_left").asInt(), root.get("last_round").asInt(), root.get("level").asInt(),
                    root.get("placement").asInt(), root.has("partner_group_id") ? root.get("partner_group_id").asInt() : -1,
                    root.get("players_eliminated").asInt(), root.get("puuid").asText(), (float)root.get("time_eliminated").asDouble(),
                    root.get("total_damage_to_players").asInt(), traitData, unitData);
        }
    }

    public static class TraitDataDeserializer extends JsonDeserializer<Participant.TraitData> {

        @Override
        public Participant.TraitData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            Trait trait = Traits.getTrait(root.get("name").asText());
            int units = root.get("num_units").asInt();
            return new Participant.TraitData(trait, units, trait.getEffects().stream()
                    .filter(effect -> effect.getMinUnits() <= units && units <= effect.getMaxUnits()).findAny().orElse(null));
        }
    }

    public static class UnitDataDeserializer extends JsonDeserializer<Participant.UnitData> {

        @Override
        public Participant.UnitData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            return new Participant.UnitData(Units.getUnit(root.get("character_id").asText()),
                    StreamSupport.stream(root.get("itemNames").spliterator(), false).map(JsonNode::asText)
                            .map(Items::getItem).toList(),
                    root.get("rarity").asInt(), root.get("tier").asInt());
        }
    }

    public static class QueueTypeDeserializer extends JsonDeserializer<QueueType> {

        @Override
        public QueueType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);


            Sprite sprite = new Sprite(root.get("image").get("sprite").asText(),
                    root.get("image").get("group").asText(),
                    root.get("image").get("x").asInt(),
                    root.get("image").get("y").asInt(),
                    root.get("image").get("w").asInt(),
                    root.get("image").get("h").asInt());
            return new QueueType(root.get("id").asInt(), root.get("name").asText(),
                    root.get("queueType").asText(), sprite, root.get("image").get("full").asText());
        }
    }

    public static class TacticianDeserializer extends JsonDeserializer<Tactician> {

        @Override
        public Tactician deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            return new Tactician(root.get("itemId").asInt(), root.get("contentId").asText(),
                    root.get("level").asInt(), root.get("name").asText(), root.get("loadoutsIcon").asText(),
                    root.get("description").asText(), root.get("speciesName").asText(), root.get("speciesId").asInt(),
                    MAPPER.readerFor(Tactician.Rarity.class).readValue(root.get("rarity")),
                    root.get("isDefault").asBoolean(), root.get("TFTOnly").asBoolean());
        }
    }

    public static class AugmentDeserializer extends JsonDeserializer<Augment> {

        @Override
        public Augment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            List<Trait> associatedTraits = StreamSupport.stream(root.get("associatedTraits").spliterator(), false)
                    .map(node -> Traits.getTraitByIdOrName(node.asText())).toList();
            Map<String, Float> effects = MAPPER.readerForMapOf(Float.class).readValue(root.get("effects"));
            return new Augment(root.get("apiName").asText(), root.get("name").asText(), associatedTraits,
                    root.get("desc").asText(), effects, root.get("icon").asText());
        }
    }

    public static class UnitDeserializer extends JsonDeserializer<Unit> {

        @Override
        public Unit deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            Unit.Ability ability = MAPPER.readerFor(Unit.Ability.class).readValue(root.get("ability"));
            Unit.Stats stats = MAPPER.readerFor(Unit.Stats.class).readValue(root.get("stats"));
            List<Trait> traits = StreamSupport.stream(root.get("traits").spliterator(), false)
                    .map(jsonNode -> Traits.getTraitByName(jsonNode.asText())).toList();
            return new Unit(ability, root.get("apiName").asText(), root.get("cost").asInt(),
                    root.get("icon").asText(), root.get("name").asText(), stats, traits);
        }
    }

    public static class ItemDeserializer extends JsonDeserializer<Item> {

        @Override
        public Item deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode root = p.getCodec().readTree(p);

            Map<String, Float> effects = MAPPER.readerForMapOf(Float.class).readValue(root.get("effects"));
            List<Trait> incompatibleTraits = StreamSupport.stream(root.get("incompatibleTraits").spliterator(), false)
                    .map(jsonNode -> Traits.getTraitByIdOrName(jsonNode.asText())).toList();
            return new Item(root.get("apiName").asText(), root.get("desc").asText(), effects, root.get("icon").asText(),
                    incompatibleTraits, root.get("name").asText(), root.get("unique").asBoolean());
        }
    }

    public static RankEntry getTfTRankEntryFromEntries(List<JsonNode> nodes) {
        for (JsonNode node: nodes) {
            try {
                if(MAPPER.readerFor(RankedQueue.class).readValue(node.get("queueType")) != RankedQueue.TFT) continue;
                return MAPPER.readerFor(RankEntry.class).readValue(node);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return RankEntry.UNRANKED;
    }

    public static HyperRollEntry getHyperRollEntryFromEntries(List<JsonNode> nodes) {
        for (JsonNode node: nodes) {
            try {
                if(MAPPER.readerFor(RankedQueue.class).readValue(node.get("queueType")) != RankedQueue.HYPER_ROLL) continue;
                return MAPPER.readerFor(HyperRollEntry.class).readValue(node);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return HyperRollEntry.UNRANKED;
    }
}