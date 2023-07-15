package net.petersil98.spatula.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.core.Core;
import net.petersil98.core.util.Loader;
import net.petersil98.core.util.settings.Settings;
import net.petersil98.spatula.Spatula;
import net.petersil98.spatula.collection.*;
import net.petersil98.spatula.data.*;
import net.petersil98.stcommons.constants.STConstants;
import net.petersil98.stcommons.data.Sprite;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static net.petersil98.spatula.model.Deserializers.MAPPER;

public class TftLoader extends Loader {

    private static String latestDDragonVersion;

    public static Map<String, List<String>> ITEMS_COMPOSITIONS = new HashMap<>();
    public static Map<String, List<String>> TACTICIANS_UPGRADES = new HashMap<>();

    @Override
    protected void load() {
        if(latestDDragonVersion == null) loadLatestVersions();
        loadQueueTypes();
        loadTacticians();
        loadTraits();
        loadAugments();
        loadUnits();
        loadItems();
    }

    @Override
    protected boolean shouldReloadData() {
        String versionsUrl = STConstants.DDRAGON_BASE_PATH + "api/versions.json";
        try(InputStream lolVersion = URI.create(versionsUrl).toURL().openConnection().getInputStream()) {
            String[] versions = Core.MAPPER.readValue(IOUtils.toString(new InputStreamReader(lolVersion)), TypeFactory.defaultInstance().constructArrayType(String.class));
            STConstants.DDRAGON_VERSION = versions[0];
            if(!latestDDragonVersion.equals(STConstants.DDRAGON_VERSION)) {
                latestDDragonVersion = STConstants.DDRAGON_VERSION;
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static void loadLatestVersions() {
        String versionsUrl = STConstants.DDRAGON_BASE_PATH + "api/versions.json";
        try(InputStream lolVersion = URI.create(versionsUrl).toURL().openConnection().getInputStream()) {
            String[] versions = Core.MAPPER.readValue(IOUtils.toString(new InputStreamReader(lolVersion)), TypeFactory.defaultInstance().constructArrayType(String.class));
            STConstants.DDRAGON_VERSION = versions[0];
            latestDDragonVersion = versions[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadQueueTypes() {
        try(InputStream in = new URI(String.format("%scdn/%s/data/%s/tft-queues.json", STConstants.DDRAGON_BASE_PATH, STConstants.DDRAGON_VERSION, Settings.getLanguage().toString())).toURL().openStream()) {
            Map<Integer, QueueType> queueTypes = new HashMap<>();
            for(JsonNode queue: MAPPER.readTree(in).get("data")) {
                Sprite sprite = new Sprite(queue.get("image").get("sprite").asText(),
                        queue.get("image").get("group").asText(),
                        queue.get("image").get("x").asInt(),
                        queue.get("image").get("y").asInt(),
                        queue.get("image").get("w").asInt(),
                        queue.get("image").get("h").asInt());
                int id = queue.get("id").asInt();
                queueTypes.put(id, new QueueType(id, queue.get("name").asText(),
                        queue.get("queueType").asText(), sprite, queue.get("image").get("full").asText()));
            }
            setFieldInCollection(QueueTypes.class, queueTypes);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadTacticians() {
        try(InputStream in = new URI("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/companions.json").toURL().openStream()) {
            TACTICIANS_UPGRADES.clear();
            Map<Integer, Tactician> tacticians = new HashMap<>();
            for(JsonNode tactician: MAPPER.readTree(in)) {
                int id = tactician.get("itemId").asInt();
                tacticians.put(id, new Tactician(id, tactician.get("contentId").asText(),
                        tactician.get("level").asInt(), tactician.get("name").asText(), tactician.get("loadoutsIcon").asText(),
                        tactician.get("description").asText(), tactician.get("speciesName").asText(), tactician.get("speciesId").asInt(),
                        MAPPER.readerFor(Tactician.Rarity.class).readValue(tactician.get("rarity")),
                        tactician.get("isDefault").asBoolean(), tactician.get("TFTOnly").asBoolean()));
                TACTICIANS_UPGRADES.put(tactician.get("contentId").asText(),
                        MAPPER.readValue(tactician.get("upgrades").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));
            }
            setFieldInCollection(Tacticians.class, tacticians);
            Tacticians.getTacticians().forEach(Tactician::postInit);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadTraits() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            Map<String, Trait> traits = new HashMap<>();
            JsonNode rootNode = MAPPER.readTree(in);
            for (JsonNode node: rootNode.get("setData")) {
                for(JsonNode traitNode: node.get("traits")) {
                    Trait trait = MAPPER.readerFor(Trait.class).readValue(traitNode);
                    traits.put(trait.getId(), trait);
                }
            }
            for (JsonNode node: rootNode.get("sets")) {
                for(JsonNode traitNode: node.get("traits")) {
                    Trait trait = MAPPER.readerFor(Trait.class).readValue(traitNode);
                    traits.put(trait.getId(), trait);
                }
            }
            setFieldInCollection(Traits.class, traits);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadAugments() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            Map<String, Augment> augments = new HashMap<>();
            for (JsonNode augment: MAPPER.readTree(in).get("items")) {
                if(!augment.get("apiName").asText().contains("_Augment_")) continue;
                List<Trait> associatedTraits = StreamSupport.stream(augment.get("associatedTraits").spliterator(), false)
                        .map(node -> Traits.getTraitByIdOrName(node.asText())).toList();
                Map<String, Float> effects = MAPPER.readValue(augment.get("effects").toString(), TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Float.class));
                String id = augment.get("apiName").asText();
                augments.put(id, new Augment(id,
                        augment.get("name").asText(), associatedTraits,
                        augment.get("desc").asText(), effects, augment.get("icon").asText()));
            }
            setFieldInCollection(Augments.class, augments);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadUnits() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            JsonNode rootNode = MAPPER.readTree(in);
            Map<String, Unit> units = new HashMap<>();
            for (JsonNode node: rootNode.get("setData")) {
                for(JsonNode unitNode: node.get("champions")) {
                    Unit unit = parseUnit(unitNode);
                    units.put(unit.getId(), unit);
                }
            }

            for (JsonNode node: rootNode.get("sets")) {
                for(JsonNode unitNode: node.get("champions")) {
                    Unit unit = parseUnit(unitNode);
                    units.put(unit.getId(), unit);
                }
            }
            setFieldInCollection(Units.class, units);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadItems() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            ITEMS_COMPOSITIONS.clear();
            Map<String, Item> items = new HashMap<>();
            for (JsonNode node: MAPPER.readTree(in).get("items")) {
                if(!node.get("apiName").asText().contains("_Item_")) continue;
                Map<String, Float> effects = MAPPER.readValue(node.get("effects").toString(), TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Float.class));
                List<Trait> incompatibleTraits = StreamSupport.stream(node.get("incompatibleTraits").spliterator(), false)
                        .map(jsonNode -> Traits.getTraitByIdOrName(jsonNode.asText())).toList();
                String id = node.get("apiName").asText();
                items.put(id, new Item(id, node.get("desc").asText(), effects,
                        node.get("icon").asText(), incompatibleTraits,
                        node.get("name").asText(), node.get("unique").asBoolean()));
                if(node.get("composition").size() > 0) {
                    List<String> composition = MAPPER.readValue(node.get("composition").toString(),
                            TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
                    ITEMS_COMPOSITIONS.put(node.get("id").asText(), composition);
                }
            }
            setFieldInCollection(Items.class, items);
            Items.getItems().forEach(Item::postInit);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Unit parseUnit(JsonNode unit) throws JsonProcessingException {
        Unit.Ability ability = MAPPER.readValue(unit.get("ability").toString(), Unit.Ability.class);
        Unit.Stats stats = MAPPER.readValue(unit.get("stats").toString(), Unit.Stats.class);
        List<Trait> traits = StreamSupport.stream(unit.get("traits").spliterator(), false)
                .map(jsonNode -> Traits.getTraitByName(jsonNode.asText())).toList();
        String id = unit.get("apiName").asText();
        return new Unit(ability, id, unit.get("cost").asInt(),
                unit.get("icon").asText(), unit.get("name").asText(), stats, traits);
    }

    private void setFieldInCollection(Class<?> collectionClass, Map<?,?> elements) {
        try {
            char[] fieldName = collectionClass.getSimpleName().toCharArray();
            fieldName[0] += 32;
            Field field = collectionClass.getDeclaredField(new String(fieldName));
            field.setAccessible(true);
            field.set(null, elements);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Spatula.LOGGER.error("Couldn't set collection Type of class " + collectionClass.getSimpleName(), e);
        }
    }
}