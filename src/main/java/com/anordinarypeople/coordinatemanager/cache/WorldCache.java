package com.anordinarypeople.coordinatemanager.cache;

import java.util.ArrayList;
import java.util.List;

import com.anordinarypeople.coordinatemanager.data.CoordinateData;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.data.WorldSearch;
import com.anordinarypeople.coordinatemanager.utils.Storage;
import com.google.gson.reflect.TypeToken;

public class WorldCache {
  private static Storage<List<WorldData>> storage = new Storage<List<WorldData>>(
      "worlds",
      new ArrayList<>(),
      new TypeToken<List<WorldData>>() {
      }.getType());
  public static int IS_SINGLEPLAYER = 2;
  public static List<WorldData> INSTANCE;

  private static List<WorldSearch> createKeywords() {
    List<WorldSearch> temp = new ArrayList<>(Coordinate.INSTANCE.size());

    for (CoordinateData data : Coordinate.INSTANCE) {
      temp.add(new WorldSearch(data.name, data.x, data.y, data.z));
    }

    return temp;
  }

  private static boolean isSingle(boolean fallback) {
    return IS_SINGLEPLAYER < 2 ? IS_SINGLEPLAYER == 1 : fallback;
  }

  private static void saveWorld(String world) {
    boolean isUpdated = false;
    List<WorldSearch> keywords = createKeywords();

    for (int i = 0; i < INSTANCE.size(); i++) {
      WorldData data = INSTANCE.get(i);

      if (world.equals(data.worldName)) {
        INSTANCE.set(i, new WorldData(world, isSingle(data.isSingleplayer), keywords));
        isUpdated = true;
        break;
      }
    }

    if (!isUpdated) {
      INSTANCE.add(new WorldData(world, isSingle(true), keywords));
    }

    storage.setAll(INSTANCE);
  }

  public static void save() {
    Thread.ofVirtual().start(() -> saveWorld(Coordinate.CURRENT_WORLD));
  }

  public static void save(String world) {
    saveWorld(world);
  }

  public static void directSave() {
    storage.setAll(INSTANCE);
  }

  public static void load() {
    INSTANCE = storage.getAll();
  }
}
