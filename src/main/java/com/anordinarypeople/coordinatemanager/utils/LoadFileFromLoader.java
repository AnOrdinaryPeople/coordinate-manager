package com.anordinarypeople.coordinatemanager.utils;

import java.io.File;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class LoadFileFromLoader {
  public static Path getDir() {
    return FabricLoader.getInstance().getConfigDir();
  }

  public static File fabric(String child) {
    return new File(getDir().toFile(), child);
  }
}
