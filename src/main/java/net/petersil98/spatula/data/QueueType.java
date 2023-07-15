package net.petersil98.spatula.data;

import net.petersil98.stcommons.data.Sprite;

import java.util.Objects;

public class QueueType {

    private final int id;
    private final String name;
    private final String type;
    private final Sprite sprite;
    private final String image;

    public QueueType(int id, String name, String type, Sprite sprite, String image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sprite = sprite;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueType queueType = (QueueType) o;
        return id == queueType.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
