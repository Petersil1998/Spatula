package net.petersil98.spatula.collection;

import net.petersil98.spatula.data.QueueType;

import java.util.List;

public class QueueTypes {

    private static List<QueueType> queueTypes;

    public static QueueType getQueueType(int id){
        return queueTypes.stream().filter(queueType -> queueType.getId() == id).findFirst().orElse(null);
    }

    public static List<QueueType> getQueueTypes() {
        return queueTypes;
    }
}
