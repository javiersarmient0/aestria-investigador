package com.aestria.journal.content.data;

import java.util.List;

public class EntryData {

    private String id;
    private String title;
    private UnlockData unlock;
    private List<String> content;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public UnlockData getUnlock() {
        return unlock;
    }

    public List<String> getContent() {
        return content;
    }
}