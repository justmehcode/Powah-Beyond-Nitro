package dev.powahbeyondnitro;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import owmii.powah.lib.item.IEnergyContainingItem;

/** Preserve one machine's item data; never spread or merge it across recipe outputs. */
public final class CraftingData {
    private CraftingData() {}

    private static boolean applies(ItemStack result) {
        return !result.isEmpty() && result.getItem() instanceof IEnergyContainingItem
                && BuiltInRegistries.ITEM.getKey(result.getItem()).getNamespace().equals(BeyondNitro.ID);
    }

    private static String family(ItemStack stack) {
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!id.getNamespace().equals("powah") && !id.getNamespace().equals(BeyondNitro.ID)) return "";
        String path = id.getPath();
        int separator = path.lastIndexOf('_');
        return separator < 0 ? path : path.substring(0, separator);
    }

    private static ItemStack donor(CraftingInput input, ItemStack result) {
        if (result.getCount() != 1) return ItemStack.EMPTY;
        ItemStack found = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IEnergyContainingItem
                    && family(stack).equals(family(result))) {
                if (!found.isEmpty()) return ItemStack.EMPTY;
                found = stack;
            }
        }
        return found;
    }

    public static boolean allowed(CraftingInput input, ItemStack result) {
        if (!applies(result)) return true;
        ItemStack donor = donor(input, result);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            // Any non-default data on another input would otherwise be silently discarded.
            if (!stack.isEmpty() && stack != donor && !stack.getComponentsPatch().isEmpty()) return false;
        }
        return true;
    }

    public static ItemStack assemble(CraftingInput input, ItemStack result) {
        if (!applies(result)) return result;
        if (!allowed(input, result)) return ItemStack.EMPTY;
        ItemStack donor = donor(input, result);
        if (donor.isEmpty()) return result;
        ItemStack copy = result.copy();
        copy.applyComponents(donor.getComponentsPatch());
        return copy;
    }
}
