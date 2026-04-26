package de.maxanier.guideapi.api.book;

import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.world.IInfoRenderer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IGuideBook {

    /**
     * Build your guide book here. The returned book will be registered for you. The book created here can be modified
     * later, so make sure to keep a reference for yourself.
     * This is called during the Register<Item> event, so don't do anything here except binding your book.
     *
     * @return a built book to be registered.
     */
    @Nullable
    Book buildBook();

    /**
     * Called during Post Initialization.
     */
    default void handlePost(@Nonnull Holder<Item> bookItem) {
        // No-op
    }

    /**
     * If you want to register {@link IInfoRenderer} to {@link GuideAPI}, do it in here.
     */
    default void registerInfoRenderer(Book yourBook) {

    }
}
