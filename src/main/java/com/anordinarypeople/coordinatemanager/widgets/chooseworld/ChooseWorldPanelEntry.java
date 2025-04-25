package com.anordinarypeople.coordinatemanager.widgets.chooseworld;

import com.anordinarypeople.coordinatemanager.data.Const;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ChooseWorldPanelEntry extends AlwaysSelectedEntryListWidget.Entry<ChooseWorldPanelEntry> {
  public final String data;
  private final MinecraftClient client;

  public ChooseWorldPanelEntry(MinecraftClient client, String data) {
    this.client = client;
    this.data = data;
  }

  private MutableText getTitle() {
    return Text.literal(data);
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
    drawContext.drawText(client.textRenderer, getTitle(), x + 3, y + 1, Const.WHITE, true);
  }
}
