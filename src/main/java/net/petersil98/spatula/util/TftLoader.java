package net.petersil98.spatula.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.petersil98.core.constant.Constants;
import net.petersil98.core.data.Sprite;
import net.petersil98.core.util.Loader;
import net.petersil98.core.util.settings.Settings;
import net.petersil98.spatula.Spatula;
import net.petersil98.spatula.collection.*;
import net.petersil98.spatula.data.*;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.StreamSupport;

import static net.petersil98.spatula.model.Deserializers.MAPPER;

public class TftLoader extends Loader {

    private static final String TFT_BASE_PATH = BASE_PATH + "tft" + File.separator;
    private static final String QUEUE_TYPES_FILE_PATH = TFT_BASE_PATH + "queues.json";
    private static final String TACTICIANS_FILE_PATH = TFT_BASE_PATH + "tacticians.json";
    private static final String TRAITS_FILE_PATH = TFT_BASE_PATH + "traits.json";
    private static final String AUGMENTS_FILE_PATH = TFT_BASE_PATH + "augments.json";
    private static final String UNITS_FILE_PATH = TFT_BASE_PATH + "units.json";
    private static final String ITEMS_FILE_PATH = TFT_BASE_PATH + "items.json";

    public static Map<String, List<String>> ITEMS_COMPOSITIONS = new HashMap<>();
    public static Map<String, List<String>> TACTICIANS_UPGRADES = new HashMap<>();

    @Override
    protected void load(boolean shouldUpdate) {
        createFilesIfNotExistent();
        if(shouldUpdate) {
            updateQueueTypesFile();
            updateTacticiansFile();
            updateTraitsFile();
            updateAugmentsFile();
            updateUnitsFile();
            updateItemsFile();
        }
        loadQueueTypes();
        loadTacticians();
        loadTraits();
        loadAugments();
        loadUnits();
        loadItems();
    }

    private void createFilesIfNotExistent() {
        try {
            new File(TFT_BASE_PATH).mkdirs();
            new File(QUEUE_TYPES_FILE_PATH).createNewFile();
            new File(TACTICIANS_FILE_PATH).createNewFile();
            new File(TRAITS_FILE_PATH).createNewFile();
            new File(AUGMENTS_FILE_PATH).createNewFile();
            new File(UNITS_FILE_PATH).createNewFile();
            new File(ITEMS_FILE_PATH).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateQueueTypesFile(){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(String.format("%scdn/%s/data/%s/tft-queues.json", Constants.DDRAGON_BASE_PATH, Constants.DDRAGON_VERSION, Settings.getLanguage().toString())).openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(QUEUE_TYPES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadQueueTypes(){
        try {
            String content = Files.readString(Paths.get(QUEUE_TYPES_FILE_PATH));
            JsonNode node = MAPPER.readTree(content);
            List<QueueType> queueTypes = new ArrayList<>();
            for(JsonNode queue: node.get("data")) {
                Sprite sprite = new Sprite(queue.get("image").get("sprite").asText(),
                        queue.get("image").get("group").asText(),
                        queue.get("image").get("x").asInt(),
                        queue.get("image").get("y").asInt(),
                        queue.get("image").get("w").asInt(),
                        queue.get("image").get("h").asInt());
                queueTypes.add(new QueueType(queue.get("id").asInt(), queue.get("name").asText(),
                        queue.get("queueType").asText(), sprite, queue.get("image").get("full").asText()));
            }
            setFieldInCollection(QueueTypes.class, queueTypes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTacticiansFile(){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/companions.json").openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(TACTICIANS_FILE_PATH, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTacticians(){
        try {
            TACTICIANS_UPGRADES.clear();
            String content = Files.readString(Paths.get(TACTICIANS_FILE_PATH));
            List<Tactician> tacticians = new ArrayList<>();
            for(JsonNode tactician: MAPPER.readTree(content)) {
                tacticians.add(new Tactician(tactician.get("itemId").asInt(), tactician.get("contentId").asText(),
                        tactician.get("level").asInt(), tactician.get("name").asText(), tactician.get("loadoutsIcon").asText(),
                        tactician.get("description").asText(), tactician.get("speciesName").asText(), tactician.get("speciesId").asInt(),
                        MAPPER.readerFor(Tactician.Rarity.class).readValue(tactician.get("rarity")),
                        tactician.get("isDefault").asBoolean(), tactician.get("TFTOnly").asBoolean()));
                TACTICIANS_UPGRADES.put(tactician.get("contentId").asText(),
                        MAPPER.readValue(tactician.get("upgrades").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));
            }
            setFieldInCollection(Tacticians.class, tacticians);
            Tacticians.getTacticians().forEach(Tactician::postInit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTraitsFile(){
        try(Scanner sc = new Scanner(new URL(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).openStream())) {
            sc.useDelimiter("\n");

            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }

            ArrayNode root = MAPPER.createArrayNode();
            //JsonNode rootNode = MAPPER.readTree(sb.toString());
            JsonNode rootNode = MAPPER.readTree(Files.readString(Path.of("temp.json")));
            for (JsonNode node: rootNode.get("setData")) {
                for(JsonNode trait: node.get("traits")) {
                    renameFieldInNode((ObjectNode) trait, "apiName", "id");
                    renameFieldInNode((ObjectNode) trait, "icon", "image");
                    addNodeIfIdDoesntExist(root, trait);
                }
            }

            for (JsonNode node: rootNode.get("sets")) {
                for(JsonNode trait: node.get("traits")) {
                    renameFieldInNode((ObjectNode) trait, "apiName", "id");
                    renameFieldInNode((ObjectNode) trait, "icon", "image");
                    addNodeIfIdDoesntExist(root, trait);
                }
            }

            Files.writeString(Paths.get(TRAITS_FILE_PATH), root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTraits(){
        try {
            String content = Files.readString(Paths.get(TRAITS_FILE_PATH));
            setFieldInCollection(Traits.class, MAPPER.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, Trait.class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAugmentsFile(){
        try(Scanner sc = new Scanner(new URL(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).openStream())) {
            sc.useDelimiter("\n");

            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }

            ArrayNode root = MAPPER.createArrayNode();
            JsonNode json = MAPPER.readTree(sb.toString()).get("items");
            for (JsonNode node: json) {
                if(!node.get("apiName").asText().contains("_Augment_")) continue;
                ObjectNode objectNode = (ObjectNode)node;
                renameFieldInNode(objectNode, "apiName", "id");
                objectNode.remove("composition");
                objectNode.remove("from");
                objectNode.remove("incompatibleTraits");
                objectNode.remove("unique");
                addNodeIfIdDoesntExist(root, objectNode);
            }

            Files.writeString(Paths.get(AUGMENTS_FILE_PATH), root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAugments(){
        try {
            String content = Files.readString(Paths.get(AUGMENTS_FILE_PATH));
            JsonNode root = MAPPER.readTree(content);
            List<Augment> augments = new ArrayList<>();
            for(JsonNode augment: root) {
                List<Trait> associatedTraits = StreamSupport.stream(augment.get("associatedTraits").spliterator(), false)
                        .map(node -> Traits.getTraitByIdOrName(node.asText())).toList();
                Map<String, Float> effects = MAPPER.readValue(augment.get("effects").toString(), TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Float.class));
                augments.add(new Augment(augment.get("id").asText(),
                        augment.get("name").asText(), associatedTraits,
                        augment.get("desc").asText(), effects, augment.get("icon").asText()));
            }
            setFieldInCollection(Augments.class, augments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUnitsFile(){
        try(Scanner sc = new Scanner(new URL(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).openStream())) {
            sc.useDelimiter("\n");

            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }

            ArrayNode root = MAPPER.createArrayNode();
            JsonNode rootNode = MAPPER.readTree(sb.toString());
            for (JsonNode node: rootNode.get("setData")) {
                for(JsonNode unit: node.get("champions")) {
                    renameFieldInNode((ObjectNode) unit, "apiName", "id");
                    ((ObjectNode) unit).remove("characterName");
                    ((ObjectNode) unit).remove("squareIcon");
                    ((ObjectNode) unit).remove("tileIcon");
                    renameFieldInNode((ObjectNode) unit, "icon", "image");
                    addNodeIfIdDoesntExist(root, unit);
                }
            }

            for (JsonNode node: rootNode.get("sets")) {
                for(JsonNode unit: node.get("champions")) {
                    renameFieldInNode((ObjectNode) unit, "apiName", "id");
                    ((ObjectNode) unit).remove("characterName");
                    ((ObjectNode) unit).remove("squareIcon");
                    ((ObjectNode) unit).remove("tileIcon");
                    renameFieldInNode((ObjectNode) unit, "icon", "image");
                    addNodeIfIdDoesntExist(root, unit);
                }
            }

            Files.writeString(Paths.get(UNITS_FILE_PATH), root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUnits(){
        try {
            String content = Files.readString(Paths.get(UNITS_FILE_PATH));
            JsonNode root = MAPPER.readTree(content);
            List<Unit> units = new ArrayList<>();
            for(JsonNode unit: root) {
                Unit.Ability ability = MAPPER.readValue(unit.get("ability").toString(), Unit.Ability.class);
                Unit.Stats stats = MAPPER.readValue(unit.get("stats").toString(), Unit.Stats.class);
                List<Trait> traits = StreamSupport.stream(unit.get("traits").spliterator(), false)
                        .map(jsonNode -> Traits.getTraitByName(jsonNode.asText())).toList();
                units.add(new Unit(ability, unit.get("id").asText(), unit.get("cost").asInt(),
                        unit.get("image").asText(), unit.get("name").asText(), stats, traits));
            }
            setFieldInCollection(Units.class, units);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateItemsFile(){
        try(Scanner sc = new Scanner(new URL(String.format("https://raw.communitydragon.org/latest/cdragon/tft/%s.json", Settings.getLanguage().toString().toLowerCase())).openStream())) {
            sc.useDelimiter("\n");

            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }

            ArrayNode root = MAPPER.createArrayNode();
            JsonNode json = MAPPER.readTree(sb.toString()).get("items");
            for (JsonNode node: json) {
                if(!node.get("apiName").asText().contains("_Item_")) continue;
                renameFieldInNode((ObjectNode)node, "apiName", "id");
                ((ObjectNode)node).remove("associatedTraits");
                ((ObjectNode)node).remove("from");
                addNodeIfIdDoesntExist(root, node);
            }

            Files.writeString(Paths.get(ITEMS_FILE_PATH), root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadItems(){
        try {
            ITEMS_COMPOSITIONS.clear();
            String content = Files.readString(Paths.get(ITEMS_FILE_PATH));
            JsonNode root = MAPPER.readTree(content);
            List<Item> items = new ArrayList<>();
            for(JsonNode item: root) {
                Map<String, Float> effects = MAPPER.readValue(item.get("effects").toString(), TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Float.class));
                List<Trait> incompatibleTraits = StreamSupport.stream(item.get("incompatibleTraits").spliterator(), false)
                                .map(jsonNode -> Traits.getTraitByIdOrName(jsonNode.asText())).toList();
                items.add(new Item(item.get("id").asText(), item.get("desc").asText(), effects,
                        item.get("icon").asText(), incompatibleTraits,
                        MAPPER.readValue(item.get("incompatibleTraits").toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)),
                        item.get("name").asText(), item.get("unique").asBoolean()));
                if(item.get("composition").size() > 0) {
                    List<String> composition = MAPPER.readValue(item.get("composition").toString(),
                            TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
                    ITEMS_COMPOSITIONS.put(item.get("id").asText(), composition);
                }
            }
            setFieldInCollection(Items.class, items);
            Items.getItems().forEach(Item::postInit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNodeIfIdDoesntExist(ArrayNode nodes, JsonNode toAdd) {
        Iterator<JsonNode> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            JsonNode node = iterator.next();
            if(node.get("id").equals(toAdd.get("id"))) {
                iterator.remove();
                break;
            }
        }
        nodes.add(toAdd);
    }

    private void renameFieldInNode(ObjectNode node, String fieldName, String newFieldName) {
        node.set(newFieldName, node.get(fieldName));
        node.remove(fieldName);
    }

    private void setFieldInCollection(Class<?> collectionClass, List<?> elements) {
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