package com.googlecode.playnquake.core;

import java.nio.ByteBuffer;

import playn.core.util.Callback;

import com.googlecode.playnquake.core.tools.AsyncFilesystem;
import com.googlecode.playnquake.core.tools.AsyncFilesystem.Entry;
import com.googlecode.playnquake.core.tools.CountingCallback;
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
  }

  
  final Callback<Void> readyCallback = new Callback<Void>() {
    @Override
    public void onSuccess(Void result) {
      processed();
    }
    @Override
    public void onFailure(Throwable cause) {
      error("Error processing files", cause);
    }
  };
  CountingCallback countingCallback = new CountingCallback(readyCallback);

  final Callback<Entry> processor = new Callback<Entry>() {
    @Override
    public void onSuccess(Entry result) {
      tools.println("Processing: " + result.fullPath);
      if (result.directory) {
        afs.processFiles(result.fullPath, processor, countingCallback.addAccess());
      }
    }
    @Override
    public void onFailure(Throwable cause) {
      tools.println("Error: " + cause);
    }
  };
  
  
  void unpacked() {
    afs.processFiles("", processor, countingCallback.addAccess());
  }

  void processed() {
    tools.println("All files processed!");
  }
}
