package de.maxanier.guideapi.api;

import de.maxanier.guideapi.api.impl.Book;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public interface IGuideItem {

    /**
     * The page that was last opened
     */

    Book getBook(ItemStack stack);
}
