package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class DynamicImage {
  private static final Console logger = new Console("dynamic-image");
  private static final float shaderColor = 1.0F;
  private static final float z = 0.0F;

  private static Identifier get(MinecraftClient client, @Nullable String path) {
    if (path != null) {
      File file = new File(path);

      if (file.exists()) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
          String name = file.getName();
          NativeImage image = NativeImage.read(inputStream);
          NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> name, image);
          Identifier dynamicId = Identifier.of(Const.MOD_ID, name);

          client.getTextureManager().registerTexture(dynamicId, texture);

          return dynamicId;
        } catch (IOException e) {
          logger.errorFile("stream image", file, e);
        }
      }
    }

    return Identifier.of(Const.MOD_ID, "icon.png");
  }

  public static void render(DrawContext context, MinecraftClient client, String path, int x, int y, int size) {
    RenderSystem.setShaderColor(shaderColor, shaderColor, shaderColor, shaderColor);
    GlStateManager._enableBlend();
    context.drawTexture(RenderLayer::getGuiTextured, get(client, path), x, y, z, z, size, size, size, size);
    GlStateManager._disableBlend();
  }
}
