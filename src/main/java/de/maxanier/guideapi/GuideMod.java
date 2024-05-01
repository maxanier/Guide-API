package de.maxanier.guideapi;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.IGuideBook;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.item.ItemGuideBookDataComponents;
import de.maxanier.guideapi.network.ReadingStatePayload;
import de.maxanier.guideapi.proxy.ClientProxy;
import de.maxanier.guideapi.proxy.CommonProxy;
import de.maxanier.guideapi.util.AnnotationHandler;
import de.maxanier.guideapi.util.ReloadCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;
import net.neoforged.fml.common.Mod;

@Mod(value = GuideMod.ID)
public class GuideMod {

    public static final String NAME = "Guide-API VP";
    public static final String ID = "guideapi_vp";
    public static boolean inDev = false;

    public static GuideMod INSTANCE;

    public static final CommonProxy PROXY =  FMLEnvironment.dist == Dist.CLIENT ? new ClientProxy() : new CommonProxy();
    public final IEventBus modBus;

    public GuideMod(IEventBus modBus) {
        INSTANCE = this;
        this.modBus = modBus;
        checkDevEnv();
        GuideAPI.initialize();
        modBus.addListener(this::setup);
        modBus.addListener(this::loadComplete);
        modBus.addListener(this::registerPackets);
        ItemGuideBookDataComponents.register(modBus);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void checkDevEnv() {
        String launchTarget = System.getProperty("guideapi.target");
        if (launchTarget != null && launchTarget.contains("dev")) {
            inDev = true;
        }
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        PROXY.initColors();

        for (Pair<Book, IGuideBook> guide : AnnotationHandler.BOOK_CLASSES)
            guide.getRight().handlePost(GuideAPI.getStackFromBook(guide.getLeft()));
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        if (inDev) {
            event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("guide-api-vp").then(ReloadCommand.register()));
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        if (GuideConfig.COMMON == null) {
            throw new IllegalStateException("Did not build configuration, before configuration load. Make sure to call GuideConfig#buildConfiguration during one of the registry events");
        }
        for (Pair<Book, IGuideBook> pair : AnnotationHandler.BOOK_CLASSES) {
            IGuideBook guide = pair.getRight();
            guide.registerInfoRenderer(pair.getLeft());
        }
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ReadingStatePayload.TYPE, ReadingStatePayload.STREAM_CODEC, ReadingStatePayload::handle);
    }
}
