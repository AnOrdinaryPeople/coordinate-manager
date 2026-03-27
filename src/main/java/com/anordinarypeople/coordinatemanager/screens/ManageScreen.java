package com.anordinarypeople.coordinatemanager.screens;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.data.WorldSearch;
import com.anordinarypeople.coordinatemanager.enums.DeleteAllType;
import com.anordinarypeople.coordinatemanager.screens.confirm.ManageDelConfirm;
import com.anordinarypeople.coordinatemanager.utils.TextTrim;
import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.world.WorldPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

@Environment(EnvType.CLIENT)
public class ManageScreen extends Screen {
  public final int inputHeight = 20;
  public final int padding = 5;
  private final Screen parent;
  private final int footerHeight = 25;
  private final Btn button = new Btn(0, 0, 0, inputHeight);
  private WorldPanel worldPanel;
  private EditBox searchWidget;
  private Button deleteButton;
  private Button openWorldButton;
  private String selectedWorld;

  public ManageScreen(Screen parent) {
    super(Component.translatable("management.title"));
    this.parent = parent;
  }

  private void renderSearch() {
    searchWidget = new EditBox(
        font,
        padding,
        padding,
        (int) (width * 0.7) - padding * 2,
        inputHeight, Component.empty());
    searchWidget.setHint(Component.translatable("management.search").setStyle(Style.EMPTY.withItalic(true)));
    searchWidget.setResponder(text -> worldPanel.filter(text));

    addRenderableWidget(searchWidget);
  }

  private void renderConfigBtn() {
    addRenderableWidget(
        Button.builder(
            Component.translatable("management.config"),
            b -> minecraft.setScreen(ConfigScreen.get(this)))
            .bounds((int) (width * 0.7), padding, (int) (width * 0.3) - padding, inputHeight)
            .build());
  }

  private void renderWorld() {
    worldPanel = new WorldPanel(
        minecraft,
        width - padding * 2,
        height - inputHeight - footerHeight - padding * 3 - 2,
        padding,
        padding * 2 + 2 + inputHeight,
        30,
        worldName -> {
          Coordinate.load(worldName);
          selectedWorld = worldName;
          deleteButton.active = true;
          openWorldButton.active = true;
          openWorldButton.setMessage(TextTrim.trim(minecraft.font, Component.literal(worldName), button.width));
          openWorldButton
              .setTooltip(Tooltip.create(Component.translatable("management.open_world.tooltip_selected", worldName)));
        },
        this);

    worldPanel.filter("");
    addRenderableWidget(worldPanel);
  }

  private void renderOpenWorldBtn() {
    button.x = padding;

    openWorldButton = button.widget(
        "management.open_world",
        "management.open_world.tooltip",
        b -> minecraft.setScreen(new HistoryScreen(this)));
    openWorldButton.active = false;

    addRenderableWidget(openWorldButton);
  }

  private void renderDeleteBtn() {
    button.x = padding * 2 + button.width;

    deleteButton = button.widget(
        "management.delete",
        "management.delete.tooltip",
        b -> minecraft.setScreen(new ManageDelConfirm(this, selectedWorld)));
    deleteButton.active = false;

    addRenderableWidget(deleteButton);
  }

  private void renderBackBtn() {
    button.x = padding * 3 + button.width * 2;

    addRenderableWidget(button.widget("history.back", b -> onClose()));
  }

  private void virtualDeleteImages(ArrayList<String> paths) {
    Thread.ofVirtual().start(() -> {
      for (String path : paths) {
        File file = new File(path);

        if (file.exists()) {
          file.delete();
        }
      }
    });
  }

  public void onDeleteConfirmed(DeleteAllType confirmType) {
    int size = Coordinate.INSTANCE.size();
    boolean isUnfavorite = confirmType == DeleteAllType.UNFAVORITE;
    ArrayList<String> paths = new ArrayList<>(size);
    List<WorldSearch> keepKeywords = new ArrayList<>(size);

    Coordinate.INSTANCE.removeIf(data -> {
      boolean isDelete = true;

      if (isUnfavorite && data.isPinned) {
        keepKeywords.add(new WorldSearch(data.name != null ? data.name : "", data.x, data.y, data.z));
        isDelete = false;
      }

      if (isDelete && data.imagePath != null) {
        paths.add(data.imagePath);
      }

      return isDelete;
    });

    if (isUnfavorite && Coordinate.INSTANCE.size() > 0) {
      for (WorldData data : WorldCache.INSTANCE) {
        if (data.worldName.equals(selectedWorld)) {
          data.keywords = keepKeywords;
          break;
        }
      }
      Coordinate.save();
    } else {
      WorldCache.INSTANCE.removeIf((data) -> data.worldName.equals(selectedWorld));
      Coordinate.JSON_FILE.delete();
    }

    WorldCache.directSave();
    virtualDeleteImages(paths);
  }

  public void refreshList() {
    searchWidget.setValue("");
    worldPanel.filter("");
  }

  @Override
  public boolean keyPressed(KeyEvent keyInput) {
    return super.keyPressed(keyInput)
        || searchWidget.keyPressed(keyInput);
  }

  @Override
  public boolean charTyped(CharacterEvent chr) {
    return searchWidget.charTyped(chr);
  }

  @Override
  public void onClose() {
    IntegratedServer server = minecraft.getSingleplayerServer();

    if (server != null) {
      String world = server.getWorldData().getLevelName();

      if (!world.equals(Coordinate.CURRENT_WORLD)) {
        Coordinate.load(world);
      }
    }

    minecraft.setScreen(parent);
  }

  @Override
  protected void init() {
    renderSearch();
    renderConfigBtn();
    renderWorld();

    int footerY = height - footerHeight;
    button.y = footerY + (footerHeight - inputHeight) / 2;
    button.width = (width - padding * 4) / 3;

    renderOpenWorldBtn();
    renderDeleteBtn();
    renderBackBtn();
  }
}
