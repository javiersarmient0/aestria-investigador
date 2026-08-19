package com.aestria.journal.command;

import com.aestria.journal.content.JournalDatabase;
import com.aestria.journal.content.JournalEntry;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class JournalTestCommand {

    public static void register() {

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {

                    dispatcher.register(
                            CommandManager.literal("aj")
                                    .then(
                                            CommandManager.literal("test")
                                                    .executes(context -> {

                                                        ServerCommandSource source =
                                                                context.getSource();

                                                        JournalEntry entry =
                                                                JournalDatabase.getEntry("cultivos");

                                                        if (entry == null) {

                                                            source.sendError(
                                                                    Text.literal(
                                                                            "No se encontró la investigación 'cultivos'."
                                                                    )
                                                            );

                                                            return 0;
                                                        }

                                                        source.sendFeedback(
                                                                () -> Text.literal(
                                                                        "=== Aestria Journal ==="
                                                                ),
                                                                false
                                                        );

                                                        source.sendFeedback(
                                                                () -> Text.literal(
                                                                        "ID: " + entry.getId()
                                                                ),
                                                                false
                                                        );

                                                        source.sendFeedback(
                                                                () -> Text.literal(
                                                                        "Título: " + entry.getTitle()
                                                                ),
                                                                false
                                                        );

                                                        return 1;
                                                    })
                                    )
                    );
                }
        );
    }
}