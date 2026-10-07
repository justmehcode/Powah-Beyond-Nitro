package dev.powahbeyondnitro;

import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.EnergyConfig;
import owmii.powah.item.BatteryItem;

public final class BeyondBattery extends BatteryItem {
    private final EnergyConfig config;
    public BeyondBattery(BeyondTier tier) {
        super(new Properties().stacksTo(1).fireResistant(), Tier.NITRO);
        config = ScaledConfigs.energy(tier, () -> Powah.config().devices.batteries);
    }
    @Override public EnergyConfig getConfig() { return config; }
}
