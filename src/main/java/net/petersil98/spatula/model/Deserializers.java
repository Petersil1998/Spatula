package net.petersil98.spatula.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.petersil98.spatula.collection.*;
import net.petersil98.spatula.data.Augment;
import net.petersil98.spatula.data.Trait;
import net.petersil98.spatula.model.match.Companion;
import net.petersil98.spatula.model.match.MatchDetails;
import net.petersil98.spatula.model.match.Participant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class Deserializers {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static class MatchDetailsDeserializer extends JsonDeserializer<MatchDetails> {

        @Override
        public MatchDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            JsonNode root = p.getCodec().readTree(p);
            JsonNode info = root.get("info");
            List<Participant> participants = new ArrayList<>(8);
            for(JsonNode participant: info.get("participants")) {
                List<Augment> augments = StreamSupport.stream(participant.get("augments").spliterator(), false)
                                .map(JsonNode::asText).map(Augments::getAugment).toList();
                List<Participant.TraitData> traitData = StreamSupport.stream(participant.get("traits").spliterator(), false)
                                .map(jsonNode -> {
                                    Trait trait = Traits.getTrait(jsonNode.get("name").asText());
                                    int units = jsonNode.get("num_units").asInt();
                                    return new Participant.TraitData(trait, units, trait.getEffects().stream()
                                            .filter(effect -> effect.getMinUnits() <= units && units <= effect.getMaxUnits()).findAny().orElse(null));
                                }).toList();
                List<Participant.UnitData> unitData = StreamSupport.stream(participant.get("units").spliterator(), false)
                                .map(jsonNode -> new Participant.UnitData(Units.getUnit(jsonNode.get("character_id").asText()),
                                        StreamSupport.stream(jsonNode.get("itemNames").spliterator(), false).map(JsonNode::asText)
                                                .map(Items::getItem).toList(),
                                        jsonNode.get("rarity").asInt(), jsonNode.get("tier").asInt())).toList();
                participants.add(new Participant(augments,
                        new Companion(Tacticians.getTacticianByContentId(participant.get("companion").get("content_ID").asText()),
                                participant.get("companion").get("skin_ID").asInt()),
                        participant.get("gold_left").asInt(), participant.get("last_round").asInt(), participant.get("level").asInt(),
                        participant.get("placement").asInt(),
                        participant.has("partner_group_id") ? participant.get("partner_group_id").asInt() : -1,
                        participant.get("players_eliminated").asInt(),
                        participant.get("puuid").asText(), (float)participant.get("time_eliminated").asDouble(),
                        participant.get("total_damage_to_players").asInt(), traitData, unitData));
            }
            return new MatchDetails(info.get("game_datetime").asLong(), info.get("game_length").asInt(),
                    info.has("game_variation") ? info.get("game_variation").asText() : null,
                    info.get("game_version").asText(), participants, QueueTypes.getQueueType(info.get("queue_id").asInt()),
                    info.get("tft_game_type").asText(), info.get("tft_set_number").asInt());
        }
    }
}