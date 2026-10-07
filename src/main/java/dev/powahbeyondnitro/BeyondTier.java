package dev.powahbeyondnitro;

import java.util.Locale;

public enum BeyondTier {
    AURION(4, 0xFFE4A1), VIBERION(16, 0x39EAC3),
    OBLIVION(64, 0xAE76F8), SINGULARITY(256, 0xDBE6FF);

    public final long multiplier;
    public final int color;
    BeyondTier(long multiplier, int color) { this.multiplier = multiplier; this.color = color; }
    public String id() { return name().toLowerCase(Locale.ROOT); }

    public long scale(long value, long maximum) {
        if (value <= 0) return 0;
        return value > maximum / multiplier ? maximum : value * multiplier;
    }
}
