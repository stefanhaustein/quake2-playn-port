package com.googlecode.playnquake.core.tools;

import java.nio.ByteBuffer;

import playn.core.Image;
import playn.core.util.Callback;

/**
 * Special-purpose async filesystem interface for unpacking 
 * @author haustein
 *
 */
public interface AsyncFilesystem {

  void getFile(String filename, Callback<ByteBuffer> callback);
  
  void getDirectory(String filename, Callback<String[]> callback);
  
  void saveFile(String filename, ByteBuffer data, int offet, int len, Callback<Void> callback);
}
