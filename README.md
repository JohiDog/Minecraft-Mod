# Minecraft-Mod

## Klassen Framework (Origins-Addon, Fabric 1.20.2)


Standalone, plug-and-play Test-Framework auf Basis von **Origins + Apoli**:
Rasse -> Klasse -> 3 Skills, Evolution-Ketten pro Eintrag und Progression-Locks
(z.B. nur Koch darf Farmer's Delight benutzen, nur Magier duerfen Magiewaffen fuehren,
Rezepte sind hinter Klasse/Tier gelockt).

**100 % datengetrieben** - das Jar enthaelt nur `data/` + `assets/`, keinen Java-Code.

## Schnellstart (Plug & Play)

1. Voraussetzung: Minecraft **1.20.2** mit Fabric Loader >= 0.15.10, dazu die Mods
   **Origins 1.12.10** (liefert Apoli/Calio gleich mit) und **Fabric API 0.91.6+1.20.2**.
2. Jar bauen (`./tools/build-jar.sh`, falls nicht schon in `dist/` vorhanden) und `dist/klassen-framework-1.0.0+1.20.2.jar` in den `mods/`-Ordner legen. Fertig.
3. Neue Welt erstellen -> Auswahl-Reihenfolge: **Rasse -> Klasse -> Skill 1 -> Skill 2 -> Skill 3**
   (die vanilla `origins:origin`-Schicht ist abgeschaltet, siehe `data/origins/origin_layers/origin.json`;
   zum Re-Aktivieren Datei loeschen oder `"enabled": true` setzen).

## Test-Flow

- **Locks testen:** Als Nicht-Koch versuchen, ein Farmer's-Delight-Item zu essen -> wird blockiert.
  Als Nicht-Magier einen Magie-Stab (Tag `#klassen:magie_waffen`) benutzen -> blockiert.
  Dasselbe mit Boegen (`#klassen:boegen`) und Klingen (`#klassen:schwerter`).
- **Evolution testen:** `/advancement grant @s only klassen:evolution/goblin_brute`
  -> Announcement + Origin-Wechsel auf `klassen:goblin_brute` (neue Powers sofort aktiv).
  Kette: Goblin -> Brute -> Hob -> God bzw. Mensch -> Erhobener Mensch -> Halbgott -> Gott.
- **Rezept-Locks testen:** FD-Rezepte sind ueber `origins:chose_origin`-Advancements vergeben
  (`data/klassen/advancements/recipes/`); zusaetzlich schaltet Chef/Sanji ueber `apoli:recipe`
  weitere Rezepte frei (`powers/chef_recipes.json`, `sanji_master_recipes.json`).
- **Neu waehlen:** Orb of Origin ist craftbar (`data/klassen/recipes/orb_of_origin.json`:
  Enderperle + Smaragd-Block), oder `/origin set <spieler> <layer> <origin>` (z.B. `/origin set @s klassen:class klassen:magier_lehrling`).
- **Anzeigen:** `/origin get @s <layer>`, Taste `O` (View Origin), Taste `G` (aktive Power).

## Aufbau

```
src/main/resources/
  fabric.mod.json                      Mod-Deskriptor (depends: origins, apoli, fabric-api)
  pack.mcmeta                          Pack-Metadaten des mitgelieferten Datenpakets
  data/klassen/origin_layers/          race (order 0), class (1), skill_1..3 (2-4), system (hidden, default_origin)
  data/klassen/origins/                base + Rassen-/Klassen-/Skill-Ketten (jede Stufe mit "upgrades")
  data/klassen/powers/                 4 globale Lock-Powers (prevent_item_use) + Perks + Rezept-Powers
  data/klassen/advancements/           evolution/* (Trigger fuer Upgrades), recipes/* (chose_origin -> Rezept-Rewards)
  data/klassen/tags/items/             farmers_delight, magie_waffen, boegen, schwerter
  data/klassen/recipes/                Orb of Origin (zum Umwaehlen)
  data/origins/origin_layers/origin.json   schaltet die vanilla-Ursprungs-Schicht ab
  assets/klassen/lang/                 de_de.json, en_us.json
```

## Mechanik-Kurzfassung

- **Layer = eine Wahl.** Pro Layer genau eine Origin; Layer-Reihenfolge ueber `order`.
- **Evolution = `upgrades` im Origin-JSON:** `{condition: <advancement-id>, origin: <ziel-id>, announcement: <lang-key>}`.
  Beim Advancement tauscht Origins die Origin im selben Layer aus und die Powers wechseln mit.
- **Lock = Power mit Condition:** `apoli:prevent_item_use` + `item_condition` (Tag) + invertierter
  `origins:origin`-Condition (alle Tiers der Klasse, mit/ohne `layer`). Die Locks liegen auf der
  versteckten System-Layer `klassen:system` mit `default_origin: klassen:base` -> gilt fuer alle Spieler.
- **Rezept-Lock = Advancement mit `origins:chose_origin`-Trigger** (rewards.recipes) oder `apoli:recipe`-Power.
- **Rassen-abhaengige Klassen:** conditioned origins im Layer (`{condition: {type: origins:origin, ...}, origins: [...]}`),
  z.B. Paladin nur fuer Mensch. Doppelte Skill-Wahl wird in skill_2/skill_3 ebenfalls per Condition verhindert.

## Erweitern

- Neue Rasse/Klasse/Skill: Origin-JSON in `data/klassen/origins/` + Eintrag im passenden Layer.
- Neue Waffenkategorie: Tag in `data/klassen/tags/items/` + Lock-Power in `powers/` + Referenz in `origins/base.json`.
- Eigenen Code (z.B. Power fuer Item-Durability): `build.gradle` auf fabric-loom umstellen,
  siehe `examples/CustomPowerExample.java`.
- Fertiges Jar bauen: `./tools/build-jar.sh` -> `dist/klassen-framework-1.0.0+1.20.2.jar` (packt `src/main/resources` zu einem Jar; braucht nur python3 oder das JDK-`jar`-Tool). Optionaler Gradle-Build weiter unten.

## Doku

- [Origins JSON-Referenz](https://origins.readthedocs.io/en/latest/json/origin/)
- [Origin Layer JSON](https://origins.readthedocs.io/en/latest/json/origin_layer/)
- [Upgrade JSON](https://origins.readthedocs.io/en/latest/json/upgrade/)
- [Conditioned Origins in Layern](https://origins.readthedocs.io/en/latest/guides/data/origin_conditions_in_layers/)
- [Apoli Power/Condition-Typen](https://apoli.readthedocs.io/)
