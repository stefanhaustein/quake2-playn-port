package com.googlecode.playnquake.core;

import com.googlecode.gwtquake.shared.common.ConsoleVariables;
import com.googlecode.gwtquake.shared.common.Globals;
import com.googlecode.gwtquake.shared.common.QuakeCommon;
import com.googlecode.gwtquake.shared.render.GlRenderer;
import com.googlecode.gwtquake.shared.render.Image;
import com.googlecode.gwtquake.shared.sys.KBD;
import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.PakFile;
import com.googlecode.playnquake.core.tools.Tools;

import java.nio.ByteBuffer;
import playn.core.Game;
import playn.core.PlayN;
import playn.core.util.Callback;

public class PlayNQuake implements Game {

  private static Tools tools;
  private boolean initialized;

  public static Tools tools() {
    return tools;
  }

  public PlayNQuake(Tools tools) {
    PlayNQuake.tools = tools;
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
    Globals.re = new GlRenderer(PlayN.graphics().screenWidth(),
        PlayN.graphics().screenHeight());
    QuakeCommon.Init(new String[] { "GQuake" });
    Globals.nostdout = ConsoleVariables.Get("nostdout", "0", 0);
    initialized = true;
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
