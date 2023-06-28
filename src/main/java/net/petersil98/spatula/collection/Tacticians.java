package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.Tactician;

import java.util.List;

public class Tacticians {
    private static List<Tactician> tacticians;

    public static Tactician getTactician(int id){
        return tacticians.stream().filter(tactician -> tactician.getId() == id).findFirst().orElse(null);
    }

    public static Tactician getTacticianByContentId(String content){
        return tacticians.stream().filter(tactician -> tactician.getContentId().equals(content)).findFirst().orElse(null);
    }

    public static List<Tactician> getTacticians() {
        return tacticians;
    }
}
