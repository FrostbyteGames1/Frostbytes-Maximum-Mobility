package net.frostbyte.mobility;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaximumMobility implements ModInitializer {
    public static final String MOD_ID = "mobility";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        MaximumMobilityConfig config = new MaximumMobilityConfig();
        config.read();

        StepChanger stepChanger = new StepChanger();
        ClientTickEvents.END_CLIENT_TICK.register(stepChanger);

        JumpChanger jumpChanger = new JumpChanger();
        ClientTickEvents.END_CLIENT_TICK.register(jumpChanger);

        BoatChanger boatChanger = new BoatChanger();
        ClientTickEvents.END_CLIENT_TICK.register(boatChanger);

        BlockPlacementChanger blockPlacementChanger = new BlockPlacementChanger();
        ClientTickEvents.END_CLIENT_TICK.register(blockPlacementChanger);
        HudRenderCallback.EVENT.register(blockPlacementChanger);
    }

}
