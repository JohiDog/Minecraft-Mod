// BEISPIEL - wird nicht mitgebaut (liegt nicht in src/main/java).
// Um eigene Power-Typen zu registrieren: fabric-loom in build.gradle aktivieren und
// folgende Klasse als ModInitializer eintragen. Registriert z.B. "klassen:item_durability_bonus",
// womit du den Schmied-Lehrling-Perk "bessere Durability" umsetzen kannst.
package io.github.example.klassen;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.PowerFactory;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registry;

public class KlassenFramework implements ModInitializer {
    @Override
    public void onInitialize() {
        // Registry.register(ApoliRegistries.POWER_FACTORY, Apoli.identifier("item_durability_bonus"), MyPower::createFactory);
        Apoli.LOGGER.info("Klassen Framework geladen");
    }
}
