package net.frostbyte.mobility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public final class StepChanger implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        if (player.isSneaking()) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_STEP_HEIGHT)).setBaseValue(0.6);
        } else {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_STEP_HEIGHT)).setBaseValue(MaximumMobilityConfig.stepUp);
        }
    }
}
