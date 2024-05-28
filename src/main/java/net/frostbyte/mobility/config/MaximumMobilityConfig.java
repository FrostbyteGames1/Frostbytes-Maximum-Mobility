package net.frostbyte.mobility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaximumMobilityConfig {
    public static final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("frostbyte");
    public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/maximum-mobility.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static double stepUp = 0.6;
    public static double boatStepUp = 0.0;
    public static int coyoteTime = 0;
    public static boolean reachAround = false;

    public static Screen createScreen(Screen parent) {
        read();
        return YetAnotherConfigLib.createBuilder()
            .title(Text.of("Frostbyte's Maximum Mobility Config Menu"))

            .category(ConfigCategory.createBuilder()
                .name(Text.of("Frostbyte's Maximum Mobility"))
                .tooltip(Text.of("Configuration menu for Frostbyte's Maximum Mobility"))
                .option(Option.<Double>createBuilder()
                    .name(Text.of("Player Step Height"))
                    .description(OptionDescription.of(Text.of("The number of blocks that the player can walk up without jumping")))
                    .binding(0.6, () -> stepUp, newVal -> stepUp = newVal)
                    .controller(option -> doubleSliderController(option, 0, 10, 0.1))
                    .build())
                .option(Option.<Double>createBuilder()
                    .name(Text.of("Boat Step Height"))
                    .description(OptionDescription.of(Text.of("The number of blocks that a boat can move up")))
                    .binding(0.0, () -> boatStepUp, newVal -> boatStepUp = newVal)
                    .controller(option -> doubleSliderController(option, 0, 10, 0.1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.of("Coyote Time"))
                    .description(OptionDescription.of(Text.of("The number of ticks after falling off of a block that the player can still jump")))
                    .binding(0, () -> coyoteTime, newVal -> coyoteTime = newVal)
                    .controller(option -> integerSliderController(option, 0, 100, 1))
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Reach Around Block Placement"))
                    .description(OptionDescription.of(Text.of("The ability to place a block in front of the player, like is possible in Bedrock Edition")))
                    .binding(false, () -> reachAround, newVal -> reachAround = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())

            .save(MaximumMobilityConfig::write)
            .build()
            .generateScreen(parent);
    }

    private static DoubleSliderControllerBuilder doubleSliderController(Option<Double> option, double min, double max, double step) {
        return DoubleSliderControllerBuilder.create(option).range(min, max).step(step);
    }

    private static IntegerSliderControllerBuilder integerSliderController(Option<Integer> option, int min, int max, int step) {
        return IntegerSliderControllerBuilder.create(option).range(min, max).step(step);
    }

    public static void write() {
        try {
            if (Files.notExists(configDir)) {
                Files.createDirectory(configDir);
            }
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("stepUp", stepUp);
            json.addProperty("boatStepUp", boatStepUp);
            json.addProperty("coyoteTime", coyoteTime);
            json.addProperty("reachAround", reachAround);
            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException ignored) {
        }
    }

    public static void read() {
        try {
            if (Files.notExists(configFile)) {
                write();
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("stepUp")) {
                stepUp = json.getAsJsonPrimitive("stepUp").getAsFloat();
            }
            if (json.has("boatStepUp")) {
                boatStepUp = json.getAsJsonPrimitive("boatStepUp").getAsFloat();
            }
            if (json.has("coyoteTime")) {
                coyoteTime = json.getAsJsonPrimitive("coyoteTime").getAsInt();
            }
            if (json.has("reachAround")) {
                reachAround = json.getAsJsonPrimitive("reachAround").getAsBoolean();
            }
        } catch (IOException ignored) {
        }
    }

}
