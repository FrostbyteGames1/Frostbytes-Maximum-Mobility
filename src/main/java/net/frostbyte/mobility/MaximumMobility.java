package net.frostbyte.mobility;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frostbyte.mobility.config.MaximumMobilityConfig;

public class MaximumMobility implements ModInitializer {

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
    }

}
