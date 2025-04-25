package com.anordinarypeople.coordinatemanager.screens;

import com.anordinarypeople.coordinatemanager.utils.BaseContainerScreen;
import com.anordinarypeople.coordinatemanager.widgets.Button;
import com.anordinarypeople.coordinatemanager.widgets.chooseworld.ChooseWorldPanel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ChooseWorldScreen extends BaseContainerScreen {
  public final WorldNameScreen parent;
  private final Button button = new Button(0, 0, 0, inputHeight);
  private ChooseWorldPanel panel;
  private TextFieldWidget input;
  private ButtonWidget submit;
  public String selected;

  public ChooseWorldScreen(WorldNameScreen parent) {
    super("world_name.select_world");
    this.parent = parent;
  }

  private void renderInput() {
    input = new TextFieldWidget(
        textRenderer,
        padding,
        padding,
        width - padding * 2,
        inputHeight,
        Text.empty());
    input.setPlaceholder(Text.translatable("world_name.choose_input").setStyle(Style.EMPTY.withItalic(true)));
    input.setChangedListener(text -> submit.active = text != null && !text.isBlank());

    addDrawableChild(input);
  }

  private void renderPanel() {
    panel = new ChooseWorldPanel(
        client,
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
    addDrawableChild(panel);
  }

  private void renderSubmitBtn() {
    button.x = padding;
    submit = button.widget("world_name.submit_choose", "world_name.submit_tooltip", b -> onSubmit());
    submit.active = false;

    addDrawableChild(submit);
  }

  private void renderBackBtn() {
    button.x = padding * 2 + button.width;

    addDrawableChild(button.widget("history.back", b -> close()));
  }

  private void onSubmit() {
    String text = input.getText();
    parent.onSubmit(text != null && !text.isBlank() ? text : selected);
    close();
  }

  @Override
  public void close() {
    parent.selected = null;
    client.setScreen(parent);
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
