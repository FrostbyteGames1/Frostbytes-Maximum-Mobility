package net.frostbyte.mobility;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.mobility.config.MaximumMobilityConfig;

public class MaximumMobility implements ModInitializer {
    public static final String MOD_ID = "mobility";
    @SuppressWarnings("deprecation")
    @Override
    public void onInitialize() {
        MaximumMobilityConfig.read();

        StepChanger stepChanger = new StepChanger();
        ClientTickEvents.END_CLIENT_TICK.register(stepChanger);

        JumpChanger jumpChanger = new JumpChanger();
        ClientTickEvents.END_CLIENT_TICK.register(jumpChanger);

        BlockPlacementChanger blockPlacementChanger = new BlockPlacementChanger();
        ClientTickEvents.END_CLIENT_TICK.register(blockPlacementChanger);
        HudRenderCallback.EVENT.register(blockPlacementChanger);

        ElytraCancel elytraCancel = new ElytraCancel();
        ClientTickEvents.END_CLIENT_TICK.register(elytraCancel);
    }

}
