package com.aestria.journal.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentLoader {

    private static final String NAMESPACE = "aestria_journal";
    private static final String ENTRY_DIRECTORY = "entries";

    public void load(ResourceManager resourceManager) {

        System.out.println("[Aestria Journal] Cargando investigaciones...");

        JournalDatabase.clear();

        try {

            Map<Identifier, Resource> resources =
                    resourceManager.findResources(
                            ENTRY_DIRECTORY,
                            path -> path.getPath().endsWith(".json")
                    );

            for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {

                Identifier identifier = resourceEntry.getKey();
                Resource resource = resourceEntry.getValue();

                loadEntry(identifier, resource);
            }

            System.out.println(
                    "[Aestria Journal] Investigaciones cargadas: "
                            + JournalDatabase.getEntries().size()
            );

        } catch (Exception e) {

            System.err.println(
                    "[Aestria Journal] Error cargando investigaciones."
            );

            e.printStackTrace();
        }
    }

    private void loadEntry(Identifier identifier, Resource resource) {

        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader =
                     new InputStreamReader(
                             inputStream,
                             StandardCharsets.UTF_8
                     )) {

            JsonObject json =
                    JsonParser.parseReader(reader).getAsJsonObject();

            String id =
                    json.get("id").getAsString();

            String title =
                    json.get("title").getAsString();

            JsonObject unlock =
                    json.getAsJsonObject("unlock");

            String unlockType =
                    unlock.get("type").getAsString();

            String unlockId =
                    unlock.get("id").getAsString();

            JsonArray contentArray =
                    json.getAsJsonArray("content");

            List<String> content =
                    contentArray.asList()
                            .stream()
                            .map(element -> element.getAsString())
                            .collect(Collectors.toList());

            JournalEntry entry =
                    new JournalEntry(
                            id,
                            title,
                            unlockType,
                            unlockId,
                            content
                    );

            String categoryId =
                    getCategoryFromPath(identifier);

            JournalCategory category =
                    JournalDatabase.getCategories()
                            .stream()
                            .filter(existing ->
                                    existing.getId().equals(categoryId)
                            )
                            .findFirst()
                            .orElse(null);

            if (category == null) {

                category =
                        new JournalCategory(
                                categoryId,
                                formatCategoryTitle(categoryId)
                        );

                JournalDatabase.registerCategory(category);
            }

            JournalDatabase.registerEntry(
                    entry,
                    categoryId
            );

            System.out.println(
                    "[Aestria Journal] Investigación cargada: "
                            + id
                            + " | categoría: "
                            + categoryId
            );

        } catch (Exception e) {

            System.err.println(
                    "[Aestria Journal] No se pudo cargar: "
                            + identifier
            );

            e.printStackTrace();
        }
    }

    private String getCategoryFromPath(Identifier identifier) {

        String path = identifier.getPath();

        String prefix = ENTRY_DIRECTORY + "/";

        if (!path.startsWith(prefix)) {
            return "general";
        }

        String remaining =
                path.substring(prefix.length());

        int separator =
                remaining.indexOf('/');

        if (separator == -1) {
            return "general";
        }

        return remaining.substring(0, separator);
    }

    private String formatCategoryTitle(String categoryId) {

        if (categoryId == null || categoryId.isEmpty()) {
            return "General";
        }

        String formatted =
                categoryId.replace("_", " ");

        return Character.toUpperCase(formatted.charAt(0))
                + formatted.substring(1);
    }
}