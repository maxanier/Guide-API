package de.maxanier.guideapi.proxy;

import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class CommonProxy {


    public void openGuidebook(PlayerEntity player, World world, Book book, ItemStack bookStack) {
    }

    public void playSound(SoundEvent sound) {
    }

    public void openEntry(Book book, CategoryAbstract categoryAbstract, EntryAbstract entryAbstract, PlayerEntity player, ItemStack stack) {
    }

    public void initColors() {
    }
}
