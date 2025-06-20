package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;

public class RenderHelper {
  public static void drawSelected(
      DrawContext context,
      int rowLeft,
      int rowWidth,
      int entryLeft,
      int entryTop,
      int entryHeight) {
    context.fill(rowLeft - 2, entryTop - 2, rowLeft + rowWidth, entryTop + entryHeight + 2, Colors.WHITE);
    context.fill(rowLeft - 1, entryTop - 1, rowLeft + rowWidth - 1, entryTop + entryHeight + 1, Colors.BLACK);
  }
}
