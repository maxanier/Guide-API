package de.maxanier.guideapi;


import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class GuideConfig {

    /**
     * For side independent configuration. Not synced.
     * Loaded after registry events but before setup
     */
    public static Common COMMON;

    public static void buildConfiguration(IEventBus modBus) {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        ModConfigSpec commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, commonSpec);
    }

    public static class Common {

        public final ModConfigSpec.BooleanValue canSpawnWithBook;

        public final ModConfigSpec.BooleanValue enableLogging;
        public final Map<Book, ModConfigSpec.BooleanValue> SPAWN_BOOKS = new HashMap<>();


        Common(ModConfigSpec.Builder builder) {
            builder.comment("Common configurations settings").push("common");
            enableLogging = builder.comment("Enables extra information being printed to the console.").define("enableLogging", true);
            canSpawnWithBook = builder.comment("Allows books to spawn with new players.\nThis is a global override for all books if set to false.").define("canSpawnWithBook", true);
            builder.comment("If the player should spawn with this book").push("spawnBook");
            for (Book book : GuideAPI.getBooks().values()) {
                SPAWN_BOOKS.put(book, builder.define(book.getRegistryName().getNamespace() + "-" + book.getRegistryName().getPath(), book.shouldSpawnWithBook()));
            }
            builder.pop();
            builder.pop();
        }
    }

}
