package dev.powahbeyondnitro;

import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.*;
import owmii.powah.block.energycell.EnergyCellBlock;
import owmii.powah.block.cable.CableBlock;
import owmii.powah.block.energizing.EnergizingRodBlock;
import owmii.powah.block.furnator.FurnatorBlock;
import owmii.powah.block.magmator.MagmatorBlock;
import owmii.powah.block.thermo.ThermoBlock;
import owmii.powah.block.solar.SolarBlock;
import owmii.powah.block.reactor.ReactorBlock;

/** Addon blocks retain Powah machines' inventories, menus, fuel handling and persistence. */
public final class BeyondBlocks {

    public static final class EnderCell extends owmii.powah.block.ender.EnderCellBlock implements TieredMachine {
        private final BeyondTier tier;
        private final EnderConfig config;
        public EnderCell(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.ender(tier, () -> Powah.config().devices.ender_cells);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public EnderConfig getConfig() { return config; }
    }

    public static final class EnderGate extends owmii.powah.block.ender.EnderGateBlock implements TieredMachine {
        private final BeyondTier tier;
        private final EnderConfig config;
        public EnderGate(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.ender(tier, () -> Powah.config().devices.ender_gates);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public EnderConfig getConfig() { return config; }
    }

    public static final class Transmitter extends owmii.powah.block.transmitter.PlayerTransmitterBlock implements TieredMachine {
        private final BeyondTier tier;
        private final ChargingConfig config;
        public Transmitter(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.charging(tier, () -> Powah.config().devices.player_transmitters);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public ChargingConfig getConfig() { return config; }
    }

    public static final class Hopper extends owmii.powah.block.hopper.EnergyHopperBlock implements TieredMachine {
        private final BeyondTier tier;
        private final ChargingConfig config;
        public Hopper(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.charging(tier, () -> Powah.config().devices.hoppers);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public ChargingConfig getConfig() { return config; }
    }

    public static final class Discharger extends owmii.powah.block.discharger.EnergyDischargerBlock implements TieredMachine {
        private final BeyondTier tier;
        private final EnergyConfig config;
        public Discharger(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.energy(tier, () -> Powah.config().devices.dischargers);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public EnergyConfig getConfig() { return config; }
    }

    private BeyondBlocks() {}

    public static final class Cell extends EnergyCellBlock implements TieredMachine {
        private final BeyondTier tier;
        private final EnergyConfig config;
        public Cell(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.energy(tier, () -> Powah.config().devices.energy_cells);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public EnergyConfig getConfig() { return config; }
    }

    public static final class Cable extends CableBlock implements TieredMachine {
        private final BeyondTier tier;
        private final CableConfig config;
        public Cable(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.cable(tier, () -> Powah.config().devices.cables);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public CableConfig getConfig() { return config; }
    }

    public static final class Rod extends EnergizingRodBlock implements TieredMachine {
        private final BeyondTier tier;
        private final EnergyConfig config;
        public Rod(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.energy(tier, () -> Powah.config().devices.energizing_rods);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public EnergyConfig getConfig() { return config; }
    }

    public static final class Furnator extends FurnatorBlock implements TieredMachine {
        private final BeyondTier tier;
        private final GeneratorConfig config;
        public Furnator(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.generator(tier, () -> Powah.config().generators.furnators);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public GeneratorConfig getConfig() { return config; }
    }

    public static final class Magmator extends MagmatorBlock implements TieredMachine {
        private final BeyondTier tier;
        private final GeneratorConfig config;
        public Magmator(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.generator(tier, () -> Powah.config().generators.magmators);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public GeneratorConfig getConfig() { return config; }
    }

    public static final class Thermo extends ThermoBlock implements TieredMachine {
        private final BeyondTier tier;
        private final GeneratorConfig config;
        public Thermo(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.generator(tier, () -> Powah.config().generators.thermo_generators);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public GeneratorConfig getConfig() { return config; }
    }

    public static final class Solar extends SolarBlock implements TieredMachine {
        private final BeyondTier tier;
        private final GeneratorConfig config;
        public Solar(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.generator(tier, () -> Powah.config().generators.solar_panels);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public GeneratorConfig getConfig() { return config; }
    }

    public static final class Reactor extends ReactorBlock implements TieredMachine {
        private final BeyondTier tier;
        private final GeneratorConfig config;
        public Reactor(Properties properties, BeyondTier tier) {
            super(properties, Tier.NITRO);
            this.tier = tier;
            this.config = ScaledConfigs.generator(tier, () -> Powah.config().generators.reactors);
        }
        @Override public BeyondTier beyondTier() { return tier; }
        @Override public GeneratorConfig getConfig() { return config; }
    }
}
