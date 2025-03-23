package com.anordinarypeople.coordinatemanager.data;

public class WorldSearch {
  public String name;
  public String x;
  public String y;
  public String z;

  public WorldSearch(String name, double x, double y, double z) {
    this.name = name == null || name.isBlank() ? "" : name;
    this.x = Double.toString(x);
    this.y = Double.toString(y);
    this.z = Double.toString(z);
  }
}
