package de.maxanier.guideapi;

import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.IGuideBook;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.util.AnnotationHandler;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.apache.commons.lang3.tuple.Pair;


@EventBusSubscriber(value = Dist.CLIENT, modid = GuideMod.ID, bus = EventBusSubscriber.Bus.MOD)
public class RegistrarGuideAPIClient {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (Pair<Book, IGuideBook> guide : AnnotationHandler.BOOK_CLASSES) {
            ResourceLocation loc = guide.getRight().getModel();
            if (loc != null) {
                event.register(new ModelResourceLocation(loc, "inventory"));
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void bakeModel(ModelEvent.ModifyBakingResult event) {
        for (Pair<Book, IGuideBook> guide : AnnotationHandler.BOOK_CLASSES) {
            ResourceLocation loc = guide.getRight().getModel();
            if (loc != null) {
                ModelResourceLocation newMrl = new ModelResourceLocation(loc, "inventory");
                Item bookItem = GuideAPI.getStackFromBook(guide.getLeft()).getItem();
                ModelResourceLocation oldMrl = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(bookItem), "inventory");
                BakedModel model = event.getModels().get(newMrl);

                event.getModels().put(oldMrl, model);
            }
        }

    }

}
