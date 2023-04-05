package net.frostbyte.mobility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BoatChanger implements ClientTickEvents.EndTick{

    public KeyBinding boatStepUpToggleKey;
    private MinecraftClient mc;
    public static boolean firstRun = true;
    public static boolean boatStepUp = true;

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(boatStepUpToggleKey = new KeyBinding("Toggle Boat Step Up", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_M, "Maximum Mobility"));
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;

        if (player==null)
            return;
        processKeyBinds();

        if (firstRun) {
            message();
            firstRun = false;
        }
    }

    public void processKeyBinds() {
        if (boatStepUpToggleKey.wasPressed()) {
            boatStepUp = !boatStepUp;
            message();
        }
    }

    private void message() {
        String m = "[" + Formatting.GOLD + "Maximum Mobility" + Formatting.WHITE + "] " + "Boat Step Up: ";
        if (boatStepUp) {
            m = m + Formatting.GREEN + "Active";
        }else {
            m = m + Formatting.RED + "Inactive";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }
}
