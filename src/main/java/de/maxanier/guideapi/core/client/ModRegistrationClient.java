package de.maxanier.guideapi.core.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxanier.guideapi.GuideMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import javax.annotation.Nullable;


@EventBusSubscriber(value = Dist.CLIENT, modid = GuideMod.ID)
public class ModRegistrationClient {

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


}
