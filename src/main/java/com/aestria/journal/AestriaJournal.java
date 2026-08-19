package com.aestria.journal;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

import com.aestria.journal.content.JournalLoader;
import com.aestria.journal.config.FileManager;
import com.aestria.journal.content.JournalDatabase;
import com.aestria.journal.content.JournalEntry;
import com.aestria.journal.util.StringUtils;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.command.argument.StringArgumentType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AestriaJournal implements ModInitializer {

    public static final String MOD_ID = "travelers-journal";

    private static final Logger LOGGER =
            LogManager.getLogger("TravelersJournal");

    public static boolean debugLoggingEnabled = false;

    public static void logDebug(String message, Object... params) {
        if (debugLoggingEnabled) {
            LOGGER.info("[TJ Debug] " + message, params);
        }
    }

    public static void logError(String message, Object... params) {
        LOGGER.error(message, params);
    }

    @Override
    public void onInitialize() {

        // Load configuration
        FileManager.readFiles();

        // Register commands
        registerCommands();

        // Register events
        registerEvents();

        // Register journal data loader
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new JournalLoader());

        logDebug("Travelers Journal initialized");

        System.out.println(
                "[Aestria Journal] Sistema inicializado."
        );
    }

    private void registerCommands() {

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {

                    /*
                     * COMANDO DE PRUEBA
                     *
                     * /aj
                     * /aj test
                     */
                    dispatcher.register(
                            CommandManager.literal("aj")
                                    .then(
                                            CommandManager.literal("test")
                                                    .executes(context -> {

                                                        ServerCommandSource source =
                                                                context.getSource();

                                                        JournalEntry entry =
                                                                JournalDatabase.getEntry(
                                                                        "cultivos"
                                                                );

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

                    dispatcher.register(
      CommandManager.literal("aj")
        .then(
                CommandManager.literal("historia")
                        .executes(context -> {

                            ServerCommandSource source =
                                    context.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                                return 0;
                            }

                            player.getServer()
                                    .getCommandManager()
                                    .executeWithPrefix(
                                            player.getServer().getCommandSource(),
                                            "give "
                                                    + player.getName().getString()
                                                    + " "
                                                    + StringUtils.getHistoryBookString()
                                    );

                            return 1;
                        })
        )
        .then(
        CommandManager.literal("abrir")
                .then(
                        CommandManager.argument("id", StringArgumentType.word())
                                .executes(context -> {

                                    ServerCommandSource source =
                                            context.getSource();

                                    if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                                        return 0;
                                    }

                                    String id =
                                            StringArgumentType.getString(
                                                    context,
                                                    "id"
                                            );

                                    JournalEntry entry =
                                            JournalDatabase.getEntry(id);

                                    if (entry == null) {
                                        source.sendError(
                                                Text.literal(
                                                        "No se encontró la investigación: " + id
                                                )
                                        );
                                        return 0;
                                    }

                                    String book =
                                            StringUtils.getEntryBookString(entry);

                                    player.getServer()
                                            .getCommandManager()
                                            .executeWithPrefix(
                                                    player.getServer().getCommandSource(),
                                                    "give "
                                                            + player.getName().getString()
                                                            + " "
                                                            + book
                                            );

                                    return 1;
                                })
                )
)
);
                    /*
                     * COMANDO PRINCIPAL DEL DIARIO
                     */
                    dispatcher.register(
                            CommandManager.literal("travelers_journal")
                                    .requires(source -> {

                                        if (source instanceof ServerCommandSource serverSource) {
                                            return serverSource.hasPermissionLevel(2);
                                        }

                                        return false;
                                    })

                                    .then(
                                            CommandManager.literal("reload")
                                                    .executes(context -> {

                                                        FileManager.readFiles();

                                                        context.getSource()
                                                                .sendFeedback(
                                                                        () -> Text.literal(
                                                                                "Reloaded journal files"
                                                                        ),
                                                                        false
                                                                );

                                                        return 1;
                                                    })
                                    )

                                    .then(
                                            CommandManager.literal("give")
                                                    .then(
                                                            CommandManager.argument(
                                                                    "targets",
                                                                    EntityArgumentType.players()
                                                            )
                                                            .executes(context -> {

                                                                for (
                                                                        ServerPlayerEntity target :
                                                                        EntityArgumentType.getPlayers(
                                                                                context,
                                                                                "targets"
                                                                        )
                                                                ) {

                                                                    giveBookToPlayer(target);
                                                                }

                                                                context.getSource()
                                                                        .sendFeedback(
                                                                                () -> Text.literal(
                                                                                        "Given journal to specified players"
                                                                                ),
                                                                                false
                                                                        );

                                                                return 1;
                                                            })
                                                    )
                                    )
                    );
                }
        );
    }

    private void registerEvents() {

        // Give new players the journal
        ServerEntityEvents.ENTITY_LOAD.register(
                (entity, world) -> {

                    if (entity instanceof ServerPlayerEntity player) {

                        if (
                                !FileManager.joinList.contains(
                                        player.getUuidAsString()
                                )
                                &&
                                !player.getInventory().containsAny(
                                        itemStack -> isJournalBook(itemStack)
                                )
                        ) {

                            giveBookToPlayer(player);

                            FileManager.writeJoinList(
                                    player.getUuidAsString()
                            );
                        }
                    }
                }
        );

        // Update journal when used
        UseItemCallback.EVENT.register(
                (player, world, hand) -> {

                    if (player instanceof ServerPlayerEntity serverPlayer) {

                        ItemStack stack =
                                player.getStackInHand(hand);

                        if (
                                stack.getItem().equals(Items.WRITTEN_BOOK)
                                &&
                                isJournalBook(stack)
                        ) {

                            player.setStackInHand(
                                    hand,
                                    ItemStack.EMPTY
                            );

                            replaceBookInPlayerInv(
                                    serverPlayer,
                                    player.getInventory().selectedSlot
                            );

                            return TypedActionResult.pass(
                                    player.getStackInHand(hand)
                            );
                        }
                    }

                    return TypedActionResult.pass(
                            player.getStackInHand(hand)
                    );
                }
        );
    }

    private boolean isJournalBook(ItemStack stack) {

        if (!stack.getItem().equals(Items.WRITTEN_BOOK)) {
            return false;
        }

        String bookTitle =
                stack.getName().getString();

        logDebug(
                "Checking book title: '{}' against current title: '{}' and legacy titles: {}",
                bookTitle,
                FileManager.journalTitle,
                FileManager.legacyTitles
        );

        if (
                bookTitle.equalsIgnoreCase(
                        FileManager.journalTitle
                )
        ) {

            logDebug("Matched current title");

            return true;
        }

        for (String legacyTitle : FileManager.legacyTitles) {

            if (
                    bookTitle.equalsIgnoreCase(
                            legacyTitle
                    )
            ) {

                logDebug(
                        "Matched legacy title: {}",
                        legacyTitle
                );

                return true;
            }
        }

        logDebug("No title match found");

        return false;
    }

    public static void replaceBookInPlayerInv(
            ServerPlayerEntity player,
            int slot
    ) {

        player.getServer()
                .getCommandManager()
                .executeWithPrefix(
                        player.getServer()
                                .getCommandSource(),

                        "item replace entity "
                                + player.getName().getString()
                                + " container."
                                + slot
                                + " with "
                                + StringUtils.getBookString()
                );
    }

    public static void giveBookToPlayer(
            ServerPlayerEntity player
    ) {

        player.getServer()
                .getCommandManager()
                .executeWithPrefix(
                        player.getServer()
                                .getCommandSource(),

                        "give "
                                + player.getName().getString()
                                + " "
                                + StringUtils.getBookString()
                );
    }
}