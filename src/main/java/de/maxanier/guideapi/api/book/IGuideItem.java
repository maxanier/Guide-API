package de.maxanier.guideapi.api.book;

import net.minecraft.world.item.ItemStack;

public interface IGuideItem {

    Book getBook(ItemStack stack);
}
