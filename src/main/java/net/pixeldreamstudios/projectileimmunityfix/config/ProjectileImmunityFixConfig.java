package net.pixeldreamstudios.projectileimmunityfix.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectileImmunityFixConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "projectileimmunityfix.json");

    // CONFIG VALUES
    public int maxHitsBeforeCooldown = 10;
    public int cooldownTicks = 20;
    public int timeWithoutHitResetTicks = 20;

    public static ProjectileImmunityFixConfig INSTANCE = new ProjectileImmunityFixConfig();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                boolean changed = false;

                if (json.has("maxHitsBeforeCooldown")) {
                    INSTANCE.maxHitsBeforeCooldown = json.get("maxHitsBeforeCooldown").getAsInt();
                } else {
                    changed = true;
                }

                if (json.has("cooldownTicks")) {
                    INSTANCE.cooldownTicks = json.get("cooldownTicks").getAsInt();
                } else {
                    changed = true;
                }

                if (json.has("timeWithoutHitResetTicks")) {
                    INSTANCE.timeWithoutHitResetTicks = json.get("timeWithoutHitResetTicks").getAsInt();
                } else {
                    changed = true;
                }

                if (changed) {
                    save();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
