package com.aestria.journal.api;

import com.aestria.journal.storage.PlayerJournalManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class JournalAPI {

    public static boolean unlock(ServerPlayerEntity player, String entryId) {

        if (PlayerJournalManager.hasEntry(player, entryId)) {
            return false;
        }

        PlayerJournalManager.unlock(player, entryId);

        player.sendMessage(
                Text.literal("📖 Nueva investigación registrada."),
                false
        );

        return true;
    }

    public static boolean hasUnlocked(ServerPlayerEntity player, String entryId) {
        return PlayerJournalManager.hasEntry(player, entryId);
    }

}