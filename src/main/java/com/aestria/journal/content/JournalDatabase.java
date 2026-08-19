package com.aestria.journal.content;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class JournalDatabase {

    private static final Map<String, JournalEntry> ENTRIES = new LinkedHashMap<>();

    private static final Map<String, JournalCategory> CATEGORIES = new LinkedHashMap<>();

    public static void registerCategory(JournalCategory category) {
        CATEGORIES.put(category.getId(), category);
    }

    public static void registerEntry(JournalEntry entry, String categoryId) {

        ENTRIES.put(entry.getId(), entry);

        JournalCategory category = CATEGORIES.get(categoryId);

        if (category != null) {
            category.addEntry(entry);
        }
    }

    public static JournalEntry getEntry(String id) {
        return ENTRIES.get(id);
    }

    public static Collection<JournalEntry> getEntries() {
        return ENTRIES.values();
    }

    public static Collection<JournalCategory> getCategories() {
        return CATEGORIES.values();
    }

    public static void clear() {
        ENTRIES.clear();
        CATEGORIES.clear();
    }
}