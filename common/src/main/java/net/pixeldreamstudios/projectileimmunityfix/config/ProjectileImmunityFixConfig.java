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

    public ProjectileMobConfig projectileMobConfig   = new ProjectileMobConfig(true, 10, 20, 20);
    public ProjectilePlayerConfig projectilePlayerConfig = new ProjectilePlayerConfig(false, 6, 40, 40);

    public MeleeMobConfig meleeMobConfig             = new MeleeMobConfig(false, 10, 20, 20);
    public MeleePlayerConfig meleePlayerConfig       = new MeleePlayerConfig(false, 6, 40, 40);

    public static class BaseStackingIFrameConfig {
        public boolean enabled;
        public int maxHitsBeforeCooldown;
        public int cooldownTicks;
        public int timeWithoutHitResetTicks;

        public BaseStackingIFrameConfig() {}

        public BaseStackingIFrameConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            this.enabled = enabled;
            this.maxHitsBeforeCooldown = maxHitsBeforeCooldown;
            this.cooldownTicks = cooldownTicks;
            this.timeWithoutHitResetTicks = timeWithoutHitResetTicks;
        }
    }

    public static class ProjectilePlayerConfig extends BaseStackingIFrameConfig {
        public ProjectilePlayerConfig() {}
        public ProjectilePlayerConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            super(enabled, maxHitsBeforeCooldown, cooldownTicks, timeWithoutHitResetTicks);
        }
    }

    public static class ProjectileMobConfig extends BaseStackingIFrameConfig {
        public List<PerEntityOverride> entities = new ArrayList<>();
        public ProjectileMobConfig() {}
        public ProjectileMobConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            super(enabled, maxHitsBeforeCooldown, cooldownTicks, timeWithoutHitResetTicks);
        }
    }

    public static class MeleePlayerConfig extends BaseStackingIFrameConfig {
        public MeleePlayerConfig() {}
        public MeleePlayerConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            super(enabled, maxHitsBeforeCooldown, cooldownTicks, timeWithoutHitResetTicks);
        }
    }

    public static class MeleeMobConfig extends BaseStackingIFrameConfig {
        public List<PerEntityOverride> entities = new ArrayList<>();
        public MeleeMobConfig() {}
        public MeleeMobConfig(boolean enabled, int maxHitsBeforeCooldown, int cooldownTicks, int timeWithoutHitResetTicks) {
            super(enabled, maxHitsBeforeCooldown, cooldownTicks, timeWithoutHitResetTicks);
        }
    }

    public static class PerEntityOverride {
        public String mobId;
        public int maxHitsBeforeCooldown;
        public int cooldownTicks;
        public int timeWithoutHitResetTicks;
        public PerEntityOverride() {}
    }

    public BaseStackingIFrameConfig getEffectiveProjectileConfig(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return projectilePlayerConfig.enabled ? projectilePlayerConfig : null;
        }
        if (!projectileMobConfig.enabled) return null;

        String id = EntityType.getId(entity.getType()).toString();
        for (PerEntityOverride override : projectileMobConfig.entities) {
            if (override.mobId.equals(id)) {
                return new BaseStackingIFrameConfig(
                        true,
                        override.maxHitsBeforeCooldown,
                        override.cooldownTicks,
                        override.timeWithoutHitResetTicks
                );
            }
        }
        return projectileMobConfig;
    }

    public BaseStackingIFrameConfig getEffectiveMeleeConfig(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return meleePlayerConfig.enabled ? meleePlayerConfig : null;
        }
        if (!meleeMobConfig.enabled) return null;

        String id = EntityType.getId(entity.getType()).toString();
        for (PerEntityOverride override : meleeMobConfig.entities) {
            if (override.mobId.equals(id)) {
                return new BaseStackingIFrameConfig(
                        true,
                        override.maxHitsBeforeCooldown,
                        override.cooldownTicks,
                        override.timeWithoutHitResetTicks
                );
            }
        }
        return meleeMobConfig;
    }

    public static void load(Path configDir) {
        configFile = configDir.resolve("projectileimmunityfix.json").toFile();
        ProjectileImmunityFix.LOGGER.info("Loading config from: " + configFile.getAbsolutePath());

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                if (json.has("projectileMobConfig")) {
                    INSTANCE.projectileMobConfig = GSON.fromJson(json.get("projectileMobConfig"), ProjectileMobConfig.class);
                }
                if (json.has("projectilePlayerConfig")) {
                    INSTANCE.projectilePlayerConfig = GSON.fromJson(json.get("projectilePlayerConfig"), ProjectilePlayerConfig.class);
                }
                if (json.has("meleeMobConfig")) {
                    INSTANCE.meleeMobConfig = GSON.fromJson(json.get("meleeMobConfig"), MeleeMobConfig.class);
                }
                if (json.has("meleePlayerConfig")) {
                    INSTANCE.meleePlayerConfig = GSON.fromJson(json.get("meleePlayerConfig"), MeleePlayerConfig.class);
                }

                if (json.has("mobConfig") || json.has("playerConfig")) {
                    if (json.has("mobConfig") && !json.has("projectileMobConfig")) {
                        INSTANCE.projectileMobConfig = GSON.fromJson(json.get("mobConfig"), ProjectileMobConfig.class);
                    }
                    if (json.has("playerConfig") && !json.has("projectilePlayerConfig")) {
                        INSTANCE.projectilePlayerConfig = GSON.fromJson(json.get("playerConfig"), ProjectilePlayerConfig.class);
                    }
                }

                save();
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
