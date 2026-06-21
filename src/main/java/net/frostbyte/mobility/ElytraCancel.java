package net.frostbyte.mobility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.Minecraft;

public class ElytraCancel implements ClientTickEvents.EndTick {
    @SuppressWarnings("NullableProblems")
    @Override
    public void onEndTick(Minecraft client) {
        if (MaximumMobilityConfig.elytraCancel && client.player != null && client.player.isFallFlying() && client.player.getFallFlyingTicks() > 5 && client.options.keyJump.isDown()) {
            client.player.stopFallFlying();
        }
    }
}
