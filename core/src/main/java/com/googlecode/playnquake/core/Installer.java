package com.googlecode.playnquake.core;

import java.nio.ByteBuffer;

import playn.core.util.Callback;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.PakFile;
import com.googlecode.playnquake.core.tools.Tools;

public class Installer {
  
  Tools tools;
  AsyncFilesystem afs;
  Callback<Void> doneCallback;
  
  Installer(Tools tools, Callback<Void> doneCallback) {
    this.tools = tools;
    this.afs = tools.getFileSystem();
    this.doneCallback = doneCallback;
  }
  
  void error(String msg, Throwable cause) {
    tools.println(msg);
    doneCallback.onFailure(cause);
  }
  
  void run() {
    tools.println("Unpacking pak0.pak");
    afs.getFile("Install/Data/baseq2/pak0.pak", new Callback<ByteBuffer>() {

      @Override
      public void onSuccess(ByteBuffer result) {
        new PakFile(result).unpack(tools, 
            new Callback<Void>() {

              @Override
              public void onSuccess(Void result) {
                tools.println("pak0.pak successfully unpacked.");
                unpacked();
              }

              @Override
              public void onFailure(Throwable cause) {
                error("Error unpacking pak0.pak", cause);
              }
            });
        }

      @Override
      public void onFailure(Throwable cause) {
        error("Error accessing pak0.pak", cause);
      }
    });

    afs.getDirectory(".", new Callback<String[]>() {

      @Override
      public void onSuccess(String[] result) {
        for (String f: result) {
          tools.println(f);
        }
      }

      @Override
      public void onFailure(Throwable cause) {
        tools.println("Error: " + cause);
      }
    });
  }

  void unpacked() {
  // 
  }

}
