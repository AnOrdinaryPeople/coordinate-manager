package com.anordinarypeople.coordinatemanager.screens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.anordinarypeople.coordinatemanager.cache.Coordinate;
import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.Const;
import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.data.EmptyWorld;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.data.WorldSearch;
import com.anordinarypeople.coordinatemanager.utils.BaseContainerScreen;
import com.anordinarypeople.coordinatemanager.utils.Console;
import com.anordinarypeople.coordinatemanager.utils.LoadFileFromLoader;
import com.anordinarypeople.coordinatemanager.utils.TextTrim;
import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.TextField;
import com.anordinarypeople.coordinatemanager.widgets.container.ContainerPanel;
import com.anordinarypeople.coordinatemanager.widgets.empty.EmptyWorldPanel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

@Environment(EnvType.CLIENT)
public class WorldNameScreen extends BaseContainerScreen {
  private final Console logger = new Console("WorldNameScreen");
  private final Btn button = new Btn(0, 0, 0, inputHeight);
  private final String coordinatePath = "coordinatemanager/coordinates";
  private final Screen parent;
  private final List<EmptyWorld> data;
  private EmptyWorldPanel leftPanel;
  private ContainerPanel containerPanel;
  private Button selectWidget;
  private Button submit;
  private int totalInputHeight;
  private int leftPanelWidth;
  private int containerWidth;
  private int totalSubmitted = 0;
  private boolean hasRendered = false;
  private boolean chooseWorldInitiated = false;
  private boolean submitRerender = false;
  public ArrayList<String> availableWorlds = new ArrayList<>();
  public EmptyWorld selected;

  public WorldNameScreen(Screen parent, List<EmptyWorld> data) {
    super("world_name.title");
    this.parent = parent;
    this.data = data;
  }

  private void initWorlds() {
    if (chooseWorldInitiated) {
      return;
    }

    if (availableWorlds.size() > 0) {
      availableWorlds.removeIf(world -> {
        boolean bool = false;

        for (EmptyWorld empty : data) {
          if (empty.name == world) {
            bool = true;
            break;
          }
        }

        return bool;
      });
      return;
    }

    Path coordinates = LoadFileFromLoader.getDir().resolve(coordinatePath);
    availableWorlds = new ArrayList<>(WorldCache.INSTANCE.size());
    chooseWorldInitiated = true;

    try (DirectoryStream<Path> paths = Files.newDirectoryStream(coordinates)) {
      for (Path path : paths) {
        String fileName = path.getFileName().toString().replaceFirst("\\.json$", "");
        boolean matched = false;

        for (WorldData world : WorldCache.INSTANCE) {
          if (fileName.equals(
              world.worldName != null
                  ? Coordinate.sanitize(world.worldName)
                  : null)) {
            matched = true;
            break;
          }
        }

        if (!matched) {
          for (EmptyWorld empty : data) {
            if (empty.name == null) {
              availableWorlds.add(fileName);
              break;
            }
          }
        }
      }
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  private void renderTitle() {
    int height = 15;
    TextField desc1 = new TextField(font, padding, padding, width, height);
    TextField desc2 = new TextField(font, padding, padding + height, width, height);
    totalInputHeight = height * 2;

    desc1.text = Component.translatable("world_name.description").setStyle(Style.EMPTY.withColor(Const.YELLOW));

    addRenderableWidget(desc1.label());
    addRenderableWidget(desc2.label("world_name.description_2"));
  }

  private void renderList() {
    leftPanel = new EmptyWorldPanel(
        minecraft,
        leftPanelWidth,
        height - totalInputHeight - footerHeight - padding * 3 - 2,
        padding,
        padding * 2 + 2 + totalInputHeight,
        15,
        data,
        data -> {
          selected = data;
          selectWidget.setMessage(data.name != null
              ? trim(Component.literal(data.name))
              : Component.translatable("world_name.select"));
          containerPanel.list = data.data;
          hasRendered = false;
        },
        this);

    leftPanel.showEntries();
    addRenderableWidget(leftPanel);
  }

  private void renderSelect() {
    int x = leftPanelWidth + padding * 2;
    int y = padding * 2 + totalInputHeight;
    Btn button = new Btn(x, y, containerWidth, inputHeight);

    selectWidget = button.widget("world_name.select", b -> onSelect());
    selectWidget.active = false;

    addRenderableWidget(selectWidget);
  }

  private void renderContainer() {
    containerPanel = new ContainerPanel(
        minecraft,
        containerWidth,
        height - totalInputHeight - inputHeight - footerHeight - padding * 4 - 2,
        leftPanelWidth + padding * 2,
        padding * 3 + 2 + totalInputHeight + inputHeight,
        30,
        null,
        this,
        false);

    containerPanel.filter("");
    addRenderableWidget(containerPanel);
  }

  private void renderSubmitBtn() {
    button.x = padding;

    submit = Button.builder(submitText(), b -> onSubmitWorlds())
        .bounds(button.x, button.y, button.width, button.height)
        .build();
    submit.active = false;

    addRenderableWidget(submit);
  }

  private void renderBackBtn() {
    button.x = padding * 2 + button.width;

    addRenderableWidget(button.widget("history.back", b -> onClose()));
  }

  private MutableComponent trim(Component text) {
    return TextTrim.trim(font, text, containerWidth - padding * 2);
  }

  private MutableComponent submitText() {
    int size = data.size();

    if (size != totalSubmitted) {
      int temp = size - totalSubmitted;
      return Component.translatable("world_name.submit_more", temp > 99 ? "99+" : temp);
    }

    return Component.translatable("world_name.submit");
  }

  private void onSelect() {
    initWorlds();
    minecraft.setScreen(new ChooseWorldScreen(this));
  }

  public void onSubmit(String worldName) {
    chooseWorldInitiated = false;

    for (EmptyWorld empty : data) {
      if (selected.index == empty.index) {
        empty.name = worldName;
        totalSubmitted++;
        break;
      }
    }

    submitRerender = totalSubmitted == data.size();
    submit.setMessage(submitText());
  }

  private void onSubmitWorlds() {
    Gson gson = new GsonBuilder().serializeNulls().create();

    for (EmptyWorld empty : data) {
      File file = LoadFileFromLoader.fabric(String.format(
          "%s/%s.json",
          coordinatePath,
          Coordinate.sanitize(empty.name)));

      if (!file.exists()) {
        ArrayList<CoordinateData> coordinates = new ArrayList<>(empty.data.size());

        for (SelectableCoor coordinate : empty.data) {
          coordinates.add(new CoordinateData(coordinate.name, "", coordinate.x, coordinate.y, coordinate.z));
        }

        try {
          file.getParentFile().mkdirs();
          file.createNewFile();

          try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(coordinates, writer);
          }
        } catch (IOException e) {
          logger.errorFile("write JSON", file, e);
        }
      }
      int idx = 0;

      for (WorldData world : WorldCache.INSTANCE) {
        if (idx == empty.index) {
          ArrayList<WorldSearch> keywords = new ArrayList<>(empty.data.size());
          world.worldName = empty.name;

          for (SelectableCoor coordinate : empty.data) {
            keywords.add(new WorldSearch(coordinate.name, coordinate.x, coordinate.y, coordinate.z));
          }

          world.keywords = keywords;
          break;
        }

        idx++;
      }
    }
    File file = LoadFileFromLoader.fabric(String.format("%s/%s.json", "coordinatemanager", "worlds"));

    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(WorldCache.INSTANCE, writer);
    } catch (IOException e) {
      logger.errorFile("write JSON", file, e);
    }
    WorldCache.load();
    minecraft.setScreen(new ManageScreen(parent));
  }

  @Override
  public void onClose() {
    minecraft.setScreen(parent);
  }

  @Override
  protected void init() {
    leftPanelWidth = (int) (width * 0.3) - padding;
    containerWidth = (int) (width * 0.7) - padding * 2;

    renderTitle();
    renderContainer();
    renderSelect();
    renderList();

    int footerY = height - footerHeight;
    button.y = footerY + (footerHeight - inputHeight) / 2;
    button.width = (width - padding * 3) / 2;

    renderSubmitBtn();
    renderBackBtn();
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    super.extractRenderState(context, mouseX, mouseY, delta);

    if (selected != null && !hasRendered) {
      hasRendered = true;
      selectWidget.active = true;
      containerPanel.filter("");
    }

    if (submitRerender) {
      submitRerender = false;
      submit.active = true;
    }
  }
}
