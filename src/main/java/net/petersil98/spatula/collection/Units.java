package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Unit;

import java.util.List;

public class Units {

    private static List<Unit> units;

    public static Unit getUnit(String id){
        return units.stream().filter(unit -> unit.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<Unit> getUnits() {
        return units;
    }
}
