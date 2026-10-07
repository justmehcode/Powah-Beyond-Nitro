package dev.powahbeyondnitro;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.AbstractEnergyBlock;
import owmii.powah.lib.item.IEnergyContainingItem;
import owmii.powah.lib.logistics.energy.Energy;

@Mod(BeyondNitro.ID)
public final class BeyondNitro {
    public static final String ID = "powahbeyondnitro";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final Map<String, DeferredBlock<Block>> MACHINES = new LinkedHashMap<>();
    public static final Map<String, DeferredItem<Item>> MATERIALS = new LinkedHashMap<>();
    public static final Map<String, DeferredBlock<Block>> CRYSTAL_BLOCKS = new LinkedHashMap<>();
    private static final Map<String, Supplier<? extends BlockEntityType<?>>> TYPES = new LinkedHashMap<>();

    static {
        registerFamily("energy_cell", BeyondBlocks.Cell::new, Tiles.ENERGY_CELL);
        registerFamily("energy_cable", BeyondBlocks.Cable::new, Tiles.CABLE);
        registerFamily("energizing_rod", BeyondBlocks.Rod::new, Tiles.ENERGIZING_ROD);
        registerFamily("furnator", BeyondBlocks.Furnator::new, Tiles.FURNATOR);
        registerFamily("magmator", BeyondBlocks.Magmator::new, Tiles.MAGMATOR);
        registerFamily("thermo_generator", BeyondBlocks.Thermo::new, Tiles.THERMO_GEN);
        registerFamily("solar_panel", BeyondBlocks.Solar::new, Tiles.SOLAR_PANEL);
        registerFamily("reactor", BeyondBlocks.Reactor::new, Tiles.REACTOR);
        registerFamily("ender_cell", BeyondBlocks.EnderCell::new, Tiles.ENDER_CELL);
        registerFamily("ender_gate", BeyondBlocks.EnderGate::new, Tiles.ENDER_GATE);
        registerFamily("player_transmitter", BeyondBlocks.Transmitter::new, Tiles.PLAYER_TRANSMITTER);
        registerFamily("energy_hopper", BeyondBlocks.Hopper::new, Tiles.ENERGY_HOPPER);
        registerFamily("energy_discharger", BeyondBlocks.Discharger::new, Tiles.ENERGY_DISCHARGER);
        for (BeyondTier tier : BeyondTier.values()) {
            for (String material : new String[]{"crystal", "capacitor"}) {
                String name = material + "_" + tier.id();
                MATERIALS.put(name, ITEMS.register(name, () -> new Item(new Item.Properties().fireResistant())));
            }
            String battery = "battery_" + tier.id();
            MATERIALS.put(battery, ITEMS.register(battery, () -> new BeyondBattery(tier)));
            String crystalBlock = tier.id() + "_crystal_block";
            var block = BLOCKS.register(crystalBlock, () -> new Block(owmii.powah.lib.block.Properties.metal(2f, 20f)));
            CRYSTAL_BLOCKS.put(crystalBlock, block);
            ITEMS.register(crystalBlock, () -> new BlockItem(block.get(), new Item.Properties().fireResistant()));
        }
        TABS.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.powahbeyondnitro"))
                .icon(() -> MATERIALS.get("crystal_singularity").get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    for (BeyondTier tier : BeyondTier.values()) {
                        output.accept(MATERIALS.get("crystal_" + tier.id()).get());
                        output.accept(MATERIALS.get("capacitor_" + tier.id()).get());
                        output.accept(MATERIALS.get("battery_" + tier.id()).get());
                        output.accept(CRYSTAL_BLOCKS.get(tier.id() + "_crystal_block").get());
                        MACHINES.forEach((name, block) -> { if (name.endsWith("_" + tier.id())) output.accept(block.get()); });
                    }
                }).build());
    }

    private static void registerFamily(String family, BiFunction<Block.Properties, BeyondTier, Block> factory,
                                       Supplier<? extends BlockEntityType<?>> type) {
        TYPES.put(family, type);
        for (BeyondTier tier : BeyondTier.values()) {
            String name = family + "_" + tier.id();
            var block = BLOCKS.register(name, () -> factory.apply(
                    owmii.powah.lib.block.Properties.metalNoSolid(3.5f, 30f), tier));
            MACHINES.put(name, block);
            ITEMS.register(name, () -> (BlockItem) ((AbstractEnergyBlock<?, ?>) block.get())
                    .getBlockItem(new Item.Properties().fireResistant(), null));
        }
    }

    public BeyondNitro(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
        bus.addListener(this::extendBlockEntityTypes);
        bus.addListener(this::registerItemEnergy);
    }

    private void extendBlockEntityTypes(BlockEntityTypeAddBlocksEvent event) {
        TYPES.forEach((family, type) -> {
            Block[] blocks = java.util.Arrays.stream(BeyondTier.values())
                    .map(tier -> MACHINES.get(family + "_" + tier.id()).get()).toArray(Block[]::new);
            event.modify(type.get(), blocks);
            if (family.equals("reactor")) event.modify(Tiles.REACTOR_PART.get(), blocks);
        });
    }

    private void registerItemEnergy(RegisterCapabilitiesEvent event) {
        for (var entry : ITEMS.getEntries()) {
            if (entry.get() instanceof IEnergyContainingItem provider) {
                event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, unused) -> {
                    var info = provider.getEnergyInfo();
                    return info == null ? null : new Energy.Item(stack, info).createItemCapability();
                }, entry.get());
            }
        }
    }
}
