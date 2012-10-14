package com.googlecode.playnquake.core.tools;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import playn.core.Image;

public interface Tools {
  AsyncFilesystem getFileSystem();
  ByteBuffer convertToPng(Image image);
  void println(String text);
  float intBitsToFloat(int i);
  int floatToIntBits(float f);
}
