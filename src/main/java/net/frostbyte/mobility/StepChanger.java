package net.frostbyte.mobility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StepChanger implements ClientTickEvents.EndTick {

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/maximum-mobility.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    float stepUp = 1.25F;

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        player = client.player;

        if (player==null)
            return;

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("stepUp"))
                stepUp = json.getAsJsonPrimitive("stepUp").getAsFloat();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (player.isSneaking()) {
            player.setStepHeight(0.6F);
        } else {
            player.setStepHeight(stepUp);
        }
    }
}
