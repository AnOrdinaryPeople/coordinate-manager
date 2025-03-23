package com.anordinarypeople.coordinatemanager.data;

import java.util.List;

public class WorldData {
  public String worldName;
  public boolean isSingleplayer;
  public List<WorldSearch> keywords;

  public WorldData(String worldName, boolean isSingleplayer, List<WorldSearch> keywords) {
    this.worldName = worldName;
    this.isSingleplayer = isSingleplayer;
    this.keywords = keywords;
  }
}
