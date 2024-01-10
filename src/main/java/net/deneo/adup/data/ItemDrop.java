package net.deneo.adup.data;

import java.util.Date;
import java.util.UUID;

public class ItemDrop {
    public final UUID player;
    public final Date date;

    public ItemDrop(UUID player, Date date) {
        this.player = player;
        this.date = date;
    }
}
