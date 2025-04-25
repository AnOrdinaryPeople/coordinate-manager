package com.anordinarypeople.coordinatemanager;

import java.util.ArrayList;

import com.anordinarypeople.coordinatemanager.cache.WorldCache;
import com.anordinarypeople.coordinatemanager.data.EmptyWorld;
import com.anordinarypeople.coordinatemanager.data.ListSelectableCoor;
import com.anordinarypeople.coordinatemanager.data.SelectableCoor;
import com.anordinarypeople.coordinatemanager.data.WorldData;
import com.anordinarypeople.coordinatemanager.data.WorldSearch;
import com.anordinarypeople.coordinatemanager.screens.ManageScreen;
import com.anordinarypeople.coordinatemanager.screens.WorldNameScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModIntegration implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return parent -> {
      ArrayList<EmptyWorld> emptyWorld = new ArrayList<>(WorldCache.INSTANCE.size());
      int index = 0;

      WorldCache.load();

      for (WorldData data : WorldCache.INSTANCE) {
        if (data.worldName == null) {
          ListSelectableCoor list = new ListSelectableCoor(data.keywords.size());

          for (WorldSearch keyword : data.keywords) {
            SelectableCoor temp = new SelectableCoor(
                keyword.name,
                "",
                Double.parseDouble(keyword.x),
                Double.parseDouble(keyword.y),
                Double.parseDouble(keyword.z));

            temp.uuid = "";

            list.add(temp);
          }

          emptyWorld.add(new EmptyWorld(index, list));
        }
        index++;
      }

      if (emptyWorld.size() > 0) {
        return new WorldNameScreen(parent, emptyWorld);
      }

      return new ManageScreen(parent);
    };
  }
}
