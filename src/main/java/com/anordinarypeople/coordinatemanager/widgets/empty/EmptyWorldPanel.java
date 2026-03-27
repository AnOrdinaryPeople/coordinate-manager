package com.anordinarypeople.coordinatemanager.widgets.empty;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.data.EmptyWorld;
import com.anordinarypeople.coordinatemanager.screens.WorldNameScreen;
import com.anordinarypeople.coordinatemanager.utils.RenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.util.Mth;

public class EmptyWorldPanel extends ObjectSelectionList<EmptyWorldPanelEntry> implements AutoCloseable {
  private final WorldNameScreen parent;
  private final List<EmptyWorld> list;
  private Consumer<EmptyWorld> onSelected;
  private GuiGraphicsExtractor context;

  public EmptyWorldPanel(
      Minecraft client,
      int width,
      int height,
      int x,
      int y,
      int itemHeight,
      List<EmptyWorld> list,
      Consumer<EmptyWorld> onSelected,
      WorldNameScreen parent) {
    super(client, width, height, y, itemHeight);

    this.setX(x);
    this.onSelected = onSelected;
    this.parent = parent;
    this.list = list;
  }

  @Nullable
  private EmptyWorldPanelEntry getEntry(int index) {
    return children().size() > index ? children().get(index) : null;
  }

  private void drawItem(int entryCount, int index, int entryTop, int mouseX, int mouseY, float delta) {
    final int entryHeight = defaultEntryHeight - 5;
    final int rowWidth = getRowWidth();
    EmptyWorldPanelEntry entry = getEntry(index);

    if (parent.selected != null && parent.selected.index == entry.data.index) {
      RenderHelper.drawSelected(context, getRowLeft(), rowWidth, getRowLeft() - 2, entryTop, entryHeight);
    }

    entry.extractContent(
        context,
        mouseX,
        mouseY,
        isMouseOver(mouseX, mouseY)
            && Objects.equals(getEntryAtPos(entryCount, mouseX, mouseY), entry),
        delta);
  }

  private EmptyWorldPanelEntry getEntryAtPos(int entryCount, double x, double y) {
    final int entryY = Mth.floor(y - ((double) getY()) + (int) scrollAmount());
    final int index = entryY / defaultEntryHeight;
    final int rowLeft = getRowLeft();

    return x < (double) scrollBarX()
        && x >= (double) rowLeft
        && x <= ((double) rowLeft + getRowRight())
        && index >= 0
        && entryY >= 0
        && index < entryCount
            ? children().get(index)
            : null;
  }

  private void scrollToTop() {
    final int max = Math.max(0, contentHeight() - getBottom() - getY() - 4);

    if (scrollAmount() > max) {
      setScrollAmount(max);
    }
  }

  public void showEntries() {
    clearEntries();

    int position = 0;
    for (EmptyWorld empty : list) {
      position++;

      if (empty.name != null) {
        continue;
      }

      addEntry(new EmptyWorldPanelEntry(minecraft, empty, position));
    }

    scrollToTop();
  }

  @Override
  public void close() {
  }

  @Override
  public void setSelected(EmptyWorldPanelEntry entry) {
    super.setSelected(entry);

    if (parent.selected == null || parent.selected.index != entry.data.index) {
      onSelected.accept(entry.data);
    }
  }

  @Override
  public int getRowWidth() {
    return width
        - (Math.max(0, contentHeight() - (getBottom() - getY() - 4)) > 0 ? 18 : 12);
  }

  @Override
  public int getRowLeft() {
    return getX() + 6;
  }

  @Override
  protected void extractListItems(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {
    final int entryCount = getItemCount();
    context = drawContext;

    for (int i = 0; i < entryCount; i++) {
      int entryTop = getRowTop(i) + 2;
      int entryBottom = getRowTop(i) + defaultEntryHeight + 2;

      if (entryBottom >= getY() && entryTop <= getBottom()) {
        drawItem(entryCount, i, entryTop, mouseX, mouseY, delta);
      }
    }
  }
}
