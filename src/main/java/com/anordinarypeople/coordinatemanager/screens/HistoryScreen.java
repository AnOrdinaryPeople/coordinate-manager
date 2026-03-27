package com.anordinarypeople.coordinatemanager.screens;

import java.io.File;
import java.util.ArrayList;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.data.ListCoordinate;
import com.anordinarypeople.coordinatemanager.data.ListSelectableCoor;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.enums.SelectConfirmType;
import com.anordinarypeople.coordinatemanager.screens.confirm.ClearAllConfirm;
import com.anordinarypeople.coordinatemanager.screens.confirm.DeleteConfirm;
import com.anordinarypeople.coordinatemanager.screens.confirm.SelectConfirm;
import com.anordinarypeople.coordinatemanager.utils.BaseContainerScreen;
import com.anordinarypeople.coordinatemanager.utils.CopyXYZ;
import com.anordinarypeople.coordinatemanager.utils.DimensionColor;
import com.anordinarypeople.coordinatemanager.utils.Messager;
import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.container.ContainerPanel;
import com.anordinarypeople.coordinatemanager.widgets.container.detail.DetailPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

@Environment(EnvType.CLIENT)
public class HistoryScreen extends BaseContainerScreen {
  private final ManageScreen parent;
  private final Btn button = new Btn(0, 0, 0, inputHeight);
  private ArrayList<String> deleteQueue = new ArrayList<>(Coordinate.INSTANCE.size());
  private EditBox searchWidget;
  private ContainerPanel containerPanel;
  private DetailPanel detailPanel;
  private Button selectedButton;
  public ListSelectableCoor mapData = null;
  public boolean manualUpdate = false;
  public int totalSelected;

  public HistoryScreen(ManageScreen parent) {
    super("history.title");
    this.parent = parent;
  }

  private void initMapData(ListCoordinate data) {
    totalSelected = 0;

    if (data == null) {
      data = Coordinate.INSTANCE;
    }

    if (mapData == null) {
      mapData = new ListSelectableCoor(data.size());
    }

    ListSelectableCoor temp = new ListSelectableCoor(data.size());

    for (CoordinateData obj : data) {
      SelectableCoor map = new SelectableCoor(obj.name, obj.dimension, obj.x, obj.y, obj.z);
      map.uuid = obj.uuid;
      map.imagePath = obj.imagePath;
      map.isPinned = obj.isPinned;

      for (SelectableCoor coor : mapData) {
        if (coor.uuid.equals(obj.uuid)) {
          map.isSelected = coor.isSelected;

          if (coor.isSelected) {
            totalSelected++;
          }
          break;
        }
      }

      temp.add(map);
    }

    mapData = temp;
  }

  private void renderSearch(int width) {
    searchWidget = new EditBox(font, padding, padding, width, inputHeight, Component.empty());
    searchWidget.setHint(Component.translatable("history.search").setStyle(Style.EMPTY.withItalic(true)));
    searchWidget.setResponder(text -> containerPanel.filter(text));

    addRenderableWidget(searchWidget);
  }

  private void renderContainer(int width) {
    containerPanel = new ContainerPanel(
        minecraft,
        width,
        height - inputHeight - footerHeight - padding * 3 - 2,
        padding,
        padding * 2 + 2 + inputHeight,
        30,
        mapData,
        this,
        true);

    containerPanel.filter("");
    addRenderableWidget(containerPanel);
  }

  private void renderDetail(int containerPanelWidth) {
    detailPanel = new DetailPanel(
        this,
        font,
        minecraft,
        containerPanelWidth + padding * 2,
        padding,
        (int) (width * 0.4 - padding * 2 - 2),
        height - footerHeight - padding * 2 + 2);

    addRenderableWidget(detailPanel.description);
    addRenderableWidget(detailPanel.nameField);
    addRenderableWidget(detailPanel.xField);
    addRenderableWidget(detailPanel.yField);
    addRenderableWidget(detailPanel.zField);
    addRenderableWidget(detailPanel.favoriteButton);
    addRenderableWidget(detailPanel.copyButton);
    addRenderableWidget(detailPanel.selectButton);
    addRenderableWidget(detailPanel.deleteButton);
    addRenderableWidget(detailPanel.saveButton);
  }

  private void renderOpenSelectedBtn() {
    button.x = padding;

    selectedButton = button.widget(
        "history.set_selected",
        "history.set_selected.tooltip",
        b -> minecraft.setScreen(new SelectConfirm(this)));
    updateSelectedBtnText();

    addRenderableWidget(selectedButton);
  }

  private void updateSelectedBtnText() {
    selectedButton.active = totalSelected > 0;
    selectedButton.setMessage(
        Component.translatable(
            selectedButton.active
                ? "history.set_selected.total"
                : "history.set_selected",
            totalSelected > 99 ? "99+" : totalSelected));
  }

  private void renderClearUnfavoriteBtn() {
    button.x = padding * 2 + button.width;

    addRenderableWidget(button.widget(
        "history.clear_unfavorite",
        "history.clear_unfavorite.tooltip",
        b -> minecraft.setScreen(new ClearAllConfirm(this))));
  }

  private void refreshContainerList(ListCoordinate temp, boolean skipInstance) {
    if (!skipInstance) {
      Coordinate.INSTANCE = temp;
    }
    initMapData(temp);
    updateSelectedBtnText();
    containerPanel.list = mapData;
    containerPanel.filter(searchWidget.getValue());
  }

  private void renderBackBtn() {
    button.x = padding * 3 + button.width * 2;

    addRenderableWidget(button.widget("history.back", b -> onClose()));
  }

  private void coordinateUpdater() {
    mapData.set(Coordinate.set(currentData, false), currentData);
    containerPanel.list = mapData;
    containerPanel.filter(searchWidget.getValue());
  }

  @Override
  public void onSelected(SelectableCoor data) {
    currentData = data;
    detailPanel.updateDetail(data);
  }

  public void onClickPin() {
    if (currentData != null) {
      currentData.isPinned = !currentData.isPinned;
      coordinateUpdater();
    }
  }

  public void onClickCopy() {
    if (currentData != null) {
      onClose();
      String coordinate = CopyXYZ.copy(minecraft, currentData.x, currentData.y, currentData.z);
      MutableComponent xyz = Component.literal(coordinate).setStyle(Style.EMPTY.withBold(true).withColor(Const.WHITE));
      MutableComponent dimension = Component.translatable(currentData.dimension)
          .setStyle(Style.EMPTY.withColor(DimensionColor.get(currentData.dimension)));

      if (parent == null) {
        minecraft.player.sendSystemMessage(Messager.info("history.copy.success", xyz, dimension));
      } else {
        minecraft.getToastManager().addToast(
            SystemToast.multiline(
                minecraft,
                SystemToastId.NARRATOR_TOGGLE,
                Component.translatable("keybind.category"),
                Component.translatable("history.copy.success", xyz, dimension)));
      }
    }
  }

  public void onClickSelect() {
    if (currentData == null) {
      return;
    }

    for (SelectableCoor data : mapData) {
      if (data.uuid.equals(currentData.uuid)) {
        data.isSelected = !data.isSelected;
        totalSelected += data.isSelected ? 1 : -1;
        updateSelectedBtnText();
        break;
      }
    }
  }

  public void onClickDelete() {
    if (currentData != null && !currentData.isPinned) {
      minecraft.setScreen(new DeleteConfirm(this));
    }
  }

  public void onClickSave() {
    if (currentData != null) {
      currentData.name = detailPanel.nameField.getValue();
      currentData.x = Double.parseDouble(detailPanel.xField.getValue());
      currentData.y = Double.parseDouble(detailPanel.yField.getValue());
      currentData.z = Double.parseDouble(detailPanel.zField.getValue());
      coordinateUpdater();
    }
  }

  public void deleteConfirmed() {
    Coordinate.set(currentData, true);

    if (currentData.imagePath != null) {
      deleteQueue.add(currentData.imagePath);
    }

    if (currentData.isSelected) {
      totalSelected--;
    }

    currentData = null;
    refreshContainerList(null, true);
  }

  public void onSelectedConfirmed(SelectConfirmType confirmType) {
    currentData = null;
    ListCoordinate temp = new ListCoordinate(mapData.size());

    for (SelectableCoor data : mapData) {
      if (confirmType == SelectConfirmType.DELETE) {
        if (!data.isSelected) {
          data.isSelected = false;
          temp.add(data);
        }

        if (data.isSelected && data.imagePath != null) {
          deleteQueue.add(data.imagePath);
        }
      } else {
        if (data.isSelected) {
          data.isSelected = false;
          data.isPinned = confirmType == SelectConfirmType.FAVORITE;
        }

        temp.add(data);
      }
    }

    refreshContainerList(temp, false);
  }

  public void onClearAllConfirmed() {
    ListCoordinate temp = new ListCoordinate(mapData.size());
    totalSelected = 0;
    currentData = null;

    for (SelectableCoor data : mapData) {
      if (data.isPinned) {
        if (data.isSelected) {
          totalSelected++;
        }

        temp.add(data);
      }

      if (!data.isPinned && data.imagePath != null) {
        deleteQueue.add(data.imagePath);
      }
    }

    refreshContainerList(temp, false);
    updateSelectedBtnText();
  }

  @Override
  public boolean keyPressed(KeyEvent keyInput) {
    return super.keyPressed(keyInput)
        || searchWidget.keyPressed(keyInput)
        || detailPanel.nameField.keyPressed(keyInput)
        || detailPanel.xField.keyPressed(keyInput)
        || detailPanel.yField.keyPressed(keyInput)
        || detailPanel.zField.keyPressed(keyInput);
  }

  @Override
  public boolean charTyped(CharacterEvent chr) {
    return searchWidget.charTyped(chr)
        || detailPanel.nameField.charTyped(chr)
        || detailPanel.xField.charTyped(chr)
        || detailPanel.yField.charTyped(chr)
        || detailPanel.zField.charTyped(chr);
  }

  @Override
  public void onClose() {
    Coordinate.save();
    Thread.ofVirtual().start(() -> {
      for (String path : deleteQueue) {
        File file = new File(path);

        if (file.exists()) {
          file.delete();
        }
      }

      WorldCache.save();
    });

    if (parent != null) {
      parent.refreshList();
      minecraft.setScreen(parent);
    } else {
      super.onClose();
    }
  }

  @Override
  protected void init() {
    int containerPanelWidth = (int) (width * 0.6) - padding;

    initMapData(null);

    renderSearch(containerPanelWidth);
    renderContainer(containerPanelWidth);
    renderDetail(containerPanelWidth);

    int footerY = height - footerHeight;
    button.y = footerY + (footerHeight - inputHeight) / 2;
    button.width = (width - padding * 4) / 3;

    renderOpenSelectedBtn();
    renderClearUnfavoriteBtn();
    renderBackBtn();
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractRenderState(context, mouseX, mouseY, delta);

    if (currentData != null) {
      detailPanel.renderImage(context, currentData);

      if (!detailPanel.favoriteButton.visible) {
        detailPanel.updateDetail(currentData);
      }
    } else if (currentData == null && detailPanel.favoriteButton.visible) {
      detailPanel.updateDetail(null);
    }
  }
}
