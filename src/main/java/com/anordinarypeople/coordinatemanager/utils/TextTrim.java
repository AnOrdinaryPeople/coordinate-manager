package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class TextTrim {
  public static MutableComponent trim(Font font, Component text, int maxWidth) {
    if (font.width(text) > maxWidth) {
      String trimmed = font.plainSubstrByWidth(text.getString(), maxWidth - font.width("..."));
      return Component.literal(trimmed + "...");
    }
    return Component.literal(text.getString());
  }
}
