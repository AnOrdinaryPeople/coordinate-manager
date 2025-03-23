package com.anordinarypeople.coordinatemanager.utils;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class BufferHelper {
  private final Console logger = new Console("buffer-helper");
  private Tessellator tessellator;
  private BuiltBuffer built;
  private BufferBuilder builder;

  public BufferHelper() {
    tessellator = Tessellator.getInstance();
  }

  public BufferBuilder begin() {
    return builder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
  }

  public void render() {
    try {
      built = builder.end();
      BufferRenderer.drawWithGlobalProgram(built);
      built.close();
    } catch (Exception e) {
      logger.error("Failed to render buffer or release resources", e);
    }
  }
}
