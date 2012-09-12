package com.googlecode.playnquake.core;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.PakFile;
import com.googlecode.playnquake.core.tools.Tools;

import java.nio.ByteBuffer;
import playn.core.Game;
import playn.core.util.Callback;

public class PlayNQuake implements Game {

  Tools tools;
  AsyncFilesystem afs;
  
  public PlayNQuake(Tools tools) {
    this.tools = tools;
    this.afs = tools.getFileSystem();
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
      }

      @Override
      public void onFailure(Throwable cause) {
        error("Error installing files", cause);
      }
      
    });
    installer.run();
  }
  
  
  @Override
  public void update(float delta) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void paint(float alpha) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int updateRate() {
    // TODO Auto-generated method stub
    return 0;
  }

}
