package com.aestria.journal.storage;

import java.util.HashSet;
import java.util.Set;

public class PlayerJournalData {

    private final Set<String> unlockedEntries = new HashSet<>();

    public boolean unlock(String id) {
        return unlockedEntries.add(id);
    }

    public boolean isUnlocked(String id) {
        return unlockedEntries.contains(id);
    }

    public Set<String> getUnlockedEntries() {
        return unlockedEntries;
    }

    public void clear() {
        unlockedEntries.clear();
    }
}