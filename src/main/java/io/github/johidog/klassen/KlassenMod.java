package io.github.johidog.klassen;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlassenMod implements ModInitializer {

    public static final String MOD_ID = "klassen";
    public static final Logger LOGGER = LoggerFactory.getLogger("KlassenFramework");

    @Override
    public void onInitialize() {
        LOGGER.info("Klassen Framework geladen (Rassen/Klassen/Skills + X-UI + JEI-Requirements).");
    }
}
