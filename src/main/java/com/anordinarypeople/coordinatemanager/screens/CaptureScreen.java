package com.anordinarypeople.coordinatemanager.screens;

import com.anordinarypeople.coordinatemanager.CaptureHandler;
import com.anordinarypeople.coordinatemanager.widgets.Btn;
import com.anordinarypeople.coordinatemanager.widgets.TextField;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class CaptureScreen extends Screen {
  private final CaptureHandler handler;
  private final int padding = 5;
  private final int inputHeight = 20;
  private final int buttonHeight = 20;
  private final int footerHeight = 25;
  private final TextField textField = new TextField(null, 0, 0, 0, inputHeight);
  private final Btn button = new Btn(0, 0, 0, buttonHeight);
  private EditBox nameField;
  private EditBox xField;
  private EditBox yField;
  private EditBox zField;
  private int fieldWidth;
  private boolean isSaved = false;

  public CaptureScreen(CaptureHandler handler) {
    super(Component.translatable("capture.title"));

    this.handler = handler;
  }

  private void renderSubmitBtn() {
    button.x = padding;

    addRenderableWidget(button.widget(
        "capture.submit",
        b -> {
          String name = nameField.getValue();
          isSaved = true;

          handler.name = name.isBlank() || name.isEmpty() ? null : name;
          handler.x = getNumber(xField);
          handler.y = getNumber(yField);
          handler.z = getNumber(zField);

          handler.saveCoordinate();
          onClose();
        }));
  }

  private void renderBackBtn() {
    button.x = padding * 2 + button.width;

    addRenderableWidget(button.widget(
        "history.back",
        b -> onClose()));
  }

  private double getNumber(EditBox field) {
    double number;

    try {
      number = Double.valueOf(field.getValue());
    } catch (Exception e) {
      number = 0;
    }

    return number;
  }

  private EditBox renderNameField(String translationKey) {
    textField.x = (width / 2) - fieldWidth / 2;
    textField.y = padding;
    addRenderableWidget(textField.label(translationKey));

    textField.y = inputHeight + padding;
    EditBox field = textField.input("");
    addRenderableWidget(field);

    return field;
  }

  private EditBox renderInputField(String translationKey, double value) {
    int originalX = textField.x;

    textField.x = textField.x / 2 - padding * 2;
    textField.y += inputHeight + padding;

    addRenderableWidget(textField.label(translationKey));

    textField.x = originalX;
    EditBox field = textField.input(String.valueOf(value));
    addRenderableWidget(field);

    return field;
  }

  @Override
  public void onClose() {
    if (!isSaved) {
      handler.deleteImage();
    }

    super.onClose();
  }

  @Override
  protected void init() {
    fieldWidth = width / 3;
    textField.textRenderer = font;
    textField.width = width / 3;

    nameField = renderNameField("capture.name");
    xField = renderInputField("capture.x", handler.x);
    yField = renderInputField("capture.y", handler.y);
    zField = renderInputField("capture.z", handler.z);

    int footerY = height - footerHeight;
    button.y = footerY + (footerHeight - buttonHeight) / 2;
    button.width = (width - padding * 4) / 2;

    renderSubmitBtn();
    renderBackBtn();
  }
}
