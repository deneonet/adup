package net.deneo.adup.data;

import java.util.Date;
import java.util.UUID;

/**
 * <h3>A class for holding custom data about a player.</h3>
 *
 * @since R-1.5
 */
public class AdupPlayer {
    public final UUID uuid;
    public final boolean isMarkedAsDuper;
    public final boolean isMarkedAsClear;
    public int pickUpMultiplier;
    public int moveMultiplier;
    public boolean isSuspected;
    public boolean isWhitelisted;
    public Date banDate;
    public Date unbanDate;
    public Date suspectedDate;
    public boolean isBanned;
    public int warnings;
    public int dropMultiplier;

    public AdupPlayer(
            UUID uuid,
            boolean isBanned,
            boolean isSuspected,
            boolean isMarkedAsDuper,
            boolean isMarkedAsClear,
            boolean isWhitelisted,
            Date banDate,
            Date unbanDate,
            Date suspectedDate,
            int warnings,
            int dropMultiplier,
            int pickUpMultiplier,
            int moveMultiplier
    ) {
        this.uuid = uuid;
        this.isBanned = isBanned;
        this.isSuspected = isSuspected;
        this.isMarkedAsDuper = isMarkedAsDuper;
        this.isMarkedAsClear = isMarkedAsClear;
        this.isWhitelisted = isWhitelisted;
        this.banDate = banDate;
        this.unbanDate = unbanDate;
        this.suspectedDate = suspectedDate;
        this.warnings = warnings;
        this.dropMultiplier = dropMultiplier;
        this.pickUpMultiplier = pickUpMultiplier;
        this.moveMultiplier = moveMultiplier;
    }

    public AdupPlayer(UUID uuid) {
        this.uuid = uuid;
        this.isBanned = false;
        this.isSuspected = false;
        this.isMarkedAsDuper = false;
        this.isMarkedAsClear = false;
        this.isWhitelisted = false;
        this.banDate = null;
        this.unbanDate = null;
        this.suspectedDate = null;
        this.warnings = 0;
        this.dropMultiplier = 0;
        this.pickUpMultiplier = 0;
        this.moveMultiplier = 0;
    }
}
