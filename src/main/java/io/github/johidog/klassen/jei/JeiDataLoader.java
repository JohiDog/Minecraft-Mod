package io.github.johidog.klassen.jei;

import com.google.gson.JsonParser;
import io.github.johidog.klassen.KlassenMod;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;

/**
 * Laedt JEI-Daten aus data/klassen/... direkt vom Classpath (funktioniert im Mod-Jar
 * und im Dev-Environment; der Client-ResourceManager sieht data/ naemlich nicht).
 */
public final class JeiDataLoader {

    private JeiDataLoader() {
    }

    public static List<JsonObject> loadJsonObjects(String dir) {
        List<JsonObject> out = new ArrayList<>();
        for (String path : listClasspathEntries(dir)) {
            if (!path.endsWith(".json")) continue;
            try (InputStream in = JeiDataLoader.class.getClassLoader().getResourceAsStream(path)) {
                if (in == null) continue;
                out.add(JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject());
            } catch (Exception e) {
                KlassenMod.LOGGER.warn("JEI-Datei {} konnte nicht gelesen werden: {}", path, e.getMessage());
            }
        }
        return out;
    }

    private static List<String> listClasspathEntries(String dir) {
        List<String> out = new ArrayList<>();
        try {
            Enumeration<URL> roots = JeiDataLoader.class.getClassLoader().getResources(dir);
            while (roots.hasMoreElements()) {
                URL url = roots.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                    try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:file:" + jarPath), Map.of())) {
                        Path dirPath = fs.getPath(dir);
                        if (Files.isDirectory(dirPath)) {
                            try (var stream = Files.list(dirPath)) {
                                stream.forEach(p -> out.add(dir + "/" + p.getFileName()));
                            }
                        }
                    }
                } else if ("file".equals(url.getProtocol())) {
                    Path dirPath = Paths.get(url.toURI());
                    if (Files.isDirectory(dirPath)) {
                        try (var stream = Files.list(dirPath)) {
                            stream.forEach(p -> out.add(dir + "/" + p.getFileName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            KlassenMod.LOGGER.warn("JEI-Verzeichnis {} nicht gefunden: {}", dir, e.getMessage());
        }
        return out;
    }
}
