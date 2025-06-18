package net.pixeldreamstudios.projectileimmunityfix;

import net.fabricmc.api.ModInitializer;
import net.pixeldreamstudios.projectileimmunityfix.config.PlatformInit;
import net.pixeldreamstudios.projectileimmunityfix.config.ProjectileImmunityFixConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectileImmunityFix {
	public static final String MOD_ID = "kevs_projectile_immunity_fix";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		ProjectileImmunityFixConfig.load(PlatformInit.get().getConfigDir());
		LOGGER.info("Projectile Immunity Fix initialized!");
	}
}
