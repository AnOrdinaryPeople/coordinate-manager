package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.CommonColors;

public class RenderHelper {
  public static void drawSelected(
      GuiGraphicsExtractor context,
      int rowLeft,
      int rowWidth,
      int entryLeft,
      int entryTop,
      int entryHeight) {
    context.fill(rowLeft - 2, entryTop - 4, rowLeft + rowWidth, entryTop + entryHeight,
        CommonColors.WHITE);
    context.fill(rowLeft - 1, entryTop - 3, rowLeft + rowWidth - 1, entryTop + entryHeight - 1,
        CommonColors.BLACK);
  }
}
