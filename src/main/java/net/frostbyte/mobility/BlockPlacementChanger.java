package net.frostbyte.mobility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.frostbyte.mobility.config.MaximumMobilityConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BlockPlacementChanger implements ClientTickEvents.EndTick, HudElement {
    Minecraft client;
    boolean canPlace;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEndTick(Minecraft client) {
        this.client = client;
        if (client.player == null) {
            return;
        }

        if (MaximumMobilityConfig.reachAround) {
            canPlace = client.player.getInventory().getSelectedItem().getItem() instanceof BlockItem
                && !client.player.getInventory().getSelectedItem().is(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                && client.player.mainSupportingBlockPos.isPresent()
                && Objects.requireNonNull(client.hitResult).getType() != HitResult.Type.BLOCK
                && getTargetPos(client.player) != null
                && Objects.requireNonNull(client.level).getBlockState(getTargetPos(client.player)).is(BlockTags.REPLACEABLE);
            if (canPlace && client.options.keyUse.isDown()) {
                if (client.gameMode != null && client.gameMode.useItemOn(client.player, client.player.getUsedItemHand(), new BlockHitResult(client.player.position(), client.player.getDirection().getOpposite(), getTargetPos(client.player), false)) instanceof InteractionResult.Success) {
                    client.player.swing(client.player.getUsedItemHand(), SwingAnimation.DEFAULT, true);
                }
            }
        }
    }

    private static BlockPos getTargetPos(LocalPlayer player) {
        BlockPos targetPos = player.mainSupportingBlockPos.orElse(null);
        if (targetPos == null) {
            return null;
        }
        return switch (player.getDirection()) {
            case NORTH -> targetPos.north();
            case EAST -> targetPos.east();
            case SOUTH -> targetPos.south();
            case WEST -> targetPos.west();
            default -> targetPos;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (client != null && client.player != null && MaximumMobilityConfig.reachAround && canPlace) {
            graphics.blit(
                RenderPipelines.CROSSHAIR,
                Identifier.fromNamespaceAndPath(MaximumMobility.MOD_ID, "textures/gui/sprites/hud/reacharound_indicator.png"),
                (client.getWindow().getGuiScaledWidth() - 15) / 2,
                (client.getWindow().getGuiScaledHeight() - 15) / 2,
                0, 0, 15, 15, 15, 15
            );
        }
    }
}
