package net.frostbyte.mobility;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MaximumMobility implements ModInitializer {

    public static final String MOD_ID = "mobility";

    @Override
    public void onInitialize() {
        StepChanger stepChanger = new StepChanger();
        stepChanger.setKeyBindings();
        ClientTickEvents.END_CLIENT_TICK.register(stepChanger);

        JumpChanger jumpChanger = new JumpChanger();
        jumpChanger.setKeyBindings();
        ClientTickEvents.END_CLIENT_TICK.register(jumpChanger);

        BoatChanger boatChanger = new BoatChanger();
        boatChanger.setKeyBindings();
        ClientTickEvents.END_CLIENT_TICK.register(boatChanger);
    }
}
