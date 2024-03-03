package net.frostbyte.mobility.mixin;

import net.frostbyte.mobility.BlockPlacementChanger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (BlockPlacementChanger.canPlace && !this.client.options.hudHidden) {
            context.getMatrices().push();
            context.getMatrices().translate(scaledWidth / 2F, scaledHeight / 2f - 4, 0);
            context.getMatrices().scale(1, 1f, 1f);
            context.getMatrices().translate(-client.textRenderer.getWidth("<  >") / 2.0f, 0, 0);
            context.drawText(client.textRenderer, "<  >", 0, 0, Colors.BLACK, false);
            context.getMatrices().pop();
        }
    }
}
