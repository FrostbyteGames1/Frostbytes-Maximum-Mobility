package net.frostbyte.mobility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

@Environment(EnvType.CLIENT)
public final class StepChanger implements ClientTickEvents.EndTick {
    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEndTick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        if (player.isCrouching()) {
            player.getAttributes().getInstance(Attributes.STEP_HEIGHT).setBaseValue(0.6);
        } else {
            player.getAttributes().getInstance(Attributes.STEP_HEIGHT).setBaseValue(MaximumMobilityConfig.stepUp);
        }
    }
}
