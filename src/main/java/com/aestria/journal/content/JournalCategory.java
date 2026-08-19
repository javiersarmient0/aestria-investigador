package com.aestria.journal.content;

import java.util.ArrayList;
import java.util.List;

public class JournalCategory {

    private final String id;
    private final String title;

    private final List<JournalEntry> entries = new ArrayList<>();

    public JournalCategory(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void addEntry(JournalEntry entry) {
        entries.add(entry);
    }

    public List<JournalEntry> getEntries() {
        return entries;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}