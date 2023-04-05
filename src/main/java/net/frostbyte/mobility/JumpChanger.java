package net.frostbyte.mobility;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class JumpChanger implements ClientTickEvents.EndTick {

    public KeyBinding coyoteTimeToggleKey;
    private MinecraftClient mc;
    public static boolean coyoteTime = true;
    public static boolean firstRun = true;
    private int fallingTicks = 0;

    private double[] yValues = {0, 0};

    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(coyoteTimeToggleKey = new KeyBinding("Toggle Coyote Time", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_K, "Maximum Mobility"));
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

        if (coyoteTime) {

            if (player.isOnGround()){
                yValues[0] = player.getY();
                yValues[1] = player.getY();
            }

            if (!player.isOnGround()) {
                yValues[1] = player.getY();
                if (fallingTicks < 10 && player.input.jumping && yValues[1] < yValues[0]) {
                    player.jump();
                    fallingTicks = 10;
                }
                fallingTicks ++;
            } else {
                fallingTicks = 0;
            }

        }
    }

    public void processKeyBinds() {
        if (coyoteTimeToggleKey.wasPressed()) {
            coyoteTime = !coyoteTime;
            message();
        }
    }

    private void message() {
        String m = "[" + Formatting.GOLD + "Maximum Mobility" + Formatting.WHITE + "] " + "Coyote Time: ";
        if (coyoteTime) {
            m = m + Formatting.GREEN + "Active";
        }else {
            m = m + Formatting.RED + "Inactive";
        }
        mc.player.sendMessage(Text.literal(m), false);
    }
}
