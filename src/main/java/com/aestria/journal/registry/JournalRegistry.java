package com.aestria.journal.registry;

import com.aestria.journal.content.JournalCategory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class JournalRegistry {

    private final Map<String, JournalCategory> categories = new LinkedHashMap<>();

    public void registerCategory(JournalCategory category) {
        categories.put(category.getId(), category);
    }

    public JournalCategory getCategory(String id) {
        return categories.get(id);
    }

    public Collection<JournalCategory> getCategories() {
        return categories.values();
    }

    public void clear() {
        categories.clear();
    }
}