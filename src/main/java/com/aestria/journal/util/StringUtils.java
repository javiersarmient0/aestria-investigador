package com.aestria.journal.util;

import com.aestria.journal.config.FileManager;
import com.aestria.journal.content.JournalCategory;
import com.aestria.journal.content.JournalDatabase;
import com.aestria.journal.content.JournalEntry;

import java.util.List;

public class StringUtils {

    public static String getBookString() {
        return createBook(FileManager.journalTitle, FileManager.journalAuthor, createMainPage());
    }

    public static String getHistoryBookString() {
        return createBook("Diario de Aestria - Historia", FileManager.journalAuthor, createHistoryPage());
    }

    public static String getEntryBookString(JournalEntry entry) {
        if (entry == null) return getBookString();

        StringBuilder page = new StringBuilder();
        page.append("{\"text\":\"");
        page.append(escapeJson(entry.getTitle()));
        page.append("\",\"extra\":[");

        List<String> content = entry.getContent();
        if (content == null || content.isEmpty()) {
            page.append("{\"text\":\"No hay contenido disponible.\"}");
        } else {
            boolean first = true;
            for (String line : content) {
                if (!first) page.append(",");
                first = false;
                page.append("{\"text\":\"");
                page.append(escapeJson(line));
                page.append("\"}");
            }
        }
        page.append("]}");

        return createBook(entry.getTitle(), FileManager.journalAuthor, page.toString());
    }

    private static String createMainPage() {
        return "{"
                + "\"text\":\"DIARIO DE AESTRIA\","
                + "\"extra\":["
                + "{\"text\":\"Un registro de las investigaciones y descubrimientos de Aestria.\"},"
                + "{\"text\":\" [ HISTORIA ]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj historia\"}},"
                + "{\"text\":\" [ INVESTIGACIONES ]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj investigaciones\"}},"
                + "{\"text\":\" [ REGIONES ]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj regiones\"}}"
                + "]}";
    }

    private static String createHistoryPage() {
        StringBuilder page = new StringBuilder();
        page.append("{\"text\":\"HISTORIA\",\"extra\":[");

        JournalCategory historyCategory = JournalDatabase.getCategories().stream()
                .filter(category -> category.getId().equals("historia"))
                .findFirst().orElse(null);

        if (historyCategory == null || historyCategory.getEntries().isEmpty()) {
            page.append("{\"text\":\"No hay investigaciones disponibles.\"}");
        } else {
            boolean first = true;
            for (JournalEntry entry : historyCategory.getEntries()) {
                if (!first) page.append(",");
                first = false;
                page.append("{\"text\":\"");
                page.append(escapeJson(entry.getTitle()));
                page.append("\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj abrir ");
                page.append(escapeCommand(entry.getId()));
                page.append("\"}}");
            }
        }

        page.append("]}");
        return page.toString();
    }

    private static String createBook(String title, String author, String page) {
        return "written_book[written_book_content={"
                + "title:'" + escapeComponentText(title) + "',"
                + "author:'" + escapeComponentText(author) + "',"
                + "generation:0,"
                + "pages:['" + escapePageForComponent(page) + "']"
                + "}]";
    }

    private static String escapeComponentText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("'", "\\'");
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    private static String escapePageForComponent(String page) {
        if (page == null) return "{\"text\":\"\"}";
        return page.replace("\\", "\\\\").replace("'", "\\'");
    }

    private static String escapeCommand(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'");
    }
}
