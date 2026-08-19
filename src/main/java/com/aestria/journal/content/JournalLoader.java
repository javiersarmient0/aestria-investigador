package com.aestria.journal.content;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class JournalLoader implements IdentifiableResourceReloadListener {

    private final ContentLoader contentLoader;

    public JournalLoader() {
        this.contentLoader = new ContentLoader();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(
                "aestria_journal",
                "journal_loader"
        );
    }

    @Override
    public CompletableFuture<Void> reload(
            ResourceReloader.Synchronizer synchronizer,
            ResourceManager resourceManager,
            Profiler prepareProfiler,
            Profiler applyProfiler,
            Executor prepareExecutor,
            Executor applyExecutor
    ) {
        return CompletableFuture.runAsync(
                () -> {
                    System.out.println(
                            "[Aestria Journal] Recargando investigaciones..."
                    );

                    contentLoader.load(resourceManager);
                },
                prepareExecutor
        ).thenCompose(synchronizer::whenPrepared);
    }
}