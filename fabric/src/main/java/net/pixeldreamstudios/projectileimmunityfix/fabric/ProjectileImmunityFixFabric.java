package net.pixeldreamstudios.projectileimmunityfix.fabric;

import net.fabricmc.api.ModInitializer;


import net.fabricmc.loader.api.FabricLoader;
import net.pixeldreamstudios.projectileimmunityfix.ProjectileImmunityFix;
import net.pixeldreamstudios.projectileimmunityfix.config.ProjectileImmunityFixConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectileImmunityFixFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ProjectileImmunityFixConfig.load(FabricLoader.getInstance().getConfigDir());
        ProjectileImmunityFix.init();
    }
}
