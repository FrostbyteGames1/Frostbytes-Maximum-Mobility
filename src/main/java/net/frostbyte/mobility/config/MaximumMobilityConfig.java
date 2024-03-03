package net.frostbyte.mobility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaximumMobilityConfig {

    public final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("frostbyte");
    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/maximum-mobility.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    float stepUp = 1.25F;
    float boatStepUp = 1.0F;
    int coyoteTime = 10;
    boolean reachAround = true;

    public void write() {
        try {
            if (Files.notExists(configDir)) {
                Files.createDirectory(configDir);
            }
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("stepUp", stepUp);
            json.addProperty("boatStepUp", boatStepUp);
            json.addProperty("coyoteTime", coyoteTime);
            json.addProperty("reachAround", reachAround);
            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        try {
            if (Files.notExists(configFile)) {
                write();
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("stepUp")) {
                stepUp = json.getAsJsonPrimitive("stepUp").getAsFloat();
            }
            if (json.has("boatStepUp")) {
                boatStepUp = json.getAsJsonPrimitive("boatStepUp").getAsFloat();
            }
            if (json.has("coyoteTime")) {
                coyoteTime = json.getAsJsonPrimitive("coyoteTime").getAsInt();
            }
            if (json.has("reachAround")) {
                reachAround = json.getAsJsonPrimitive("reachAround").getAsBoolean();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
