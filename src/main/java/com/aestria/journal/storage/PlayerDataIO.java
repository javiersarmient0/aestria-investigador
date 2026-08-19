package com.aestria.journal.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerDataIO {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final File PLAYER_FOLDER =
            new File("config/aestria-journal/playerdata");

    static {
        PLAYER_FOLDER.mkdirs();
    }

    public static void save(String uuid, PlayerJournalData data) {

        File file = new File(PLAYER_FOLDER, uuid + ".json");

        try (FileWriter writer = new FileWriter(file)) {

            GSON.toJson(data, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerJournalData load(String uuid) {

        File file = new File(PLAYER_FOLDER, uuid + ".json");

        if (!file.exists()) {
            return new PlayerJournalData();
        }

        try (FileReader reader = new FileReader(file)) {

            PlayerJournalData data =
                    GSON.fromJson(reader, PlayerJournalData.class);

            return data == null ? new PlayerJournalData() : data;

        } catch (IOException e) {
            e.printStackTrace();
            return new PlayerJournalData();
        }
    }

}