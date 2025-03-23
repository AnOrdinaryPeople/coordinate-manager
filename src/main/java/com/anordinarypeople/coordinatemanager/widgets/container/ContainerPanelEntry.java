package com.anordinarypeople.coordinatemanager.widgets.container;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.utils.DynamicImage;
import com.anordinarypeople.coordinatemanager.utils.TextTrim;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ContainerPanelEntry extends AlwaysSelectedEntryListWidget.Entry<ContainerPanelEntry> {
  public final SelectableCoor data;
  private final MinecraftClient client;
  private final int imageSize = 25;
  private DrawContext context;
  private int maxWidth;

  public ContainerPanelEntry(MinecraftClient client, SelectableCoor data) {
    this.client = client;
    this.data = data;
  }

  private MutableText trim(Text text) {
    return TextTrim.trim(client.textRenderer, text, maxWidth);
  }

  private MutableText getTitle() {
    Style style = null;

    if (data.isSelected) {
      style = Style.EMPTY.withBold(true).withUnderline(true);
    }

    if (data.name != null && !data.name.isBlank()) {
      MutableText text = trim(Text.literal(data.name));

      if (style != null) {
        text.setStyle(style);
      }

      return text;
    }

    style = (style != null ? style : Style.EMPTY).withItalic(true);

    return trim(Text.translatable("container.unnamed")).setStyle(style);
  }

  private void renderTitle(int x, int y) {
    context.drawText(
        client.textRenderer,
        getTitle(),
        x + imageSize + 3,
        y + 1,
        data.isPinned ? Const.GREEN : Const.WHITE,
        true);
  }

  private void renderCoordinate(int x, int y) {
    String coordinate = String.format(Const.COORDINATE, data.x, data.y, data.z);

    context.drawText(
        client.textRenderer,
        trim(Text.literal(coordinate)),
        x + imageSize + 3,
        y + client.textRenderer.fontHeight + 3,
        data.isPinned ? Const.DARK_GREEN : Const.GRAY,
        true);
  }

  @Override
  public Text getNarration() {
    return getTitle();
  }

  @Override
  public void render(
      DrawContext drawContext,
      int index,
      int y,
      int x,
      int rowWidth,
      int rowHeight,
      int mouseX,
      int mouseY,
      boolean hovered,
      float delta) {
    maxWidth = rowWidth - imageSize - 3;
    context = drawContext;

    DynamicImage.render(drawContext, client, data.imagePath, x, y, imageSize);
    renderTitle(x, y);
    renderCoordinate(x, y);
  }
}
