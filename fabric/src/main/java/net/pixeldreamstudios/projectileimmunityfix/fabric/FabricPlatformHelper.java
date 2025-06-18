package net.pixeldreamstudios.projectileimmunityfix.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.pixeldreamstudios.projectileimmunityfix.config.PlatformHelper;

import java.nio.file.Path;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}