package de.maxanier.guideapi;

import de.maxanier.guideapi.api.GuideAPI;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.book.IGuideBook;
import de.maxanier.guideapi.core.APISetter;
import de.maxanier.guideapi.core.AnnotationHandler;
import de.maxanier.guideapi.core.item.ItemGuideBookDataComponents;
import de.maxanier.guideapi.core.network.ReadingStatePayload;
import de.maxanier.guideapi.core.proxy.ClientProxy;
import de.maxanier.guideapi.core.proxy.CommonProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;

@Mod(value = GuideMod.ID)
public record GuideMod(IEventBus modBus) {

    public static final String NAME = "Guide-API VP";
    public static final String ID = "guideapi_vp";
    public static final CommonProxy PROXY = FMLEnvironment.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();
    public static boolean inDev = false;
    public static GuideMod INSTANCE;

    public GuideMod(IEventBus modBus) {
        INSTANCE = this;
        this.modBus = modBus;
        checkDevEnv();
        GuideAPI.initialize();
        APISetter.setScreenFactories();
        modBus.addListener(this::setup);
        modBus.addListener(this::loadComplete);
        modBus.addListener(this::registerPackets);
        ItemGuideBookDataComponents.register(modBus);
    }

    private void checkDevEnv() {
        inDev = !FMLEnvironment.isProduction();
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        for (Pair<Book, IGuideBook> guide : AnnotationHandler.BOOK_CLASSES)
            guide.getRight().handlePost(GuideAPI.getItemForBook(guide.getLeft()));
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ReadingStatePayload.TYPE, ReadingStatePayload.STREAM_CODEC, ReadingStatePayload::handle);
    }

    private void setup(final FMLClientSetupEvent event) {
        if (GuideConfig.COMMON == null) {
            throw new IllegalStateException("Did not build configuration, before configuration load. Make sure to call GuideConfig#buildConfiguration during one of the registry events");
        }
        for (Pair<Book, IGuideBook> pair : AnnotationHandler.BOOK_CLASSES) {
            IGuideBook guide = pair.getRight();
            guide.registerInfoOverlays(pair.getLeft());
        }
    }
}
