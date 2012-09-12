package com.googlecode.playnquake.core.tools;

import java.nio.ByteBuffer;

import playn.core.Image;

public interface Tools {
  AsyncFilesystem getFileSystem();
  ByteBuffer convertToPng(Image image);
  void println(String text);
  
}
