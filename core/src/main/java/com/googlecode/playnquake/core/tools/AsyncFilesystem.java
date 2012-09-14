package com.googlecode.playnquake.core.tools;

import java.nio.ByteBuffer;

import playn.core.util.Callback;

/**
 * Special-purpose async filesystem interface for unpacking 
 * @author haustein
 *
 */
public interface AsyncFilesystem {

  public static class Entry {
    public boolean directory;
    public String name;
    public String fullPath;
  }
  
  void getFile(String filename, Callback<ByteBuffer> callback);
  
  void saveFile(String filename, ByteBuffer data, int offet, int len, Callback<Void> callback);

  void processFiles(String dirName, Callback<Entry> processCallback, Callback<Void> readyCallback);
}
