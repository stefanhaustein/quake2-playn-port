package com.googlecode.playnquake.core;

import com.googlecode.gwtquake.shared.client.Dimension;
import com.googlecode.gwtquake.shared.common.ConsoleVariables;
import com.googlecode.gwtquake.shared.common.Globals;
import com.googlecode.gwtquake.shared.common.QuakeCommon;
import com.googlecode.gwtquake.shared.render.GlRenderer;
import com.googlecode.gwtquake.shared.sound.DummyDriver;
import com.googlecode.gwtquake.shared.sound.Sound;
import com.googlecode.playnquake.core.tools.Tools;

import java.util.HashMap;
import java.util.Map;

import playn.core.Game;
import playn.core.PlayN;
import playn.core.util.Callback;
import playn.gl11emulation.GL11Emulation;

public class PlayNQuake implements Game {

  private static Tools tools;
  private static Map<String,Dimension> imageSizes = new HashMap<String,Dimension>();
  private boolean initialized;

  public static Tools tools() {
    return tools;
  }

  public PlayNQuake(Tools tools) {
    PlayNQuake.tools = tools;
  }

  public static Dimension getImageSize(String name) {
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    return imageSizes.get(name);
  }
  
  
  void error(String msg, Throwable cause) {
    tools.println(msg + ": " + cause.toString());
  }
  
  @Override
  public void init() {
    Installer installer = new Installer(tools, new Callback<Void>() {
      @Override
      public void onSuccess(Void result) {
        tools.println("All files successfully installed and converted");
        initGame();
      }

      @Override
      public void onFailure(Throwable cause) {
        error("Error installing files", cause);
      }
      
    });
    installer.run();
  }
  
  /**
   * Game initialization, after resources are loaded / converted.
   */
  void initGame() {
    loadImageSizes();
    
    Globals.re = new GlRenderer(
        new GL11Emulation(PlayN.graphics().gl20()),
        PlayN.graphics().screenWidth(),
        PlayN.graphics().screenHeight());
    
    Sound.impl = new DummyDriver();
    
    QuakeCommon.Init(new String[] { "GQuake" });
    Globals.nostdout = ConsoleVariables.Get("nostdout", "0", 0);
    
    initialized = true;
  }

  void loadImageSizes() {
    String all = PlayN.storage().getItem("imageSizes");
    for(String line: all.split("\n")) {
      String[] parts = line.split(",");
      if (parts.length > 0) {
        imageSizes.put(parts[0], new Dimension(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
      }
    }
    
    System.out.println("" + imageSizes);
  }
  
  
  @Override
  public void update(float delta) {
   
  }

  @Override
  public void paint(float alpha) {
    if (!initialized) {
      return;
    }
    QuakeCommon.Frame((int) alpha);
  }

  @Override
  public int updateRate() {
    // TODO Auto-generated method stub
    return 0;
  }

}
