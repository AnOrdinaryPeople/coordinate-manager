package com.anordinarypeople.coordinatemanager.widgets.container;

import java.util.Objects;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.ListSelectableCoor;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.utils.BaseContainerScreen;
import com.anordinarypeople.coordinatemanager.utils.RenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class ContainerPanel extends ObjectSelectionList<ContainerPanelEntry> implements AutoCloseable {
  private final BaseContainerScreen parent;
  private final boolean selectable;
  private MutableComponent description;
  private GuiGraphicsExtractor context;
  public ListSelectableCoor list;

  public ContainerPanel(
      Minecraft client,
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
    final int entryHeight = defaultEntryHeight - 5;
    final int rowWidth = getRowWidth();
    ContainerPanelEntry entry = getEntry(index);

    if (parent.currentData != null && entry.data.uuid.equals(parent.currentData.uuid)) {
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

  private ContainerPanelEntry getEntryAtPos(int entryCount, double x, double y) {
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

  public void filter(String keyword) {
    if (list == null || list.size() == 0) {
      description = Component.translatable(selectable
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
        addEntry(new ContainerPanelEntry(minecraft, data));
      }
    }

    description = clearDescription ? null : Component.translatable("container.description_not_found");
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
        - (Math.max(0, contentHeight() - (getBottom() - getY() - 4)) > 0 ? 18 : 12);
  }

  @Override
  public int getRowLeft() {
    return getX() + 6;
  }

  @Override
  protected void extractListItems(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {
    if (description != null) {
      drawContext.text(
          minecraft.font,
          description,
          parent.padding * 2,
          parent.inputHeight + parent.padding * 3 + 2,
          Const.WHITE,
          true);
      return;
    }

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
