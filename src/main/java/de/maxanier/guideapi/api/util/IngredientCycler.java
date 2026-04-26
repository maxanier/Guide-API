package de.maxanier.guideapi.api.util;


import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Random;


/**
 * Allows convenient consistent cycling through all matching stacks of an ingredient
 */
public class IngredientCycler {

    private final Random rand = new Random();
    private long lastCycle = -1;
    private int cycleIdx = 0;

    /**
     * Retrieves an itemstack that matches the ingredient.
     * Cycles though all matching stacks.
     * Must call {@link IngredientCycler#tick(long)} before (e.g. once per onDraw)
     *
     * @param ingredient The ingredient
     * @param index      An "unique" id for this ingredient, so multiple ingredients can be cycled independently
     * @return Optional. Can be empty if ingredient is invalid and has no matching stacks
     */
    public Optional<ItemStack> getCycledIngredientStack(@Nonnull Ingredient ingredient, int index) {
        Holder<Item>[] itemStacks = ingredient.items().toArray(Holder[]::new);
        if (itemStacks.length > 0) {
            rand.setSeed(index);
            int id = (index + rand.nextInt(itemStacks.length) + cycleIdx) % itemStacks.length;
            return Optional.of(new ItemStack(itemStacks[id].value()));
        }
        return Optional.empty();
    }

    /**
     * Cycles though all matching stacks.
     * Must call {@link IngredientCycler#tick(long)} before (e.g. once per onDraw)
     *
     * @param index An "unique" id for this ingredient, so multiple ingredients can be cycled independently
     * @return Optional. Can be empty if ingredient is invalid and has no matching stacks
     */
    public ItemStack getCycledIngredientStack(List<ItemStack> stacks, int index) {
        if (!stacks.isEmpty()) {
            rand.setSeed(index);
            int id = (index + rand.nextInt(stacks.size()) + cycleIdx) % stacks.size();
            return stacks.get(id);
        }
        return ItemStack.EMPTY;
    }

    /**
     * @param time Minecraft game time
     */
    public void tick(long time) {
        if (lastCycle < 0 || lastCycle < time - 20) {
            if (lastCycle > 0) {
                cycleIdx++;
                cycleIdx = Math.max(0, cycleIdx);
            }
            lastCycle = time;
        }
    }
}
