package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class Messager {
  public static final Style BASE_STYLE = Style.EMPTY.withColor(ChatFormatting.GREEN);

  private static MutableComponent base() {
    return Component.translatable("log").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
  }

  public static MutableComponent info(String translationKey, Object... args) {
    return base().append(Component.translatable(translationKey, args).setStyle(BASE_STYLE));
  }
}
