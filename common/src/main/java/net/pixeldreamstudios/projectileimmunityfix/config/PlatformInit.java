package net.pixeldreamstudios.projectileimmunityfix.config;

import java.util.ServiceLoader;

public class PlatformInit {
    private static final PlatformHelper INSTANCE = ServiceLoader.load(PlatformHelper.class)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No net.pixeldreamstudios.projectileimmunityfix.config.PlatformHelper implementation found"));

    public static PlatformHelper get() {
        return INSTANCE;
    }
}