package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.anordinarypeople.coordinatemanager.data.Const;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class DynamicImage {
  private static final Console logger = new Console("dynamic-image");
  private static final float z = 0.0F;
  private static final Map<String, Identifier> cache = new ConcurrentHashMap<>();

  private static Identifier get(Minecraft client, @Nullable String path) {
    if (path != null) {
      File file = new File(path);
      String cacheKey = file.getAbsolutePath();

      if (cache.containsKey(cacheKey)) {
        return cache.get(cacheKey);
      }

      if (file.exists()) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
          String name = file.getName();
          NativeImage image = NativeImage.read(inputStream);
          DynamicTexture texture = new DynamicTexture(() -> name, image);
          Identifier dynamicId = Identifier.fromNamespaceAndPath(Const.MOD_ID, name);

          client.getTextureManager().register(dynamicId, texture);
          cache.put(cacheKey, dynamicId);

          return dynamicId;
        } catch (IOException e) {
          logger.errorFile("stream image", file, e);
        }
      }
    }

    String fallbackKey = "icon.png";

    if (!cache.containsKey(fallbackKey)) {
      Identifier fallbackId = Identifier.fromNamespaceAndPath(Const.MOD_ID, fallbackKey);
      cache.put(fallbackKey, fallbackId);

      return fallbackId;
    }

    return cache.get(fallbackKey);
  }

  public static void render(GuiGraphicsExtractor context, Minecraft client, String path, int x, int y, int size) {
    context.blit(RenderPipelines.GUI_TEXTURED, get(client, path), x, y, z, z, size, size, size, size);
  }
}
