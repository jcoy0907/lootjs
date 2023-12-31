package com.almostreliable.lootjs;

import java.util.ServiceLoader;

public class PlatformLoader {
    static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LootJS.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}