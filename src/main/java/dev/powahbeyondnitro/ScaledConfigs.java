package dev.powahbeyondnitro;

import java.util.function.Supplier;
import java.util.function.LongSupplier;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.CableConfig;
import owmii.powah.config.v2.types.EnergyConfig;
import owmii.powah.config.v2.types.GeneratorConfig;
import owmii.powah.config.v2.types.ChargingConfig;
import owmii.powah.config.v2.types.EnderConfig;
import owmii.powah.config.v2.values.TieredChannelValues;
import owmii.powah.config.v2.values.TieredEnergyValues;
import owmii.powah.lib.logistics.energy.Energy;

/** Live views of Powah's Nitro settings. Never mutate the shared Powah config. */
public final class ScaledConfigs {
    private ScaledConfigs() {}
    private static TieredEnergyValues values(BeyondTier tier, LongSupplier base, long maximum) {
        // Powah's generator BE reads generation_rates.get() directly, bypassing getGeneration().
        return new TieredEnergyValues(0, 0, 0, 0, 0, 0, 0) {
            @Override public long get(Tier ignored) { return tier.scale(base.getAsLong(), maximum); }
        };
    }
    public static EnergyConfig energy(BeyondTier tier, Supplier<EnergyConfig> base) {
        return new EnergyConfig(values(tier, () -> base.get().getCapacity(Tier.NITRO), Energy.MAX),
                values(tier, () -> base.get().getTransfer(Tier.NITRO), Integer.MAX_VALUE));
    }
    public static GeneratorConfig generator(BeyondTier tier, Supplier<GeneratorConfig> base) {
        return new GeneratorConfig(values(tier, () -> base.get().getCapacity(Tier.NITRO), Energy.MAX),
                values(tier, () -> base.get().getTransfer(Tier.NITRO), Integer.MAX_VALUE),
                values(tier, () -> base.get().getGeneration(Tier.NITRO), Integer.MAX_VALUE));
    }
    public static CableConfig cable(BeyondTier tier, Supplier<CableConfig> base) {
        return new CableConfig(values(tier, () -> base.get().getTransfer(Tier.NITRO), Integer.MAX_VALUE));
    }
    public static ChargingConfig charging(BeyondTier tier, Supplier<ChargingConfig> base) {
        return new ChargingConfig(values(tier, () -> base.get().getCapacity(Tier.NITRO), Energy.MAX),
                values(tier, () -> base.get().getTransfer(Tier.NITRO), Integer.MAX_VALUE),
                values(tier, () -> base.get().getChargingSpeed(Tier.NITRO), Integer.MAX_VALUE));
    }
    public static EnderConfig ender(BeyondTier tier, Supplier<EnderConfig> base) {
        // Powah's network and menu support at most twelve channels. Only throughput scales.
        var channels = new TieredChannelValues(12, 12, 12, 12, 12, 12, 12) {
            @Override public int get(Tier ignored) { return Math.clamp(base.get().channels.get(Tier.NITRO), 1, 12); }
        };
        return new EnderConfig(values(tier, () -> base.get().getTransfer(Tier.NITRO), Integer.MAX_VALUE), channels);
    }
}
