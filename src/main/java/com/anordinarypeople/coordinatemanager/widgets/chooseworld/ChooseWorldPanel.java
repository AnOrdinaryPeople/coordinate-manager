package com.anordinarypeople.coordinatemanager.widgets.chooseworld;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.screens.ChooseWorldScreen;
import com.anordinarypeople.coordinatemanager.utils.BufferHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class ChooseWorldPanel extends AlwaysSelectedEntryListWidget<ChooseWorldPanelEntry> implements AutoCloseable {
  private final ChooseWorldScreen parent;
  private final List<String> list;
  private final float z = 0.0F;
  private final int bgColor = ColorHelper.fromFloats(1.0F, z, z, z);
  private Consumer<String> onSelected;
  private DrawContext context;
  private BufferHelper bufferHelper;
  private MutableText description = null;

  public ChooseWorldPanel(
      MinecraftClient client,
      int width,
      int height,
      int x,
      int y,
      int itemHeight,
      List<String> list,
      Consumer<String> onSelected,
      ChooseWorldScreen parent) {
    super(client, width, height, y, itemHeight);

    this.setX(x);
    this.onSelected = onSelected;
    this.parent = parent;
    this.list = list;
  }

  private void drawItem(int entryCount, int index, int entryTop, int mouseX, int mouseY, float delta) {
    final int entryHeight = itemHeight - 5;
    final int rowWidth = getRowWidth();
    ChooseWorldPanelEntry entry = getEntry(index);
    int entryLeft;

    if (parent.selected != null && parent.selected.equals(entry.data)) {
      entryLeft = getRowLeft() - 2;
      drawSelected(entryTop, entryHeight, entryLeft, rowWidth);
    }

    entryLeft = getRowLeft();
    entry.render(
        context,
        index,
        entryTop,
        entryLeft,
        rowWidth,
        entryHeight,
        mouseX,
        mouseY,
        isMouseOver(mouseX, mouseY)
            && Objects.equals(getEntryAtPos(entryCount, mouseX, mouseY), entry),
        delta);
  }

  private void drawSelected(int entryTop, int entryHeight, int entryLeft, int rowWidth) {
    bufferHelper.drawSelected(
        context,
        isFocused(),
        getRowLeft(),
        rowWidth,
        entryLeft,
        entryTop,
        entryHeight,
        z,
        bgColor);
  }

  private ChooseWorldPanelEntry getEntryAtPos(int entryCount, double x, double y) {
    final int entryY = MathHelper.floor(y - ((double) getY()) - headerHeight + (int) getScrollY() - 4);
    final int index = entryY / itemHeight;
    final int rowLeft = getRowLeft();

    return x < (double) getScrollbarX()
        && x >= (double) rowLeft
        && x <= ((double) rowLeft + getRowRight())
        && index >= 0
        && entryY >= 0
        && index < entryCount
            ? children().get(index)
            : null;
  }

  private void scrollToTop() {
    final int max = Math.max(0, getContentsHeightWithPadding() - getBottom() - getY() - 4);

    if (getScrollY() > max) {
      setScrollY(max);
    }
  }

  public void showEntries() {
    clearEntries();

    if (list.size() == 0) {
      description = Text.translatable("world_name.choose_empty");
      return;
    }

    for (String world : list) {
      addEntry(new ChooseWorldPanelEntry(client, world));
    }

    scrollToTop();
  }

  @Override
  public void close() {
  }

  @Override
  public void setSelected(ChooseWorldPanelEntry entry) {
    super.setSelected(entry);

    if (parent.selected == null || parent.selected.equals(entry.data)) {
      onSelected.accept(entry.data);
    }
  }

  @Override
  public int getRowWidth() {
    return width
        - (Math.max(0, getContentsHeightWithPadding() - (getBottom() - getY() - 4)) > 0 ? 18 : 12);
  }

  @Override
  public int getRowLeft() {
    return getX() + 6;
  }

  @Override
  protected void renderList(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    if (description != null) {
      drawContext.drawText(
          client.textRenderer,
          description,
          parent.padding * 2,
          parent.inputHeight + parent.padding * 3 + 2,
          Const.GRAY,
          true);
      return;
    }
    final int entryCount = getEntryCount();
    context = drawContext;
    bufferHelper = new BufferHelper(client, "Choose World Panel");

    for (int i = 0; i < entryCount; i++) {
      int entryTop = getRowTop(i) + 2;
      int entryBottom = getRowTop(i) + itemHeight + 2;

      if (entryBottom >= getY() && entryTop <= getBottom()) {
        drawItem(entryCount, i, entryTop, mouseX, mouseY, delta);
      }
    }
  }
}
