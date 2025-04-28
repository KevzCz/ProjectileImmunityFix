package net.pixeldreamstudios.projectileimmunityfix;

import net.fabricmc.api.ModInitializer;
import net.pixeldreamstudios.projectileimmunityfix.config.ProjectileImmunityFixConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectileImmunityFix implements ModInitializer {
	public static final String MOD_ID = "projectileimmunityfix";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ProjectileImmunityFixConfig.load();
		LOGGER.info("Projectile Immunity Fix initialized!");
	}
}
