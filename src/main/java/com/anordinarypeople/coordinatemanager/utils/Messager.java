package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Messager {
  public static final Style BASE_STYLE = Style.EMPTY.withColor(Formatting.GREEN);

  private static MutableText base() {
    return Text.translatable("log").setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
  }

  public static MutableText info(String translationKey, Object... args) {
    return base().append(Text.translatable(translationKey, args).setStyle(BASE_STYLE));
  }
}
