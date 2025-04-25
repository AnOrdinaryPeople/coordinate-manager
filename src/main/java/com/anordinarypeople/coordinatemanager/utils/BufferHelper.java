package com.anordinarypeople.coordinatemanager.utils;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.joml.Matrix4f;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ColorHelper;

public class BufferHelper {
  private final Console logger = new Console("buffer-helper");
  private final MinecraftClient client;
  private final String label;
  private RenderPipeline pipeline;
  private BufferAllocator alloc;
  private BufferBuilder builder;

  public BufferHelper(MinecraftClient mcClient, String labelGetter) {
    client = mcClient;
    label = labelGetter;
    pipeline = RenderPipelines.GUI;
  }

  private BufferBuilder begin() {
    try {
      alloc = new BufferAllocator(pipeline.getVertexFormat().getVertexSize() * 4);
      builder = new BufferBuilder(alloc, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

    return builder;
  }

  private void render() {
    try (BuiltBuffer built = builder.endNullable()) {
      if (built == null) {
        alloc.close();
        return;
      }
      Framebuffer framebuffer = client.getFramebuffer();
      RenderSystem.ShapeIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
      VertexFormat.IndexType indexType = indexBuffer.getIndexType();
      GpuBuffer gpuIndexBuffer = indexBuffer.getIndexBuffer(built.getDrawParameters().indexCount());
      GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(
          () -> label, BufferType.VERTICES,
          BufferUsage.DYNAMIC_WRITE,
          built.getBuffer().remaining());
      RenderSystem.getDevice().createCommandEncoder().writeToBuffer(vertexBuffer, built.getBuffer(), 0);

      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
          framebuffer.getColorAttachment(),
          OptionalInt.empty(),
          framebuffer.getDepthAttachment(),
          OptionalDouble.empty())) {
        renderPass.setPipeline(pipeline);
        renderPass.setVertexBuffer(0, vertexBuffer);
        renderPass.setIndexBuffer(gpuIndexBuffer, indexType);
        renderPass.drawIndexed(0, built.getDrawParameters().indexCount());
      }
    }
  }

  public void drawSelected(
      DrawContext context,
      boolean isFocused,
      int rowLeft,
      int rowWidth,
      int entryLeft,
      int entryTop,
      int entryHeight,
      float z,
      int bgColor) {
    final int selectionRight = rowLeft + rowWidth + 2;
    final float opacity = isFocused ? 1.0F : 0.5F;
    final Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
    final int borderColor = ColorHelper.fromFloats(1.0F, opacity, opacity, opacity);
    builder = begin();
    builder.vertex(matrix, entryLeft, entryTop + entryHeight + 2, z).color(borderColor);
    builder.vertex(matrix, selectionRight, entryTop + entryHeight + 2, z).color(borderColor);
    builder.vertex(matrix, selectionRight, entryTop - 2, z).color(borderColor);
    builder.vertex(matrix, entryLeft, entryTop - 2, z).color(borderColor);
    builder.vertex(matrix, entryLeft + 1, entryTop + entryHeight + 1, z).color(bgColor);
    builder.vertex(matrix, selectionRight - 1, entryTop + entryHeight + 1, z).color(bgColor);
    builder.vertex(matrix, selectionRight - 1, entryTop - 1, z).color(bgColor);
    builder.vertex(matrix, entryLeft + 1, entryTop - 1, z).color(bgColor);
    render();
  }
}
