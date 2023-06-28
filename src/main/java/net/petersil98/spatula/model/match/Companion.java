package net.petersil98.spatula.model.match;

import net.petersil98.spatula.data.Tactician;

public class Companion {
    private final Tactician tactician;
    private final int skinId;

    public Companion(Tactician tactician, int skinId) {
        this.tactician = tactician;
        this.skinId = skinId;
    }

    public Tactician getTactician() {
        return tactician;
    }

    public int getSkinId() {
        return skinId;
    }
}
