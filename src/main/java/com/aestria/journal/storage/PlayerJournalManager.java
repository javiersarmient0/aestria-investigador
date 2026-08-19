package com.aestria.journal.storage;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJournalManager {

    private static final Map<UUID, PlayerJournalData> PLAYERS = new HashMap<>();

    public static PlayerJournalData get(ServerPlayerEntity player) {
        return PLAYERS.computeIfAbsent(
                player.getUuid(),
                uuid -> PlayerDataIO.load(uuid.toString())
        );
    }

    public static void unlock(ServerPlayerEntity player, String entryId) {

        PlayerJournalData data = get(player);

        if (data.unlock(entryId)) {
            PlayerDataIO.save(player.getUuidAsString(), data);
        }
    }

    public static boolean hasEntry(ServerPlayerEntity player, String entryId) {
        return get(player).isUnlocked(entryId);
    }

    public static void save(ServerPlayerEntity player) {
        PlayerDataIO.save(player.getUuidAsString(), get(player));
    }

    public static void unload(ServerPlayerEntity player) {
        save(player);
        PLAYERS.remove(player.getUuid());
    }

}