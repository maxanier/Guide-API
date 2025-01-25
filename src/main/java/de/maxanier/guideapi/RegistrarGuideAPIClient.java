package de.maxanier.guideapi;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.IGuideBook;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.util.AnnotationHandler;
import de.maxanier.guideapi.util.ReloadCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;



@EventBusSubscriber(value = Dist.CLIENT, modid = GuideMod.ID)
public class RegistrarGuideAPIClient {

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
        for (RecipeHolder<?> value : syncedRecipes.values()) {
            LogManager.getLogger().info("Recipe {} with {}", value.id(), syncedRecipes.byKey(value.id()).value().getClass());
        }
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        //Clear cached recipes
        syncedRecipes = null;
    }

    public static RecipeMap getSyncedRecipes() {
        return syncedRecipes == null ? RecipeMap.EMPTY : syncedRecipes;
    }


}
