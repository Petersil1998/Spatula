package net.petersil98.spatula.util;

import net.petersil98.core.util.InvalidFilterException;
import net.petersil98.spatula.collection.QueueTypes;

import java.util.Map;

public class Util {

    public static void validateFilter(Map<String, String> filter) {
        filter.forEach((filterName, arg) -> {
            switch (filterName) {
                case "endTime", "start", "startTime", "count" -> {
                    try {
                        long number = Long.parseLong(arg);
                        if (number < 0) throw new InvalidFilterException(arg + " cannot be negative");
                    } catch (NumberFormatException e) {
                        throw new InvalidFilterException("Filter \"" + arg + "\" isn't a number", e);
                    }
                }
                default -> throw new InvalidFilterException("Unknown filter \"" + filterName + "\" for match history");
            }
        });
    }
}
