package de.maxanier.guideapi.core.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxanier.guideapi.GuideConfig;
import de.maxanier.guideapi.GuideMod;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.core.APISetter;
import de.maxanier.guideapi.core.AnnotationHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;


@EventBusSubscriber(value = Dist.CLIENT, modid = GuideMod.ID)
public class GuideModClient {

    @Nullable
    private static RecipeMap syncedRecipes;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        if (GuideMod.inDev) {
            event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("guide-api-vp").then(ReloadCommand.register()));
        }
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        syncedRecipes = event.getRecipeMap();
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        //Clear cached recipes
        syncedRecipes = null;
    }

    public static RecipeMap getSyncedRecipes() {
        return syncedRecipes == null ? RecipeMap.EMPTY : syncedRecipes;
    }


    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        APISetter.setScreenFactories();
        if (GuideConfig.COMMON == null) {
            throw new IllegalStateException("Did not build configuration, before configuration load. Make sure to call GuideConfig#buildConfiguration during one of the registry events");
        }
        for (Pair<Book, IGuideBook> pair : AnnotationHandler.BOOK_CLASSES) {
            IGuideBook guide = pair.getRight();
            guide.registerInfoOverlays(pair.getLeft());
        }
    }
}
