package de.maxanier.guideapi;

import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.GuideBook;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.item.ItemGuideBook;
import de.maxanier.guideapi.util.APISetter;
import de.maxanier.guideapi.util.AnnotationHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = GuideMod.ID, bus = EventBusSubscriber.Bus.MOD)
public class RegistrarGuideAPI {

    @SubscribeEvent
    public static void registerItems(RegisterEvent event) {
        if(!event.getRegistryKey().equals(Registries.ITEM))return;

        AnnotationHandler.gatherBooks();
        //Don't build book content here as items/blocks are not available and translation is only possible in game
        GuideConfig.buildConfiguration(GuideMod.INSTANCE.modBus);//Build configuration now that we know all added books
        for (Book book : GuideAPI.getBooks().values()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GuideMod.ID, book.getRegistryName().toString().replace(":", "-"));
            event.register(Registries.ITEM,id, () -> new ItemGuideBook(book));
            APISetter.setBookForStack(book, () -> new ItemStack(BuiltInRegistries.ITEM.get(id)));
        }
    }

}
