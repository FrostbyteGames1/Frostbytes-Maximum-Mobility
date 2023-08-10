package net.frostbyte.mobility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaximumMobilityConfigScreen extends Screen {
    final Screen parent;
    final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("frostbyte/maximum-mobility.json");
    final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MaximumMobilityConfigScreen(final Screen parent) {
        super(Text.of("Maximum Mobility Options"));
        this.parent = parent;
    }

    public void init() {
        float stepUp = 1.25F;
        float boatStepUp = 1.0F;
        int coyoteTime = 10;

        try {
            if (Files.notExists(configFile)) {
                return;
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);
            if (json.has("stepUp")) {
                stepUp = json.getAsJsonPrimitive("stepUp").getAsFloat();
            } if (json.has("boatStepUp")) {
                boatStepUp = json.getAsJsonPrimitive("boatStepUp").getAsFloat();
            } if (json.has("coyoteTime")) {
                coyoteTime = json.getAsJsonPrimitive("coyoteTime").getAsInt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (MinecraftClient.getInstance() == null) {
            return;
        }

        final TextFieldWidget stepUpField =
                new TextFieldWidget(textRenderer, this.width / 2 - 130, this.height / 4 - 24, 60, 20, Text.of("Step Up Height"));
        stepUpField.setText(String.valueOf(stepUp).replace("F", ""));
        stepUpField.setTooltip(Tooltip.of(Text.of("Set the step up height in blocks\n(To disable, set to 0.6)")));
        stepUpField.setPlaceholder(Text.of("0.6"));
        this.addDrawableChild(stepUpField);

        final TextFieldWidget boatStepUpField =
                new TextFieldWidget(textRenderer, this.width / 2 - 130, this.height / 4, 60, 20, Text.of("Boat Step Up Height"));
        boatStepUpField.setText(String.valueOf(boatStepUp).replace("F", ""));
        boatStepUpField.setTooltip(Tooltip.of(Text.of("Set the boat step up height in blocks\n(To disable, set to 0)")));
        boatStepUpField.setPlaceholder(Text.of("0.0"));
        this.addDrawableChild(boatStepUpField);

        final TextFieldWidget coyoteTimeField =
                new TextFieldWidget(textRenderer, this.width / 2 - 130, this.height / 4 + 24, 60, 20, Text.of("Coyote Time"));
        coyoteTimeField.setText(String.valueOf(coyoteTime));
        coyoteTimeField.setTooltip(Tooltip.of(Text.of("Set coyote time in ticks\n(To disable, set to 0)")));
        coyoteTimeField.setPlaceholder(Text.of("0"));
        this.addDrawableChild(coyoteTimeField);

        final ButtonWidget doneButton =
                ButtonWidget.builder(Text.of("Done"), button -> save(stepUpField.getText(), boatStepUpField.getText(), coyoteTimeField.getText()))
                        .dimensions(this.width / 2 - 130, this.height - 28, 260, 20).build();
        this.addDrawableChild(doneButton);
    }

    void save(String stepUp, String boatStepUp, String coyoteTime) {


        if (!stepUp.matches("[0-9]+\\.[0-9]+") && !stepUp.matches("\\.[0-9]+") && !stepUp.matches("[0-9]+")) {
            stepUp = "1.25";
        }

        if (!boatStepUp.matches("[0-9]+\\.[0-9]+") && !boatStepUp.matches("\\.[0-9]+") && !boatStepUp.matches("[0-9]+")) {
            boatStepUp = "0";
        }

        if (coyoteTime.matches("[0-9]+\\.[0-9]+")) {
            coyoteTime = coyoteTime.split("\\.")[0];
        } else if (!coyoteTime.matches("[0-9]+")) {
            coyoteTime = "0";
        }

        float stepUpFloat = Float.parseFloat(stepUp);
        float boatStepUpFloat = Float.parseFloat(boatStepUp);
        int coyoteTimeInt = Integer.parseInt(coyoteTime);

        try {
            Files.deleteIfExists(configFile);
            JsonObject json = new JsonObject();
            json.addProperty("stepUp", stepUpFloat);
            json.addProperty("boatStepUp", boatStepUpFloat);
            json.addProperty("coyoteTime", coyoteTimeInt);
            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.setScreen(this.parent);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawText(this.textRenderer, "Step Up Height", this.width / 2 - 60, this.height / 4 - 18, 16777215, false);
        context.drawText(this.textRenderer, "Boat Step Up Height", this.width / 2 - 60, this.height / 4 + 6, 16777215, false);
        context.drawText(this.textRenderer, "Coyote Time", this.width / 2 - 60, this.height / 4 + 30, 16777215, false);

        super.render(context, mouseX, mouseY, delta);
    }
}
