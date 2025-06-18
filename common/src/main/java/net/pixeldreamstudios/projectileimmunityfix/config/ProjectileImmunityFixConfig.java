package net.pixeldreamstudios.projectileimmunityfix.config;

import com.google.gson.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.pixeldreamstudios.projectileimmunityfix.ProjectileImmunityFix;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProjectileImmunityFixConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;

    public static ProjectileImmunityFixConfig INSTANCE = new ProjectileImmunityFixConfig();

    public MobProjectileConfig mobConfig = new MobProjectileConfig(true, 10, 20, 20);
    public PlayerProjectileConfig playerConfig = new PlayerProjectileConfig(false, 6, 40, 40); // Disabled by default

    // 🔁 Shared base class for common fields
    public static class BaseProjectileConfig {
        public boolean enabled;
        public int maxHitsBeforeCooldown;
        public int cooldownTicks;
        public int timeWithoutHitResetTicks;

        public BaseProjectileConfig() {}

        public BaseProjectileConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            this.enabled = enabled;
            this.maxHitsBeforeCooldown = maxHitsBeforeCooldown;
            this.cooldownTicks = cooldownTicks;
            this.timeWithoutHitResetTicks = timeWithoutHitResetTicks;
        }
    }

    // 👤 Player config: no entity-specific overrides
    public static class PlayerProjectileConfig extends BaseProjectileConfig {
        public PlayerProjectileConfig() {}

        public PlayerProjectileConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            super(enabled, maxHitsBeforeCooldown, cooldownTicks, timeWithoutHitResetTicks);
        }
    }

    // 🧟 Mob config: supports per-entity overrides
    public static class MobProjectileConfig extends BaseProjectileConfig {
        public List<PerEntityOverride> entities = new ArrayList<>();

        public MobProjectileConfig() {}

        public MobProjectileConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            super(enabled, maxHitsBeforeCooldown, cooldownTicks, timeWithoutHitResetTicks);
        }

        public static class PerEntityOverride {
            public String mobId;
            public int maxHitsBeforeCooldown;
            public int cooldownTicks;
            public int timeWithoutHitResetTicks;

            public PerEntityOverride() {}
        }
    }

    // 🧠 Picks correct config: player, mob, or specific override
    public BaseProjectileConfig getEffectiveConfig(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return playerConfig.enabled ? playerConfig : null;
        }

        if (!mobConfig.enabled) return null;

        String id = EntityType.getId(entity.getType()).toString();
        for (MobProjectileConfig.PerEntityOverride override : mobConfig.entities) {
            if (override.mobId.equals(id)) {
                return new MobProjectileConfig(
                        true,
                        override.maxHitsBeforeCooldown,
                        override.cooldownTicks,
                        override.timeWithoutHitResetTicks
                );
            }
        }

        return mobConfig;
    }

    public static void load(Path configDir) {
        configFile = configDir.resolve("projectileimmunityfix.json").toFile();
        ProjectileImmunityFix.LOGGER.info("Loading config from: " + configFile.getAbsolutePath());

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                if (json.has("mobConfig")) {
                    INSTANCE.mobConfig = GSON.fromJson(json.get("mobConfig"), MobProjectileConfig.class);
                }

                if (json.has("playerConfig")) {
                    INSTANCE.playerConfig = GSON.fromJson(json.get("playerConfig"), PlayerProjectileConfig.class);
                }

                save(); // Rewrite to ensure all fields exist
            } catch (IOException e) {
                ProjectileImmunityFix.LOGGER.error("Error reading config file", e);
            }
        } else {
            ProjectileImmunityFix.LOGGER.info("Config file not found. Creating new one.");
            save();
        }
    }

    public static void save() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
