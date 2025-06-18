package net.pixeldreamstudios.projectileimmunityfix.platform.neoforge;

import net.neoforged.fml.loading.FMLConfig;
import net.pixeldreamstudios.projectileimmunityfix.config.PlatformHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDir() {
        // Hardcoded to match NeoForge's actual config location
        return Paths.get(".", "config").toAbsolutePath().normalize();
    }
}
