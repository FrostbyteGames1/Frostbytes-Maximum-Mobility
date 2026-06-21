package net.frostbyte.mobility;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.resources.Identifier;

public class MaximumMobility implements ModInitializer {
    public static final String MOD_ID = "mobility";
    @Override
    public void onInitialize() {
        MaximumMobilityConfig.read();

        StepChanger stepChanger = new StepChanger();
        ClientTickEvents.END_CLIENT_TICK.register(stepChanger);

        JumpChanger jumpChanger = new JumpChanger();
        ClientTickEvents.END_CLIENT_TICK.register(jumpChanger);

        BlockPlacementChanger blockPlacementChanger = new BlockPlacementChanger();
        ClientTickEvents.END_CLIENT_TICK.register(blockPlacementChanger);
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "hud/reacharound_indicator"), blockPlacementChanger);

        ElytraCancel elytraCancel = new ElytraCancel();
        ClientTickEvents.END_CLIENT_TICK.register(elytraCancel);
    }

}
