package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Tactician;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tacticians {
    private static Map<Integer, Tactician> tacticians;

    public static Tactician getTactician(int id){
        return tacticians.get(id);
    }

    public static Tactician getTacticianByContentId(String content){
        return getTacticians().stream().filter(tactician -> tactician.getContentId().equals(content)).findFirst().orElse(null);
    }

    public static List<Tactician> getTacticians() {
        return new ArrayList<>(tacticians.values());
    }
}
