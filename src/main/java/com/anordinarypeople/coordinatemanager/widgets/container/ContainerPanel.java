package com.anordinarypeople.coordinatemanager.widgets.container;

import java.util.Objects;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.ListSelectableCoor;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.utils.BaseContainerScreen;
import com.anordinarypeople.coordinatemanager.utils.RenderHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ContainerPanel extends AlwaysSelectedEntryListWidget<ContainerPanelEntry> implements AutoCloseable {
  private final BaseContainerScreen parent;
  private final boolean selectable;
  private MutableText description;
  private DrawContext context;
  public ListSelectableCoor list;

  public ContainerPanel(
      MinecraftClient client,
      int width,
      int height,
      int x,
      int y,
      int itemHeight,
      ListSelectableCoor list,
      BaseContainerScreen parent,
      boolean selectable) {
    super(client, width, height, y, itemHeight);

    this.setX(x);
    this.selectable = selectable;
    this.parent = parent;

    if (list != null) {
      this.list = list;
    }
  }

  @Nullable
  private ContainerPanelEntry getEntry(int index) {
    return children().size() > index ? children().get(index) : null;
  }

  private void drawItem(int entryCount, int index, int entryTop, int mouseX, int mouseY, float delta) {
    final int entryHeight = itemHeight - 5;
    final int rowWidth = getRowWidth();
    ContainerPanelEntry entry = getEntry(index);

    if (parent.currentData != null && entry.data.uuid.equals(parent.currentData.uuid)) {
      drawSelected(entryTop, entryHeight, getRowLeft() - 2, rowWidth);
    }

    entry.render(
        context,
        mouseX,
        mouseY,
        isMouseOver(mouseX, mouseY)
            && Objects.equals(getEntryAtPos(entryCount, mouseX, mouseY), entry),
        delta);
  }

  private void drawSelected(int entryTop, int entryHeight, int entryLeft, int rowWidth) {
    RenderHelper.drawSelected(
        context,
        getRowLeft(),
        rowWidth,
        entryLeft,
        entryTop,
        entryHeight);
  }

  private ContainerPanelEntry getEntryAtPos(int entryCount, double x, double y) {
    final int entryY = MathHelper.floor(y - ((double) getY()) + (int) getScrollY());
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

  public void filter(String keyword) {
    if (list == null || list.size() == 0) {
      description = Text.translatable(selectable
          ? "container.description"
          : "container.description_empty_world");
      return;
    }
    String lowered = keyword.toLowerCase();
    Pattern pattern = Pattern.compile(lowered);
    boolean clearDescription = false;

    clearEntries();

    for (SelectableCoor data : list) {
      if (lowered.isBlank() || ((data.name != null &&
          pattern.matcher(data.name.toLowerCase()).find()) ||
          pattern.matcher(Double.toString(data.x)).find() ||
          pattern.matcher(Double.toString(data.y)).find() ||
          pattern.matcher(Double.toString(data.z)).find())) {
        clearDescription = true;
        addEntry(new ContainerPanelEntry(client, data));
      }
    }

    description = clearDescription ? null : Text.translatable("container.description_not_found");
    scrollToTop();
  }

  @Override
  public void close() {
  }

  @Override
  public void setSelected(ContainerPanelEntry entry) {
    if (!selectable) {
      return;
    }

    super.setSelected(entry);

    if (parent.currentData == null || !parent.currentData.uuid.equals(entry.data.uuid)) {
      parent.onSelected(entry.data);
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
          Const.WHITE,
          true);
      return;
    }

    final int entryCount = getEntryCount();
    context = drawContext;

    for (int i = 0; i < entryCount; i++) {
      int entryTop = getRowTop(i) + 2;
      int entryBottom = getRowTop(i) + itemHeight + 2;

      if (entryBottom >= getY() && entryTop <= getBottom()) {
        drawItem(entryCount, i, entryTop, mouseX, mouseY, delta);
      }
    }
  }
}
