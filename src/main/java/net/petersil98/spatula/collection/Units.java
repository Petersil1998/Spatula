package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Units {

    private static Map<String, Unit> units;

    public static Unit getUnit(String id){
        return units.get(id);
    }

    public static List<Unit> getUnits() {
        return new ArrayList<>(units.values());
    }
}
