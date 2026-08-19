package com.aestria.journal.util;

import com.aestria.journal.config.FileManager;
import com.aestria.journal.content.JournalCategory;
import com.aestria.journal.content.JournalDatabase;
import com.aestria.journal.content.JournalEntry;

public class StringUtils {

    public static String getBookString() {

        String title = FileManager.journalTitle;
        String author = FileManager.journalAuthor;

        return createBook(
                title,
                author,
                createMainPage()
        );
    }

    public static String getHistoryBookString() {

        String title = "Diario de Aestria - Historia";
        String author = FileManager.journalAuthor;

        String page = createHistoryPage();

        return createBook(
                title,
                author,
                page
        );
    }

    private static String createMainPage() {

        return "{"
                + "\"text\":\"§b§lDIARIO DE AESTRIA\\n\\n\","
                + "\"extra\":["
                + "{\"text\":\"§7Un registro de las investigaciones y descubrimientos de Aestria.\\n\\n\"},"

                + "{\"text\":\"§e§l[ HISTORIA ]\","
                + "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj historia\"},"
                + "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"Abrir Historia\"}}},"

                + "{\"text\":\"\\n\\n\"},"

                + "{\"text\":\"§a§l[ INVESTIGACIONES ]\","
                + "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj investigaciones\"},"
                + "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"Abrir Investigaciones\"}}},"

                + "{\"text\":\"\\n\\n\"},"

                + "{\"text\":\"§d§l[ REGIONES ]\","
                + "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aj regiones\"},"
                + "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"Explorar Regiones\"}}}"

                + "]"
                + "}";
    }

    private static String createHistoryPage() {

        StringBuilder page = new StringBuilder();

        page.append("{");
        page.append("\"text\":\"§e§l📖 HISTORIA\\n\\n\"");
        page.append(",\"extra\":[");

        JournalCategory historyCategory = JournalDatabase.getCategories()
                .stream()
                .filter(category -> category.getId().equals("historia"))
                .findFirst()
                .orElse(null);

        if (historyCategory == null) {

            page.append(
                    "{\"text\":\"§cNo hay investigaciones disponibles.\"}"
            );

        } else {

            boolean first = true;

            for (JournalEntry entry : historyCategory.getEntries()) {

                if (!first) {
                    page.append(",{\"text\":\"\\n\\n\"},");
                }

                first = false;

                page.append("{");
                page.append("\"text\":\"§f§l");
                page.append(escapeJson(entry.getTitle()));
                page.append("\",");
                page.append("\"clickEvent\":{");
                page.append("\"action\":\"run_command\",");
                page.append("\"value\":\"/aj abrir ");
                page.append(escapeCommand(entry.getId()));
                page.append("\"}");
                page.append("}");
            }
        }

        page.append("]}");

        return page.toString();
    }

    private static String createBook(
            String title,
            String author,
            String page
    ) {

        return "written_book{"
                + "title:'" + escape(title) + "',"
                + "author:'" + escape(author) + "',"
                + "generation:0,"
                + "pages:['"
                + page
                + "']"
                + "}";
    }

    private static String escape(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("'", "\\'");
    }

    private static String escapeJson(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String escapeCommand(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}