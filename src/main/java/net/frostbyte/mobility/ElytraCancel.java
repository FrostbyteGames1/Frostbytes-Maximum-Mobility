package net.frostbyte.mobility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.MinecraftClient;

public class ElytraCancel implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftClient client) {
        if (MaximumMobilityConfig.elytraCancel && client.player != null && client.player.isFallFlying() && client.player.getFallFlyingTicks() > 5 && client.options.jumpKey.isPressed()) {
            client.player.stopFallFlying();
        }
    }
}
