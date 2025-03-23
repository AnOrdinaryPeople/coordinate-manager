package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

public class TextTrim {
  public static MutableText trim(TextRenderer font, Text text, int maxWidth) {
    StringVisitable trimmedName = text;

    if (font.getWidth(text) > maxWidth) {
      StringVisitable ellipsis = StringVisitable.plain("...");

      trimmedName = StringVisitable.concat(
          font.trimToWidth(
              text,
              maxWidth - font.getWidth(ellipsis)),
          ellipsis);
    }

    return Text.literal(trimmedName.getString());
  }
}
