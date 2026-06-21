package net.frostbyte.mobility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public class JumpChanger implements ClientTickEvents.EndTick {
    int fallingTicks = 0;
    private final double[] yValues = {0, 0};

    @Override
    public void onEndTick(Minecraft client) {
        LocalPlayer player;
        player = client.player;

        if (player == null) {
            return;
        }

        if (MaximumMobilityConfig.coyoteTime != 0) {
            if (player.onGround()){
                yValues[0] = player.getY();
                yValues[1] = player.getY();
            }

            if (!player.onGround()) {
                yValues[1] = player.getY();
                fallingTicks ++;
                if (fallingTicks < MaximumMobilityConfig.coyoteTime && player.isJumping() && yValues[1] < yValues[0]) {
                    player.jumpFromGround();
                    fallingTicks = MaximumMobilityConfig.coyoteTime;
                }
            } else {
                fallingTicks = 0;
            }
        }

    }
}
