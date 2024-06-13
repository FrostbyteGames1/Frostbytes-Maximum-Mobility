package net.frostbyte.mobility;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BlockPlacementChanger implements ClientTickEvents.EndTick, HudRenderCallback {
    private MinecraftClient client;
    private boolean canPlace;
    @Override
    public void onEndTick(MinecraftClient client) {
        this.client = client;
        if (client.player == null ) {
            return;
        }

        assert client.interactionManager != null;
        if (MaximumMobilityConfig.reachAround) {
            canPlace = client.player.getInventory().getMainHandStack().getItem() instanceof BlockItem
                    && !client.player.getInventory().getMainHandStack().isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                    && client.player.supportingBlockPos.isPresent()
                    && Objects.requireNonNull(client.crosshairTarget).getType() != HitResult.Type.BLOCK
                    && getTargetPos(client.player) != null && Objects.requireNonNull(client.world).getBlockState(getTargetPos(client.player)).isIn(BlockTags.REPLACEABLE);
            if (client.options.useKey.isPressed() && canPlace) {
                if (client.interactionManager.interactBlock(client.player, client.player.getActiveHand(), new BlockHitResult(client.player.getPos(), client.player.getHorizontalFacing().getOpposite(), getTargetPos(client.player), false)).isAccepted()) {
                    client.player.swingHand(client.player.getActiveHand());
                }
            }
        }
    }

    private static BlockPos getTargetPos(ClientPlayerEntity player) {
        BlockPos targetPos = player.supportingBlockPos.orElse(null);
        if (targetPos == null) {
            return null;
        }
        if (player.getHorizontalFacing() == Direction.NORTH) {
            targetPos = targetPos.north();
        }
        if (player.getHorizontalFacing() == Direction.EAST) {
            targetPos = targetPos.east();
        }
        if (player.getHorizontalFacing() == Direction.SOUTH) {
            targetPos = targetPos.south();
        }
        if (player.getHorizontalFacing() == Direction.WEST) {
            targetPos = targetPos.west();
        }
        return targetPos;
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (canPlace && !this.client.options.hudHidden && this.client.options.getPerspective().isFirstPerson()) {
            drawContext.getMatrices().push();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            RenderSystem.enableBlend();
            drawContext.drawGuiTexture(Identifier.of(MaximumMobility.MOD_ID, "hud/reacharound_indicator"), (drawContext.getScaledWindowWidth() - 15) / 2, (drawContext.getScaledWindowHeight() - 15) / 2, 15, 15);
            drawContext.getMatrices().pop();
        }
    }
}
