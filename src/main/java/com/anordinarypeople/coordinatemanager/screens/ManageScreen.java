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
import com.anordinarypeople.coordinatemanager.widgets.Button;
import com.anordinarypeople.coordinatemanager.widgets.world.WorldPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ManageScreen extends Screen {
  public final int inputHeight = 20;
  public final int padding = 5;
  private final Screen parent;
  private final int footerHeight = 25;
  private final Button button = new Button(0, 0, 0, inputHeight);
  private WorldPanel worldPanel;
  private TextFieldWidget searchWidget;
  private ButtonWidget deleteButton;
  private ButtonWidget openWorldButton;
  private String selectedWorld;

  public ManageScreen(Screen parent) {
    super(Text.translatable("management.title"));
    this.parent = parent;
    WorldCache.load();
  }

  private void renderSearch() {
    searchWidget = new TextFieldWidget(
        textRenderer,
        padding,
        padding,
        (int) (width * 0.7) - padding * 2,
        inputHeight, Text.empty());
    searchWidget.setPlaceholder(Text.translatable("management.search").setStyle(Style.EMPTY.withItalic(true)));
    searchWidget.setChangedListener(text -> worldPanel.filter(text));

    addDrawableChild(searchWidget);
  }

  private void renderConfigBtn() {
    addDrawableChild(
        ButtonWidget.builder(
            Text.translatable("management.config"),
            b -> client.setScreen(ConfigScreen.get(this)))
            .dimensions((int) (width * 0.7), padding, (int) (width * 0.3) - padding, inputHeight)
            .build());
  }

  private void renderWorld() {
    worldPanel = new WorldPanel(
        client,
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
          openWorldButton.setMessage(TextTrim.trim(client.textRenderer, Text.literal(worldName), button.width));
          openWorldButton
              .setTooltip(Tooltip.of(Text.translatable("management.open_world.tooltip_selected", worldName)));
        },
        this);

    worldPanel.filter("");
    addDrawableChild(worldPanel);
  }

  private void renderOpenWorldBtn() {
    button.x = padding;

    openWorldButton = button.widget(
        "management.open_world",
        "management.open_world.tooltip",
        b -> client.setScreen(new HistoryScreen(this)));
    openWorldButton.active = false;

    addDrawableChild(openWorldButton);
  }

  private void renderDeleteBtn() {
    button.x = padding * 2 + button.width;

    deleteButton = button.widget(
        "management.delete",
        "management.delete.tooltip",
        b -> client.setScreen(new ManageDelConfirm(this, selectedWorld)));
    deleteButton.active = false;

    addDrawableChild(deleteButton);
  }

  private void renderBackBtn() {
    button.x = padding * 3 + button.width * 2;

    addDrawableChild(button.widget("history.back", b -> close()));
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

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return super.keyPressed(keyCode, scanCode, modifiers)
        || searchWidget.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int keyCode) {
    return searchWidget.charTyped(chr, keyCode);
  }

  @Override
  public void close() {
    IntegratedServer server = MinecraftClient.getInstance().getServer();

    if (server != null) {
      String world = server.getSaveProperties().getLevelName();

      if (!world.equals(Coordinate.CURRENT_WORLD)) {
        Coordinate.load(world);
      }
    }

    client.setScreen(parent);
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
