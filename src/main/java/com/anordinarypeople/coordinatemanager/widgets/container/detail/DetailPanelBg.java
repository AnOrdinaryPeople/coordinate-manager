package com.anordinarypeople.coordinatemanager.widgets.container.detail;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.utils.DimensionColor;
import com.anordinarypeople.coordinatemanager.utils.DynamicImage;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DetailPanelBg {
  private final Identifier bgTexture = Identifier.ofVanilla("textures/gui/menu_list_background.png");
  private final Identifier bgTextureClient = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");
  private final MinecraftClient client;
  private final boolean isClient;
  private final int bottom;
  protected final int left;
  protected final int imageSize = 32;
  protected final int padding = 5;
  protected final int x;
  protected final int y;
  protected final int width;
  protected final int height;

  public DetailPanelBg(MinecraftClient client, int x, int y, int width, int height) {
    this.client = client;
    this.isClient = client.world != null;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.left = x + width;
    this.bottom = y + height;
  }

  public void render(DrawContext context) {
    context.drawTexture(
        RenderLayer::getGuiTextured,
        isClient ? bgTextureClient : bgTexture,
        x,
        y,
        (float) left,
        (float) x + bottom,
        width,
        height,
        imageSize,
        imageSize);
  }

  public void renderImage(DrawContext context, CoordinateData data) {
    DynamicImage.render(
        context,
        client,
        data.imagePath,
        left - width + padding,
        y + padding,
        imageSize);
    context.drawText(
        client.textRenderer,
        Text.translatable(data.dimension),
        x + imageSize + padding * 2,
        y + padding,
        DimensionColor.get(data.dimension),
        true);

    if (data.isPinned) {
      context.drawText(
          client.textRenderer,
          Text.translatable("detail.favorited"),
          x + imageSize + padding * 2,
          y + client.textRenderer.fontHeight + padding + 2,
          Const.DARK_GREEN,
          true);
    }
  }
}
