package com.anordinarypeople.coordinatemanager.widgets.container;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.utils.DynamicImage;
import com.anordinarypeople.coordinatemanager.utils.TextTrim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class ContainerPanelEntry extends ObjectSelectionList.Entry<ContainerPanelEntry> {
  public final SelectableCoor data;
  private final Minecraft client;
  private final int imageSize = 25;
  private GuiGraphicsExtractor context;

  public ContainerPanelEntry(Minecraft client, SelectableCoor data) {
    this.client = client;
    this.data = data;
  }

  private MutableComponent trim(Component text) {
    return TextTrim.trim(client.font, text, getContentWidth());
  }

  private MutableComponent getTitle() {
    Style style = null;

    if (data.isSelected) {
      style = Style.EMPTY.withBold(true).withUnderlined(true);
    }

    if (data.name != null && !data.name.isBlank()) {
      MutableComponent text = trim(Component.literal(data.name));

      if (style != null) {
        text.setStyle(style);
      }

      return text;
    }

    style = (style != null ? style : Style.EMPTY).withItalic(true);

    return trim(Component.translatable("container.unnamed")).setStyle(style);
  }

  private void renderTitle(int x, int y) {
    context.text(
        client.font,
        getTitle(),
        x + imageSize + 3,
        y + 1,
        data.isPinned ? Const.GREEN : Const.WHITE,
        true);
  }

  private void renderCoordinate(int x, int y) {
    String coordinate = String.format(Const.COORDINATE, data.x, data.y, data.z);

    context.text(
        client.font,
        trim(Component.literal(coordinate)),
        x + imageSize + 3,
        y + client.font.lineHeight + 3,
        data.isPinned ? Const.DARK_GREEN : Const.GRAY,
        true);
  }

  @Override
  public Component getNarration() {
    return getTitle();
  }

  @Override
  public void extractContent(
      GuiGraphicsExtractor drawContext,
      int mouseX,
      int mouseY,
      boolean hovered,
      float delta) {
    int x = getX();
    int y = getY();
    context = drawContext;

    DynamicImage.render(drawContext, client, data.imagePath, x, y, imageSize);
    renderTitle(x, y);
    renderCoordinate(x, y);
  }
}
