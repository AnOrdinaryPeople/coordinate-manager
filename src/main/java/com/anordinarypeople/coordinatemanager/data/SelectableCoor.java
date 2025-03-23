package com.anordinarypeople.coordinatemanager.data;

public class SelectableCoor extends CoordinateData {
  public boolean isSelected = false;

  public SelectableCoor(String name, String dimension, double x, double y, double z) {
    super(name, dimension, x, y, z);
  }
}
