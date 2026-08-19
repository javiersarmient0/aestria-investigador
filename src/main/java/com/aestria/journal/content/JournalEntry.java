package com.aestria.journal.content;

import java.util.List;

public class JournalEntry {

    private final String id;
    private final String title;
    private final String unlockType;
    private final String unlockId;
    private final List<String> content;

    public JournalEntry(
            String id,
            String title,
            String unlockType,
            String unlockId,
            List<String> content
    ) {
        this.id = id;
        this.title = title;
        this.unlockType = unlockType;
        this.unlockId = unlockId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUnlockType() {
        return unlockType;
    }

    public String getUnlockId() {
        return unlockId;
    }

    public List<String> getContent() {
        return content;
    }
}