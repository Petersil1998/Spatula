package net.petersil98.spatula.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.core.Core;
import net.petersil98.core.util.Loader;
import net.petersil98.core.util.settings.Settings;
import net.petersil98.spatula.Spatula;
import net.petersil98.spatula.collection.*;
import net.petersil98.spatula.data.*;
import net.petersil98.stcommons.constants.STConstants;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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
                QueueType queueType = MAPPER.readerFor(QueueType.class).readValue(queue);
                queueTypes.put(queueType.getId(), queueType);
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
            for(JsonNode node: MAPPER.readTree(in)) {
                Tactician tactician = MAPPER.readerFor(Tactician.class).readValue(node);
                tacticians.put(tactician.getId(), tactician);
                TACTICIANS_UPGRADES.put(node.get("contentId").asText(),
                        MAPPER.readerForListOf(String.class).readValue(node.get("upgrades")));
            }
            setFieldInCollection(Tacticians.class, tacticians);
            Tacticians.getTacticians().forEach(Tactician::postInit);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadTraits() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            Set<Trait> traits = new HashSet<>();
            JsonNode rootNode = MAPPER.readTree(in);
            for (JsonNode node: rootNode.get("setData")) {
                traits.addAll(MAPPER.readerForListOf(Trait.class).readValue(node.get("traits")));
            }
            for (JsonNode node: rootNode.get("sets")) {
                traits.addAll(MAPPER.readerForListOf(Trait.class).readValue(node.get("traits")));
            }
            setFieldInCollection(Traits.class, traits.stream().collect(Collectors.toMap(Trait::getId, trait -> trait)));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadAugments() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            Map<String, Augment> augments = new HashMap<>();
            for (JsonNode node: MAPPER.readTree(in).get("items")) {
                if(!node.get("apiName").asText().contains("_Augment_")) continue;
                Augment augment = MAPPER.readerFor(Augment.class).readValue(node);
                augments.put(augment.getId(), augment);
            }
            setFieldInCollection(Augments.class, augments);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void loadUnits() {
        try(InputStream in = new URI(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).toURL().openStream()) {
            JsonNode rootNode = MAPPER.readTree(in);
            Set<Unit> units = new HashSet<>();
            for (JsonNode node: rootNode.get("setData")) {
                units.addAll(MAPPER.readerForListOf(Unit.class).readValue(node.get("champions")));
            }
            for (JsonNode node: rootNode.get("sets")) {
                units.addAll(MAPPER.readerForListOf(Unit.class).readValue(node.get("champions")));
            }
            setFieldInCollection(Units.class, units.stream().collect(Collectors.toMap(Unit::getId, unit -> unit)));
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
                Item item = MAPPER.readerFor(Item.class).readValue(node);
                items.put(item.getId(), item);
                if(node.get("composition").size() > 0) {
                    ITEMS_COMPOSITIONS.put(item.getId(),
                            MAPPER.readerForListOf(String.class).readValue(node.get("composition")));
                }
            }
            setFieldInCollection(Items.class, items);
            Items.getItems().forEach(Item::postInit);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
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