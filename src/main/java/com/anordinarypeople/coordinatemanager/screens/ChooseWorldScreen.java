package com.anordinarypeople.coordinatemanager.screens;

import com.anordinarypeople.coordinatemanager.utils.BaseContainerScreen;
import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.chooseworld.ChooseWorldPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

@Environment(EnvType.CLIENT)
public class ChooseWorldScreen extends BaseContainerScreen {
  public final WorldNameScreen parent;
  private final Btn button = new Btn(0, 0, 0, inputHeight);
  private ChooseWorldPanel panel;
  private EditBox input;
  private Button submit;
  public String selected;

  public ChooseWorldScreen(WorldNameScreen parent) {
    super("world_name.select_world");
    this.parent = parent;
  }

  private void renderInput() {
    input = new EditBox(
        font,
        padding,
        padding,
        width - padding * 2,
        inputHeight,
        Component.empty());
    input.setHint(Component.translatable("world_name.choose_input").setStyle(Style.EMPTY.withItalic(true)));
    input.setResponder(text -> submit.active = text != null && !text.isBlank());

    addRenderableWidget(input);
  }

  private void renderPanel() {
    panel = new ChooseWorldPanel(
        minecraft,
        width - padding * 2,
        height - inputHeight - footerHeight - padding * 3 - 2,
        padding,
        padding * 2 + 2 + inputHeight,
        15,
        parent.availableWorlds,
        str -> {
          selected = str;
          submit.active = true;
        },
        this);

    panel.showEntries();
    addRenderableWidget(panel);
  }

  private void renderSubmitBtn() {
    button.x = padding;
    submit = button.widget("world_name.submit_choose", "world_name.submit_tooltip", b -> onSubmit());
    submit.active = false;

    addRenderableWidget(submit);
  }

  private void renderBackBtn() {
    button.x = padding * 2 + button.width;

    addRenderableWidget(button.widget("history.back", b -> onClose()));
  }

  private void onSubmit() {
    String text = input.getValue();
    parent.onSubmit(text != null && !text.isBlank() ? text : selected);
    onClose();
  }

  @Override
  public void onClose() {
    parent.selected = null;
    minecraft.setScreenAndShow(parent);
  }

  @Override
  public void init() {
    renderInput();
    renderPanel();

    int footerY = height - footerHeight;
    button.y = footerY + (footerHeight - inputHeight) / 2;
    button.width = (width - padding * 3) / 2;

    renderSubmitBtn();
    renderBackBtn();
  }
}
