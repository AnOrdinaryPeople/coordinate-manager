package com.anordinarypeople.coordinatemanager.widgets.empty;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.EmptyWorld;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class EmptyWorldPanelEntry extends AlwaysSelectedEntryListWidget.Entry<EmptyWorldPanelEntry> {
  public final EmptyWorld data;
  private final int position;
  private final MinecraftClient client;

  public EmptyWorldPanelEntry(MinecraftClient client, EmptyWorld data, int position) {
    this.client = client;
    this.data = data;
    this.position = position;
  }

  private MutableText getTitle() {
    return Text.translatable("world_name.entry", position);
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
