package com.anordinarypeople.coordinatemanager.utils;

import java.util.Locale;

import com.anordinarypeople.coordinatemanager.cache.ModConfig;
import com.anordinarypeople.coordinatemanager.data.Const;

import net.minecraft.client.MinecraftClient;

public class CopyXYZ {
  public static String copy(MinecraftClient client, double x, double y, double z) {
    String coordinate = ModConfig.INSTANCE.systemAwareCopy
        ? String.format(Locale.getDefault(), Const.COORDINATE_RAW, x, y, z)
        : String.format(Const.COORDINATE_RAW, x, y, z);

    client.keyboard.setClipboard(coordinate);

    return coordinate;
  }
}
