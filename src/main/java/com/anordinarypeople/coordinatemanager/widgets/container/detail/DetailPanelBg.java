package com.anordinarypeople.coordinatemanager.widgets.container.detail;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.utils.DimensionColor;
import com.anordinarypeople.coordinatemanager.utils.DynamicImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class DetailPanelBg {
  private final Minecraft client;
  protected final int left;
  protected final int imageSize = 32;
  protected final int padding = 5;
  protected final int x;
  protected final int y;
  protected final int width;
  protected final int height;

  public DetailPanelBg(Minecraft client, int x, int y, int width, int height) {
    this.client = client;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.left = x + width;
  }

  public void renderImage(GuiGraphicsExtractor context, CoordinateData data) {
    DynamicImage.render(
        context,
        client,
        data.imagePath,
        left - width + padding,
        y + padding,
        imageSize);
    context.text(
        client.font,
        Component.translatable(data.dimension),
        x + imageSize + padding * 2,
        y + padding,
        DimensionColor.get(data.dimension),
        true);

    if (data.isPinned) {
      context.text(
          client.font,
          Component.translatable("detail.favorited"),
          x + imageSize + padding * 2,
          y + client.font.lineHeight + padding + 2,
          Const.DARK_GREEN,
          true);
    }
  }
}
