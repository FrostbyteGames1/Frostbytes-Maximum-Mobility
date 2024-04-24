package net.frostbyte.mobility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class JumpChanger implements ClientTickEvents.EndTick {

    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/maximum-mobility.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    int coyoteTime = 10;
    int fallingTicks = 0;
    private final double[] yValues = {0, 0};

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        player = client.player;

        if (player == null) {
            return;
        }

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("coyoteTime"))
                coyoteTime = json.getAsJsonPrimitive("coyoteTime").getAsInt();
        } catch (IOException e) {
            MaximumMobility.LOGGER.error(e.getMessage());
        }

        if (coyoteTime != 0) {
            if (player.isOnGround()){
                yValues[0] = player.getY();
                yValues[1] = player.getY();
            }

            if (!player.isOnGround()) {
                yValues[1] = player.getY();
                fallingTicks ++;
                if (fallingTicks < coyoteTime && player.input.jumping && yValues[1] < yValues[0]) {
                    player.jump();
                    fallingTicks = coyoteTime;
                }
            } else {
                fallingTicks = 0;
            }
        }

    }
}
