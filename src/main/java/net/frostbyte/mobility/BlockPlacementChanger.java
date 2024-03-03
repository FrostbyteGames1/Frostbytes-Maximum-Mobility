package net.frostbyte.mobility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class BlockPlacementChanger implements ClientTickEvents.EndTick {
    public final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/maximum-mobility.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean reachAround = true;
    public static boolean canPlace = false;
    @Override
    public void onEndTick(MinecraftClient client) {

        if (client.player == null ) {
            return;
        }

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("reachAround")) {
                reachAround = json.getAsJsonPrimitive("reachAround").getAsBoolean();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert client.interactionManager != null;
        if (reachAround && client.player.getInventory().getMainHandStack().getItem() instanceof BlockItem) {
            BlockPos targetPos = client.player.getBlockPos().add(0, -1, 0);
            if (client.player.getHorizontalFacing() == Direction.NORTH) {
                targetPos.add(0, 0, 1);
            } else if (client.player.getHorizontalFacing() == Direction.EAST) {
                targetPos.add(1, 0, 0);
            } else if (client.player.getHorizontalFacing() == Direction.SOUTH) {
                targetPos.add(0, 0, -1);
            } else if (client.player.getHorizontalFacing() == Direction.WEST) {
                targetPos.add(-1, 0, 0);
            }
            if (targetPos != client.player.getBlockPos().add(0, -1, 0) && client.player.isOnGround() && Objects.requireNonNull(client.world).getBlockState(targetPos).isIn(BlockTags.REPLACEABLE) && Objects.requireNonNull(client.crosshairTarget).getType() != HitResult.Type.BLOCK) {
                canPlace = true;
                if (client.options.useKey.isPressed()) {
                    if (client.interactionManager.interactBlock(client.player, client.player.getActiveHand(), new BlockHitResult(client.player.getPos(), client.player.getHorizontalFacing().getOpposite(), targetPos, false)).isAccepted()) {
                        client.player.swingHand(client.player.getActiveHand());
                    }
                }
            } else {
                canPlace = false;
            }
        }
    }
}
