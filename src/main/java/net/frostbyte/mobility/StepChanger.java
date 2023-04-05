package net.frostbyte.mobility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class StepChanger implements ClientTickEvents.EndTick {

    public KeyBinding stepUpCycleKey;
    private MinecraftClient mc;
    public static int stepUpMode = 0; // 0 = Step Up, 1 = None, 2 = Auto Jump
    public static boolean firstRun = true;


    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(stepUpCycleKey = new KeyBinding("Cycle Step Up Mode", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_J, "Maximum Mobility"));
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player;
        mc = client;
        player = client.player;
        if (player==null)
            return;
        processKeyBinds();
        if(player.isSneaking()) {
            player.setStepHeight(.6f);
        } else if(stepUpMode == 0 && player.getStepHeight() < 1.0f){
            player.setStepHeight(1.25f);
        } else if(stepUpMode == 1 && player.getStepHeight() >= 1.0f){
            player.setStepHeight(.6f);
        } else if(stepUpMode == 2 && player.getStepHeight() >= 1.0f){
            player.setStepHeight(.6f);
        }
        autoJump();

        if (firstRun) {
            message();
            firstRun = false;
        }
    }

    public void processKeyBinds() {
        if (stepUpCycleKey.wasPressed()) {
            stepUpMode = ( stepUpMode + 1 ) % 3;
            message();
        }
    }

    private void autoJump() {
        SimpleOption<Boolean> option = mc.options.getAutoJump();
        boolean b = option.getValue();
        if (stepUpMode < 2 && b == true) {
            option.setValue(false);
        } else if (stepUpMode == 2 && b == false) {
            option.setValue(true);
        }
    }

    private void message() {
        String m = "[" + Formatting.GOLD + "Maximum Mobility" + Formatting.WHITE + "] " + "Step Up Mode: ";
        if (stepUpMode == 0) {
            m = m + Formatting.GREEN + "Modded";
        } else if (stepUpMode == 1) {
            m = m + Formatting.RED + "None";
        } else if (stepUpMode == 2) {
            m = m + Formatting.GREEN + "Auto Jump";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }
}
