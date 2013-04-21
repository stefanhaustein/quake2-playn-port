package com.googlecode.playnquake.core.tools;

import java.nio.ByteBuffer;

import playn.core.CanvasImage;
import playn.core.Image;

public interface Tools {
  AsyncFilesystem getFileSystem();
  ByteBuffer convertToPng(CanvasImage image);
  void println(String text);
  float intBitsToFloat(int i);
  int floatToIntBits(float f);
  void prparationsDone();
  void exit(int i);
}
