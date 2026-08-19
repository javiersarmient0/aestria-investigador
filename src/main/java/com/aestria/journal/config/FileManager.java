package com.aestria.journal.config;

import com.aestria.journal.AestriaJournal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileManager {
    private static final Logger LOGGER = LogManager.getLogger("AestriaJournal");
    private static final String CONFIG_DIR = "config/travelers-journal";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.txt";
    private static final String CONTENT_FILE = CONFIG_DIR + "/travelers_journal.md";
    private static final String JOIN_LIST_FILE = CONFIG_DIR + "/joinlist.txt";
    
    public static List<String> lines = new ArrayList<>();
    public static String journalTitle = "Traveler's Journal";
    public static String journalAuthor = "Server";
    public static final int MAX_LINES_PER_PAGE = 15;
    public static final int MAX_CHARS_PER_LINE = 25;
    public static final int MAX_CHARS_PER_PAGE = MAX_LINES_PER_PAGE * MAX_CHARS_PER_LINE;
    public static boolean shouldGiveSpawn = false;
    public static List<String> joinList = new ArrayList<>();
    public static List<String> legacyTitles = new ArrayList<>();
    
    public static final Map<String, String> COLORS = new HashMap<String, String>() {{
        put("black", "black");
        put("dark_blue", "dark_blue");
        put("dark_green", "dark_green");
        put("dark_aqua", "dark_aqua");
        put("dark_red", "dark_red");
        put("dark_purple", "dark_purple");
        put("gold", "gold");
        put("gray", "gray");
        put("dark_gray", "dark_gray");
        put("blue", "blue");
        put("green", "green");
        put("aqua", "aqua");
        put("red", "red");
        put("light_purple", "light_purple");
        put("yellow", "yellow");
        put("white", "white");
    }};

    public static void readFiles() {
        lines = new ArrayList<>();
        legacyTitles = new ArrayList<>();

        try {
            File directory = new File(CONFIG_DIR);
            directory.mkdirs();

            // Content file
            File contentFile = new File(CONTENT_FILE);
            if(contentFile.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(contentFile));
                writer.write("# Welcome to the Server!\n\n");
                writer.write("This is your Traveler's Journal. Use it to record your adventures and discoveries.");
                writer.close();
            } else {
                Scanner reader = new Scanner(contentFile);
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    lines.add(line);
                }
                reader.close();
            }

            // Config file
            File configFile = new File(CONFIG_FILE);
            if(configFile.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
                writer.write("title=Traveler's Journal\n");
                writer.write("author=Server\n");
                writer.write("give_at_spawn=true\n");
                writer.write("debug_logging=false\n");
                writer.write("legacy_titles=Traveller's Journal,Travelers Journal\n");
                writer.close();
            } else {
                Scanner reader = new Scanner(configFile);
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();

                    if(line.startsWith("title=")) {
                        journalTitle = line.substring(6);
                        AestriaJournal.logDebug("Set journal title to: '{}'", journalTitle);
                    }
                    else if(line.startsWith("author=")) {
                        journalAuthor = line.substring(7);
                        AestriaJournal.logDebug("Set journal author to: '{}'", journalAuthor);
                    }
                    else if(line.startsWith("give_at_spawn=")) {
                        shouldGiveSpawn = line.substring(14).equals("true");
                        AestriaJournal.logDebug("Set give_at_spawn to: {}", shouldGiveSpawn);
                    }
                    else if(line.startsWith("debug_logging=")) {
                        String debugValue = line.substring(line.indexOf('=') + 1).trim();
                        AestriaJournal.debugLoggingEnabled = debugValue.equals("true");
                        AestriaJournal.logDebug("Set debugLoggingEnabled to: {}", AestriaJournal.debugLoggingEnabled);
                    }
                    else if(line.startsWith("legacy_titles=")) {
                        String titlesStr = line.substring("legacy_titles=".length());
                        String[] titles = titlesStr.split(",");
                        for (String title : titles) {
                            String trimmedTitle = title.trim();
                            if (!trimmedTitle.isEmpty()) {
                                legacyTitles.add(trimmedTitle);
                            }
                        }
                        AestriaJournal.logDebug("Added legacy titles: {}", legacyTitles);
                    }
                }
                reader.close();
            }

            // Join list file
            File joinListFile = new File(JOIN_LIST_FILE);
            if(joinListFile.exists()) {
                Scanner reader = new Scanner(joinListFile);
                while (reader.hasNextLine()) {
                    joinList.add(reader.nextLine().trim());
                }
                reader.close();
            }

        } catch (Exception e) {
            AestriaJournal.logDebug("[TJ] An error occurred: {}", e.getMessage());
            e.printStackTrace();
        }

        AestriaJournal.logDebug("[TJ] Loaded lines: {}", lines);
    }

    public static void writeJoinList(String uuid) {
        try {
            File joinListFile = new File(JOIN_LIST_FILE);
            joinListFile.createNewFile();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(joinListFile, true));
            writer.write(uuid + "\n");
            writer.close();
            
            joinList.add(uuid);
        } catch (IOException e) {
            AestriaJournal.logError("[TJ] Failed to write to join list: {}", e.getMessage());
        }
    }
}
