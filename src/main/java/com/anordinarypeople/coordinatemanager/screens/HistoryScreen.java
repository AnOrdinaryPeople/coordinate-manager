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
import com.anordinarypeople.coordinatemanager.widgets.Button;
import com.anordinarypeople.coordinatemanager.widgets.container.ContainerPanel;
import com.anordinarypeople.coordinatemanager.widgets.container.detail.DetailPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class HistoryScreen extends BaseContainerScreen {
  private final ManageScreen parent;
  private final Button button = new Button(0, 0, 0, inputHeight);
  private ArrayList<String> deleteQueue = new ArrayList<>(Coordinate.INSTANCE.size());
  private TextFieldWidget searchWidget;
  private ContainerPanel containerPanel;
  private DetailPanel detailPanel;
  private ButtonWidget selectedButton;
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
    searchWidget = new TextFieldWidget(textRenderer, padding, padding, width, inputHeight, Text.empty());
    searchWidget.setPlaceholder(Text.translatable("history.search").setStyle(Style.EMPTY.withItalic(true)));
    searchWidget.setChangedListener(text -> containerPanel.filter(text));

    addDrawableChild(searchWidget);
  }

  private void renderContainer(int width) {
    containerPanel = new ContainerPanel(
        client,
        width,
        height - inputHeight - footerHeight - padding * 3 - 2,
        padding,
        padding * 2 + 2 + inputHeight,
        30,
        mapData,
        data -> {
          currentData = data;
          detailPanel.updateDetail(data);
        },
        this,
        true);

    containerPanel.filter("");
    addDrawableChild(containerPanel);
  }

  private void renderDetail(int containerPanelWidth) {
    detailPanel = new DetailPanel(
        this,
        textRenderer,
        client,
        containerPanelWidth + padding * 2,
        padding,
        (int) (width * 0.4 - padding * 2 - 2),
        height - footerHeight - padding * 2 + 2);

    addDrawableChild(detailPanel.description);
    addDrawableChild(detailPanel.nameField);
    addDrawableChild(detailPanel.xField);
    addDrawableChild(detailPanel.yField);
    addDrawableChild(detailPanel.zField);
    addDrawableChild(detailPanel.favoriteButton);
    addDrawableChild(detailPanel.copyButton);
    addDrawableChild(detailPanel.selectButton);
    addDrawableChild(detailPanel.deleteButton);
    addDrawableChild(detailPanel.saveButton);
  }

  private void renderOpenSelectedBtn() {
    button.x = padding;

    selectedButton = button.widget(
        "history.set_selected",
        "history.set_selected.tooltip",
        b -> client.setScreen(new SelectConfirm(this)));
    updateSelectedBtnText();

    addDrawableChild(selectedButton);
  }

  private void updateSelectedBtnText() {
    selectedButton.active = totalSelected > 0;
    selectedButton.setMessage(
        Text.translatable(
            selectedButton.active
                ? "history.set_selected.total"
                : "history.set_selected",
            totalSelected > 99 ? "99+" : totalSelected));
  }

  private void renderClearUnfavoriteBtn() {
    button.x = padding * 2 + button.width;

    addDrawableChild(button.widget(
        "history.clear_unfavorite",
        "history.clear_unfavorite.tooltip",
        b -> client.setScreen(new ClearAllConfirm(this))));
  }

  private void refreshContainerList(ListCoordinate temp, boolean skipInstance) {
    if (!skipInstance) {
      Coordinate.INSTANCE = temp;
    }
    initMapData(temp);
    updateSelectedBtnText();
    containerPanel.list = mapData;
    containerPanel.filter(searchWidget.getText());
  }

  private void renderBackBtn() {
    button.x = padding * 3 + button.width * 2;

    addDrawableChild(button.widget("history.back", b -> close()));
  }

  private void coordinateUpdater() {
    mapData.set(Coordinate.set(currentData, false), currentData);
    containerPanel.list = mapData;
    containerPanel.filter(searchWidget.getText());
  }

  public void onClickPin() {
    if (currentData != null) {
      currentData.isPinned = !currentData.isPinned;
      coordinateUpdater();
    }
  }

  public void onClickCopy() {
    if (currentData != null) {
      close();
      String coordinate = CopyXYZ.copy(client, currentData.x, currentData.y, currentData.z);
      MutableText xyz = Text.literal(coordinate).setStyle(Style.EMPTY.withBold(true).withColor(Const.WHITE));
      MutableText dimension = Text.translatable(currentData.dimension)
          .setStyle(Style.EMPTY.withColor(DimensionColor.get(currentData.dimension)));

      if (parent == null) {
        client.player.sendMessage(
            Messager.info("history.copy.success", xyz, dimension),
            false);
      } else {
        client.getToastManager().add(
            SystemToast.create(
                client,
                SystemToast.Type.NARRATOR_TOGGLE,
                Text.translatable("keybind.category"),
                Text.translatable("history.copy.success", xyz, dimension)));
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
      client.setScreen(new DeleteConfirm(this));
    }
  }

  public void onClickSave() {
    if (currentData != null) {
      currentData.name = detailPanel.nameField.getText();
      currentData.x = Double.parseDouble(detailPanel.xField.getText());
      currentData.y = Double.parseDouble(detailPanel.yField.getText());
      currentData.z = Double.parseDouble(detailPanel.zField.getText());
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
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return super.keyPressed(keyCode, scanCode, modifiers)
        || searchWidget.keyPressed(keyCode, scanCode, modifiers)
        || detailPanel.nameField.keyPressed(keyCode, scanCode, modifiers)
        || detailPanel.xField.keyPressed(keyCode, scanCode, modifiers)
        || detailPanel.yField.keyPressed(keyCode, scanCode, modifiers)
        || detailPanel.zField.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int keyCode) {
    return searchWidget.charTyped(chr, keyCode)
        || detailPanel.nameField.charTyped(chr, keyCode)
        || detailPanel.xField.charTyped(chr, keyCode)
        || detailPanel.yField.charTyped(chr, keyCode)
        || detailPanel.zField.charTyped(chr, keyCode);
  }

  @Override
  public void close() {
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
      client.setScreen(parent);
    } else {
      super.close();
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
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    detailPanel.render(context);

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
