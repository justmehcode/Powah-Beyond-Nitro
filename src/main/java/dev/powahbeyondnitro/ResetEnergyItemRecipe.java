package dev.powahbeyondnitro;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import owmii.powah.lib.item.IEnergyContainingItem;

/** A single dynamic recipe: one power device in, one factory-default item out. */
public final class ResetEnergyItemRecipe extends CustomRecipe {
    public ResetEnergyItemRecipe(CraftingBookCategory category) { super(category); }

    private static ItemStack source(CraftingInput input) {
        ItemStack found = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (!found.isEmpty() || !(stack.getItem() instanceof IEnergyContainingItem)) return ItemStack.EMPTY;
            var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (!id.getNamespace().equals(BeyondNitro.ID)
                    && !(id.getNamespace().equals("powah") && id.getPath().endsWith("_nitro"))) return ItemStack.EMPTY;
            found = stack;
        }
        return found;
    }

    @Override public boolean matches(CraftingInput input, Level level) { return !source(input).isEmpty(); }

    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = source(input);
        // Do not copy components: this intentionally discards all data on the consumed item.
        return source.isEmpty() ? ItemStack.EMPTY : new ItemStack(source.getItem());
    }

    @Override public boolean canCraftInDimensions(int width, int height) { return width >= 1 && height >= 1; }
    @Override public RecipeSerializer<?> getSerializer() { return BeyondNitro.RESET_RECIPE.get(); }
}
