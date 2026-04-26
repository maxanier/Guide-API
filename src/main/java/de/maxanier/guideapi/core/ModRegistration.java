package de.maxanier.guideapi.core;

import de.maxanier.guideapi.GuideConfig;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.core.item.ItemGuideBook;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = GuideMod.ID)
public class ModRegistration {

    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM)) return;

        AnnotationHandler.gatherBooks();
        //Don't build book content here as items/blocks are not available and translation is only possible in game
        GuideConfig.buildConfiguration(GuideMod.INSTANCE.modBus());//Build configuration now that we know all added books
        for (Book book : GuideAPI.getBooks().values()) {
            Identifier id = Identifier.fromNamespaceAndPath(GuideMod.ID, book.getRegistryName().toString().replace(":", "-"));
            event.register(Registries.ITEM, id, () -> new ItemGuideBook(book, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
            APISetter.setBookForStack(book, BuiltInRegistries.ITEM.get(id).get());
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        //Request the following recipe types to be sent to the client
        event.sendRecipes(RecipeType.CRAFTING,
                RecipeType.STONECUTTING,
                RecipeType.SMELTING,
                RecipeType.SMOKING,
                RecipeType.BLASTING,
                RecipeType.CAMPFIRE_COOKING,
                RecipeType.SMITHING);
    }

}
