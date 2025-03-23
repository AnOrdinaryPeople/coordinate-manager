package com.anordinarypeople.coordinatemanager.screens;

import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.enums.CaptureMode;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen get(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent);
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startEnumSelector(
                Text.translatable("config.capture_mode"),
                CaptureMode.class,
                ModConfig.INSTANCE.captureMode)
                .setDefaultValue(CaptureMode.INSTANT)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.captureMode = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("config.enable_image"),
                ModConfig.INSTANCE.enableImage)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableImage = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("config.auto_copy"),
                ModConfig.INSTANCE.autoCopy)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.autoCopy = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(
                Text.translatable("config.capture_cooldown"),
                ModConfig.INSTANCE.captureCooldown)
                .setDefaultValue(250)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.captureCooldown = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("config.precise_coordinate"),
                ModConfig.INSTANCE.isPreciseCoordinate)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.isPreciseCoordinate = newValue)
                .build());

        builder.setSavingRunnable(() -> ModConfig.save());

        return builder.build();
    }
}
