package net.petersil98.spatula.util;

import net.petersil98.core.util.InvalidFilterException;
import net.petersil98.spatula.collection.QueueTypes;

import java.util.Map;

public class Util {

    public static void validateFilter(Map<String, String> filter) {
        filter.forEach((filterName, arg) -> {
            switch (filterName) {
                case "endTime", "start", "startTime" -> {
                    try {
                        long time = Long.parseLong(arg);
                        if (time < 0) throw new InvalidFilterException(arg + " cannot be negative");
                    } catch (NumberFormatException e) {
                        throw new InvalidFilterException("Filter \"" + arg + "\" isn't a number", e);
                    }
                }
                case "queue" -> {
                    try {
                        int queueId = Integer.parseInt(arg);
                        if (QueueTypes.getQueueTypes().stream().noneMatch(queueType -> queueType.getId() == queueId)) {
                            throw new InvalidFilterException("No queue type found with ID \"" + arg + "\"");
                        }
                    } catch (NumberFormatException e) {
                        throw new InvalidFilterException("Filter \"" + arg + "\" isn't a number", e);
                    }
                }
                case "type" -> {}
                case "count" -> {
                    try {
                        int count = Integer.parseInt(arg);
                        if (count < 0 || count > 100)
                            throw new InvalidFilterException("count must be between 0 and 100");
                    } catch (NumberFormatException e) {
                        throw new InvalidFilterException("Filter \"" + arg + "\" isn't a number", e);
                    }
                }
                default -> throw new InvalidFilterException("Unknown filter \"" + filterName + "\" for match history");
            }
        });
    }
}
